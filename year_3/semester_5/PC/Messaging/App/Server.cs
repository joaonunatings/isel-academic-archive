using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Diagnostics;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;

namespace App
{
    /*
     * Represents a server, listening and handling connections.
     */
    public class Server
    {
        public enum Status
        {
            NotStarted,
            Starting,
            Started,
            Ending,
            Ended
        }

        private readonly ILoggerFactory _loggerFactory;
        private readonly ILogger _logger;

        private readonly RoomSet _rooms = new RoomSet();
        
        // Used when Stop() is called and when Exception thrown in AcceptLoop.
        private readonly CancellationTokenSource _cts = new CancellationTokenSource();
        
        // Helps handling/watching states of server concurrently.
        private readonly object _lock = new();
        
        // Determines if server ended execution.
        private RequestWithoutCancel _statusTask = new RequestWithoutCancel();
        
        // Handles maximum limit of clients connected to the server at the same time.
        private SemaphoreSlim? _clientSemaphore;
        private int _maxClientCount;
        
        private Status _status = Status.NotStarted;
        private TcpListener? _listener;
        private int _nextClientId;
        
        

        public Server(ILoggerFactory loggerFactory)
        {
            _loggerFactory = loggerFactory;
            _logger = loggerFactory.CreateLogger<Server>();
        }

        public Status State => _status;

        public void Start(IPAddress address, int port, int maxClientCount)
        {
            lock (_lock){
                if (_status != Status.NotStarted)
                {
                    // Can be started at most once
                    throw new Exception("Server has already started");
                }

                //Global variables that suffer concurrency.
                _status = Status.Starting;
                _listener = new TcpListener(address, port);
                _clientSemaphore = new SemaphoreSlim(maxClientCount, maxClientCount);
                _listener.Start();
            }
            _logger.LogInformation("Starting");
            _maxClientCount = maxClientCount;
            
            AcceptLoop(_listener);
        }

        public void Stop()
        {
            // Stopping triggers a cancelation and cancelation stops the server,
            // which means theres no need for a Stop after a cancelation.
            if (_cts.IsCancellationRequested){
                return;
            }
            
            


            lock (_lock){
                //If the server didnt start, theres no need for Stopping it
                if (_status == Status.NotStarted)
                {
                    _logger.LogError("Server has not started");
                    throw new Exception("Server has not started");
                }
                
                //Applying all the triggers to ent the server.
                _status = Status.Ending;
                _cts.Cancel();
                _clientSemaphore!.Dispose();
                _listener.Stop();
            }

            _logger.LogInformation("Stop server call ended");
            
        }

        public async Task JoinAsync()
        {
            if (_status == Status.Ended){
                return;
            }
            
            lock (_lock){
                if (_status == Status.NotStarted)
                {
                    _logger.LogError("Server has not started");
                    throw new Exception("Server has not started");
                }
            }

            // Awaiting for this Task responsible for the knowledge of the server having ended it's execution
            await _statusTask.Task;
        }

        private async Task AcceptLoop(TcpListener listener)
        {
            _logger.LogInformation("Accept thread started");
            var clients = new AsyncTaskSynchronizer();

            _status = Status.Started;
            while (_status == Status.Started)
            {
                try
                {
                    // Semaphore client count will always show +1 due to the fact we reserve a unit for an incoming client
                    _logger.LogInformation("Waiting for client");
                    await _clientSemaphore!.WaitAsync(_cts.Token);
                    // Because the blocking call may not react to the listener Close
                    // See https://github.com/dotnet/runtime/issues/24513
                    var tcpClient = await listener.AcceptTcpClientAsync();
                    var clientName = $"client-{_nextClientId++}";
                    _logger.LogInformation("New client accepted '{}' [{}]", clientName, _maxClientCount - _clientSemaphore!.CurrentCount);
                    ClientExitAsync(clients, clients.Add(), new ConnectedClient(clientName, tcpClient, _rooms, _loggerFactory, _cts.Token));
                    
                }
                catch (Exception e)
                {
                    _logger.LogWarning(
                        "Exception caught '{}', which may happen when the listener is closed, continuing...",
                        e.Message);
                    //_cts.Cancel();
                    // continuing...
                }
            }

            _logger.LogInformation("Waiting clients to end before ending accept loop");
            await clients.WaitAsync();
            _status = Status.Ended;
            _statusTask.SetResult(true);
            _logger.LogInformation("All clients finished, exiting...");
            Debug.Assert(clients.Count == 0, "Illegal state: Clients not empty");
        }

       //Method responsible for clearing a client when it ended.
       private async Task ClientExitAsync(AsyncTaskSynchronizer clients,
            LinkedListNode<RequestWithoutCancel> clientNode, ConnectedClient client)
        {
            await client.JoinAsync();
            clients.Remove(clientNode);
            if (!_cts.IsCancellationRequested){
                _clientSemaphore!.Release();
                _logger.LogInformation("Client removed; Remaining=>[{}]", _maxClientCount - _clientSemaphore!.CurrentCount - 1);
            }
            else{
                _logger.LogInformation("Client exited...");
            }
            
        }
    }
}