using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Threading.Tasks;
using App;
using Microsoft.Extensions.Logging;

namespace Tests{
    public class TestClientAsync{
        private readonly ILogger _logger;
        private readonly TcpClient _tcpClient;
        private readonly StreamReader _reader;
        private readonly StreamWriter _writer;

        public TestClientAsync(IPAddress address, int port, ILoggerFactory loggerFactory){
            _logger = loggerFactory.CreateLogger<TestClientAsync>();
            _tcpClient = new TcpClient();
            _tcpClient.Connect(address, port);
            var stream = _tcpClient.GetStream();
            _reader = new StreamReader(stream);
            _writer = new StreamWriter(stream){
                AutoFlush = true
            };
            _logger.LogInformation("TestClient connected");
        }

        public async Task<string?> EnterRoomForOk(string room){
            await _writer.WriteLineAsync($"/enter {room}");
            return await WaitForOk();
        }
        public async Task<string?> EnterRoom(string room){
            await _writer.WriteLineAsync($"/enter {room}");
            return await ReadLineAsync();
        }

        public async Task<string?> LeaveRoomForOk(){
            await _writer.WriteLineAsync("/leave");
            return await WaitForOk();
        }
        public async Task<string?> LeaveRoom(){
            await _writer.WriteLineAsync("/leave");
            return ReadLine();
        }

        public async Task<string?> ExitForOk(){
            await _writer.WriteLineAsync("/exit");
            return await WaitForOk();
        }
        public async Task<string?> Exit(){
            await _writer.WriteLineAsync("/exit");
            return ReadLine();
        }

        public void WriteLine(string s){
            _writer.WriteLine(s);
        }
        public async Task WriteLineAsync(string s){
            await _writer.WriteLineAsync(s);
        }
        

        public string ReadLine(){
            return _reader.ReadLine() ?? throw new Exception("Reader stream is closed");
        }
        
        public async Task<string> ReadLineAsync(){
            return await _reader.ReadLineAsync() ?? throw new Exception("Reader stream is closed");
        }

        public void Close(){
            _tcpClient.Close();
        }

        private async Task<string?> WaitForOk(){
            _logger.LogInformation("TestClient reading line");
            var res = await _reader.ReadLineAsync();
            _logger.LogInformation("TestClient read line {}", res);
            if (res == null){
                return "ReadLine returns null";
            }

            return res.Equals("[OK]") ? null : res;
        }

    }
}