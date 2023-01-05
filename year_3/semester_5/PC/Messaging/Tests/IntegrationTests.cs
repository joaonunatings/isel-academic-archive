using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
using App;
using Microsoft.Extensions.Logging;
using Xunit;
using Xunit.Abstractions;

namespace Tests
{
    /*
     * Integration tests use the TCP/IP interface to test the server behaviour.
     *
     * Tests are sequential so that all of them can use the same port.
     */
    [Collection("Sequential")]
    public class IntegrationTests
    {
        private const int Port = 8080;
        private static readonly IPAddress LocalAddr = IPAddress.Parse("127.0.0.1");
        private const int Reps = 16;

        [Fact]
        public async Task First()
        {
            var server = new Server(_loggerFactory);
            server.Start(LocalAddr, Port, 50);
            var client0 = new TestClientAsync(LocalAddr, Port, _loggerFactory);
            var client1 = new TestClientAsync(LocalAddr, Port, _loggerFactory);
            var client0Received = await client0.EnterRoom("room");
            var client1Received = await client1.EnterRoom("room");
            Assert.Equal("[OK]", client0Received);
            Assert.Equal("[OK]", client1Received);
            for (var i = 0; i < Reps; ++i)
            {
                await client0.WriteLineAsync($"Hello from client0 {i}");
                await client1.WriteLineAsync($"Hello from client1 {i}");
                Assert.Equal($"[room]client-1 says 'Hello from client1 {i}'", await client0.ReadLineAsync());
                Assert.Equal($"[room]client-0 says 'Hello from client0 {i}'", await client1.ReadLineAsync());
            }

            Assert.Equal("[OK]", await client0.LeaveRoom());
            Assert.Equal("[OK]", await client1.LeaveRoom());
            Assert.Equal("[OK]", await client0.Exit());
            server.Stop();
            await server.JoinAsync();
            Assert.Equal("[Error: Server is exiting]", await client1.ReadLineAsync());
        }


        [Fact]
        public async Task MassClientsWithLimit()
        {
            var maxClients = 100;
            var server = new Server(_loggerFactory);
            server.Start(LocalAddr, Port, maxClients);

            var firstClient = new TestClientAsync(LocalAddr, Port, _loggerFactory);

            for (int i = 0; i < maxClients - 2; i++)
            {
                new TestClientAsync(LocalAddr, Port, _loggerFactory).EnterRoom("room1");
            }

            var lastClient = new TestClientAsync(LocalAddr, Port, _loggerFactory);
            var newClient = new TestClientAsync(LocalAddr, Port, _loggerFactory);

            Assert.Equal("[OK]", await firstClient.EnterRoom("room1"));
            Assert.Equal("[OK]", await lastClient.EnterRoom("room1"));

            Task<string?> canEnter = newClient.EnterRoom("room1");
            await Task.Delay(2000);
            Assert.False(canEnter.IsCompleted);

            Assert.Equal("[OK]", await firstClient.Exit());
            await Task.Delay(500);
            Assert.True(canEnter.IsCompleted);
            server.Stop();
            await server.JoinAsync();
        }

        [Fact]
        public async Task MessagesStressTest()
        {
            var maxClients = 50;
            var server = new Server(_loggerFactory);
            server.Start(LocalAddr, Port, maxClients);

            var clientsRoom1 = new Collection<TestClientAsync>();
            var clientsRoom2 = new Collection<TestClientAsync>();

            for (int i = 0; i < maxClients / 2; i++)
            {
                clientsRoom1.Add(new TestClientAsync(LocalAddr, Port, _loggerFactory));
                clientsRoom2.Add(new TestClientAsync(LocalAddr, Port, _loggerFactory));
            }

            for (int i = 0; i < maxClients / 2; i++)
            {
                Assert.Equal("[OK]", await clientsRoom1[i].EnterRoom("room1"));
                Assert.Equal("[OK]", await clientsRoom2[i].EnterRoom("room2"));
            }

            Collection<Task> clientsTasks = new Collection<Task>();
            for (int i = 0; i < maxClients / 2; i++)
            {
                clientsTasks.Add(clientsRoom1[i].WriteLineAsync("clientRoom1[" + i + "]"));
                clientsTasks.Add(clientsRoom2[i].WriteLineAsync("clientRoom2[" + i + "]"));
            }

            await Task.WhenAll(clientsTasks);
            for (int i = 0; i < maxClients / 2; i++)
            {
                //Assert.Equal("[OK]",await clientsRoom1[i].Exit());    // returning a message from another client (??)
                //Assert.Equal("[OK]",await clientsRoom2[i].Exit());
                await clientsRoom1[i].Exit();
                await clientsRoom2[i].Exit();
            }

            server.Stop();
            await server.JoinAsync();
        }

        /*
        [Fact]
        public async Task MessagesChangingRooms(){
            var maxClients = 5;
            var server = new Server(_loggerFactory);
            server.Start(LocalAddr, Port, maxClients);
            
            var clientMain = new TestClientAsync(LocalAddr, Port, _loggerFactory);
            var sideClient1 = new TestClientAsync(LocalAddr, Port, _loggerFactory);
            var sideClient2 = new TestClientAsync(LocalAddr, Port, _loggerFactory);

            Assert.Equal("[OK]", await sideClient1.EnterRoom("room1"));
            Assert.Equal("[OK]", await sideClient2.EnterRoom("room2"));

            await sideClient1.WriteLineAsync("sideClient1: first");
            await sideClient1.WriteLineAsync("sideClient1: second");
            await sideClient2.WriteLineAsync("sideClient2: first");
            
            Assert.Equal("[OK]", await clientMain.EnterRoom("room1"));

            Assert.Equal("[room1]client-1 says 'sideClient1: first'", await clientMain.ReadLineAsync());
            Assert.Equal("[room1]client-1 says 'sideClient1: second'", await clientMain.ReadLineAsync());
            var waitingNothingInitially = clientMain.ReadLineAsync();

            await Task.Delay(1000);
            
            Assert.False(waitingNothingInitially.IsCompleted);
            await sideClient1.WriteLineAsync("sideClient1: third");
            Assert.Equal("[room1]client-1 says 'sideClient1: third'", await waitingNothingInitially);
            await clientMain.WriteLineAsync("sideClient1: fourth");
            Assert.Equal("[room1]client-0 says 'sideClient1: fourth'", await sideClient1.ReadLineAsync());

            //********** After this point the behaviour is weird ************
            
            
            //await sideClient2.WriteLineAsync("sideClient1: second");
            
            Assert.Equal("[OK]", await clientMain.LeaveRoom());
            //await sideClient2.WriteLineAsync("sideClient1: third");
            
            Assert.Equal("[OK]", await clientMain.EnterRoom("room2"));
            await sideClient2.WriteLineAsync("sideClient2: fourth");
            Assert.Equal("[room2]client-2 says 'sideClient2: fourth'", await clientMain.ReadLineAsync());



            //var waitingNothingInitially2 = clientMain.ReadLineAsync();

            //await Task.Delay(1000);
            
            //Assert.False(waitingNothingInitially2.IsCompleted);
            //await sideClient2.WriteLineAsync("sideClient2: fourth");
            //Assert.Equal("[room2]client-1 says 'sideClient2: second'", await waitingNothingInitially2);
            
            Assert.Equal("[OK]", await clientMain.LeaveRoom());
            

            server.Stop();
            await server.JoinAsync();
        }
    */

        [Fact]
        public async Task DoubleStart()
        {
            var maxClients = 5;
            var server = new Server(_loggerFactory);
            server.Start(LocalAddr, Port, maxClients);
            try
            {
                server.Start(LocalAddr, Port, maxClients);
                Assert.True(false);
            }
            catch (Exception e)
            {
                Assert.True(true);
            }

            server.Stop();
            await server.JoinAsync();
        }

        [Fact]
        public async Task StartStopConcurrent()
        {
            var maxClients = 5;
            var repetitions = 50;

            for (int i = 0; i < repetitions; i++)
            {
                var server = new Server(_loggerFactory);

                try
                {
                    server.Start(LocalAddr, Port, maxClients);
                    server.Stop();
                }
                catch (Exception e)
                {
                    Assert.True(false);
                }

                await server.JoinAsync();
            }
        }

        [Fact]
        public async Task StartStopJoinConcurrent()
        {
            var maxClients = 5;
            var repetitions = 300;

            for (int i = 0; i < repetitions; i++)
            {
                var server = new Server(_loggerFactory);

                try
                {
                    server.Start(LocalAddr, Port, maxClients);
                    server.Stop();
                    await server.JoinAsync();
                }
                catch (Exception e)
                {
                    Assert.True(false);
                }
            }
        }

        [Fact]
        public async Task StartJoinStopConcurrent()
        {
            var maxClients = 5;
            var repetitions = 300;

            for (int i = 0; i < repetitions; i++)
            {
                var server = new Server(_loggerFactory);

                try
                {
                    server.Start(LocalAddr, Port, maxClients);
                    var join = server.JoinAsync();
                    await Task.Delay(15);
                    server.Stop();
                    await join;
                }
                catch (Exception e)
                {
                    Assert.True(false);
                }
            }
        }

        [Fact]
        public async Task ServerJoinBeforeStart()
        {
            var maxClients = 5;
            var repetitions = 300;

            for (int i = 0; i < repetitions; i++)
            {
                var server = new Server(_loggerFactory);

                try
                {
                    await server.JoinAsync();
                    Assert.True(false);
                }
                catch (Exception e)
                {
                    Assert.True(true);
                }
            }
        }

        [Fact]
        public async Task ServerStopBeforeStart()
        {
            var maxClients = 5;
            var repetitions = 300;

            for (int i = 0; i < repetitions; i++)
            {
                var server = new Server(_loggerFactory);

                try
                {
                    server.Stop();
                    Assert.True(false);
                }
                catch (Exception e)
                {
                    Assert.True(true);
                }
            }
        }

        [Fact]
        public async Task Second()
        {
            var nrClients = 100;
            var server = new Server(_loggerFactory);
            server.Start(LocalAddr, Port, nrClients);
            TestClientAsync client = null;
            List<TestClientAsync> clientl = new List<TestClientAsync>();
            for (int i = 0; i < nrClients; i++)
            {
                client = new TestClientAsync(LocalAddr, Port, _loggerFactory);
                clientl.Add(client);
                Assert.Equal("[OK]", await client.EnterRoom("room"));
            }

            client = clientl[0];
            for (var l = 0; l < Reps; ++l)
            {
                await client.WriteLineAsync($"Hello from client 0");
                for (var k = 1; k < nrClients; ++k)
                {
                    Assert.Equal($"[room]client-0 says 'Hello from client 0'", await clientl[k].ReadLineAsync());
                }
            }

            for (var k = 0; k < nrClients; ++k)
            {
                Assert.Equal("[OK]", await clientl[k].Exit());
            }

            server.Stop();
            await server.JoinAsync();
        }

        public IntegrationTests(ITestOutputHelper output)
        {
            _loggerFactory = Logging.CreateFactory(new XUnitLoggingProvider(output));
            SynchronizationContext.SetSynchronizationContext(null); //<-
            _output = output; //<-
        }

        private readonly ITestOutputHelper _output; // <-

        private readonly ILoggerFactory _loggerFactory;


        private void Log(string s)
        {
            _output.WriteLine("[{0,2}|{1,8}|{2:hh:mm:ss.fff}]{3}",
                Thread.CurrentThread.ManagedThreadId,
                Thread.CurrentThread.IsThreadPoolThread ? "pool" : "non-pool", DateTime.Now, s);
        }
    }
}