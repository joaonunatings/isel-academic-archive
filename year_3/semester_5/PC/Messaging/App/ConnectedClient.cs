using System;
using System.Collections.Concurrent;
using System.IO;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;

namespace App
{
    /*
     * Represents a connected client.
     * Uses two threads:
     *     - A main thread that processes control messages sent to this client, which can be
     *         - A message delivered to the room where the client is.
     *         - A line sent by the remote client.
     *         - An indication the remote client stream has ended.
     *         - An indication to stop handling this client.
     *       This thread should only block for new control messages.
     *     - An auxiliary thread blocked waiting for input from the remote client.
     * 
     * Some of its methods may be used by multiple threads.
     */
    //
    public class ConnectedClient
    {
        private readonly ILogger _logger;

        private readonly TcpClient _tcpClient;
        private readonly RoomSet _rooms;
        private readonly StreamReader _reader;
        private readonly StreamWriter _writer;
        private readonly Task _mainTask;
        private readonly CancellationToken _ct;

        private readonly AsyncQueue<ControlMessage> _controlMessageQueue = new AsyncQueue<ControlMessage>();

        private Room? _currentRoom;
        private Room? _previousRoom; // To stop the race between leaving a room and receiving a message,
                                     // also effective when leaving one room and entering another before
                                     // processing a message from the previous room
        private bool _exiting;

        public string Name { get; }

        public ConnectedClient(string name, TcpClient tcpClient, RoomSet rooms, ILoggerFactory loggerFactory, CancellationToken ct)
        {
            _logger = loggerFactory.CreateLogger<ConnectedClient>();
            Name = name;
            _tcpClient = tcpClient;
            _rooms = rooms;
            tcpClient.NoDelay = true;
            var networkStream = tcpClient.GetStream();
            _reader = new StreamReader(networkStream, Encoding.UTF8);
            _writer = new StreamWriter(networkStream, Encoding.UTF8)
            {
                AutoFlush = true
            };

            _ct = ct;
            _ct.Register(() => {
                _logger.LogInformation("Instructing a client to exit");
                _controlMessageQueue.Add(new ControlMessage.Stop());    //Works like a poison pill
            });
            _mainTask = MainLoop();
        }
        // Sends a message to the client
        public void PostRoomMessage(string message, Room sender)
        {
            _controlMessageQueue.Add(new ControlMessage.RoomMessage(message, sender));
        }

        // Synchronizes with the client termination
        public async Task JoinAsync(){
            await _mainTask;
        }

        private async Task MainLoop(){
            // Start the RemoteReadLoop 
            Task remoteReadTask = RemoteReadLoop();
            try
            {
                while (!_exiting){
                    var cts = new CancellationTokenSource();

                    try{
                        //await readTask;
                        var controlMessage = await _controlMessageQueue.TakeAsync(cts.Token);
                        switch (controlMessage)
                        {
                            case ControlMessage.RoomMessage roomMessage:
                                await WriteToRemoteAsync(roomMessage.Value);
                                break;
                            case ControlMessage.RemoteLine remoteLine:
                                await ExecuteCommand(remoteLine.Value);
                                break;
                            case ControlMessage.RemoteInputEnded:
                                await ClientExit();
                                break;
                            case ControlMessage.Stop:
                                await ServerExit();
                                break;
                            default:
                                _logger.LogWarning("Unknown message {}, ignoring it", controlMessage);
                                break;
                        }
                    }
                    catch (Exception e)
                    {
                        _logger.LogError("Unexpected exception while handling message: {}, ending connection", 
                            e.Message);
                        cts.Cancel();
                        _exiting = true;
                    }
                }
            }
            finally
            {
                _currentRoom?.Leave(this);
                _tcpClient.Close();
                await remoteReadTask;

            }
            _logger.LogInformation("Exiting MainLoop");
        }
        
        private async Task RemoteReadLoop(){
            try{
                while (!_exiting){
                    _previousRoom = _currentRoom;
                    var line = await _reader.ReadLineAsync();
                    if (line == null)
                    {
                        break;
                    }

                    _controlMessageQueue.Add(new ControlMessage.RemoteLine(line));
                }
            }
            catch (Exception e)
            {
                // Expected exception, the only way to stop reading is by throwing an exception closing stream.
                _logger.LogWarning("Exception while waiting for connection read: {} , might have been because the stream was stopped.", e.Message);
            }
            finally
            {
                if (!_exiting)
                {
                    _controlMessageQueue.Add(new ControlMessage.RemoteInputEnded());
                }
            }

            _logger.LogInformation("Exiting ReadLoop");
        }


        private async Task WriteToRemoteAsync(string line)
        {
            await _writer.WriteLineAsync(line);
        }

        private async Task WriteErrorToRemoteAsync(string line) => await WriteToRemoteAsync($"[Error: {line}]");
        private async Task WriteOkToRemoteAsync() => await WriteToRemoteAsync("[OK]");

        private async Task ExecuteCommand(string lineText)
        {
            Line line = Line.Parse(lineText);

            switch (line)
            {
                case Line.InvalidLine invalidLine:
                    await WriteErrorToRemoteAsync(invalidLine.Reason);
                    break;
                case Line.Message message:
                    await PostMessageToRoom(message);
                    break;
                case Line.EnterRoomCommand enterRoomCommand:
                    await EnterRoom(enterRoomCommand);
                    break;
                case Line.LeaveRoomCommand:
                    await LeaveRoom();
                    break;
                case Line.ExitCommand:
                    await ClientExit();
                    break;
                default:
                    await WriteErrorToRemoteAsync("unable to process line");
                    break;
            }
        }

        private async Task PostMessageToRoom(Line.Message message)
        {
            if (_currentRoom == null)
            {
                await WriteErrorToRemoteAsync("Need to be inside a room to post a message");
            }
            else
            {
                if (_currentRoom == _previousRoom){
                    _currentRoom.Post(this, message.Value);
                }
            }
        }

        private async Task EnterRoom(Line.EnterRoomCommand enterRoomCommand)
        {
            _currentRoom?.Leave(this);
            _currentRoom = _rooms.GetOrCreateRoom(enterRoomCommand.Name);
            _currentRoom.Enter(this);
            await WriteOkToRemoteAsync();
        }

        private async Task LeaveRoom()
        {
            if (_currentRoom == null)
            {
                await WriteErrorToRemoteAsync("There is no room to leave from");
            }
            else
            {
                _currentRoom.Leave(this);
                _currentRoom = null;
                await WriteOkToRemoteAsync();
            }
        }

        private async Task ClientExit()
        {
            _currentRoom?.Leave(this);
            _exiting = true;
            await WriteOkToRemoteAsync();
        }

        private async Task ServerExit()
        {
            _currentRoom?.Leave(this);
            _exiting = true;
            await WriteErrorToRemoteAsync("Server is exiting");
        }

        private abstract class ControlMessage
        {
            private ControlMessage()
            {
                // to make the hierarchy closed
            }

            // A message sent by to a room
            public class RoomMessage : ControlMessage
            {
                public Room Sender { get; }
                public string Value { get; }

                public RoomMessage(string value, Room sender)
                {
                    Value = value;
                    Sender = sender;
                }
            }

            // A line sent by the remote client.
            public class RemoteLine : ControlMessage
            {
                public string Value { get; }

                public RemoteLine(string value)
                {
                    Value = value;
                }
            }

            // The information that the remote client stream has ended, probably because the 
            // socket was closed.
            public class RemoteInputEnded : ControlMessage
            {
            }

            // An instruction to stop handling this remote client
            public class Stop : ControlMessage
            {
            }
        }
    }
}