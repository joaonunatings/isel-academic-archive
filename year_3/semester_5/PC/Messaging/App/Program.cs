using System;
using System.Net;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;

namespace App
{
    public class Program
    {
        private static readonly int _maxClientCount = 2;

        static async Task Main()
        {
            if (_maxClientCount <= 0)
            {
                throw new ArgumentException("Client count must be >= 1");
            }
            
            var loggerFactory = Logging.CreateFactory();
            var logger = loggerFactory.CreateLogger<Program>();
            
            logger.LogInformation("Starting program");

            var server = new Server(loggerFactory);
            var port = 8080;
            var localAddr = IPAddress.Parse("127.0.0.1");
            server.Start(localAddr, port, _maxClientCount);
            Console.CancelKeyPress += (_, eventArgs) =>
            {
                eventArgs.Cancel = true;
                logger.LogInformation("Stopping the server");
                server.Stop();
            };
            
            await server.JoinAsync();
        }
    }
}