using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Threading;
using System.Threading.Tasks;
using App;
using Xunit;
using Xunit.Abstractions;

namespace Tests
{
    public class AsyncQueueTests
    {
        
        [Fact]
        public async void SimpleTest() {
            AsyncQueue<int> asyncQueue = new AsyncQueue<int>();
            asyncQueue.Add(2);
            var x = await asyncQueue.TakeAsync(new CancellationToken());
            Assert.Equal(2, x);
        }
        [Fact]
        public async void CancelTest() {
            var asyncQueue = new AsyncQueue<int>();
            CancellationTokenSource cs = new CancellationTokenSource();
            var x = asyncQueue.TakeAsync(cs.Token);
            cs.Cancel();
            asyncQueue.Add(2);
            Assert.Equal(TaskStatus.Canceled, x.Status);

        }
        [Fact]
        public async void CancelTestlate() {
            var asyncQueue = new AsyncQueue<int>();
            CancellationTokenSource cs = new CancellationTokenSource();
            var x = asyncQueue.TakeAsync(cs.Token);
            asyncQueue.Add(2);
            cs.Cancel();
            Assert.Equal(TaskStatus.RanToCompletion, x.Status);
            Assert.Equal(2, await x);

        }
        [Fact]
        public async void MultipleThreadedTest() {
            AsyncQueue<int> asyncQueue = new AsyncQueue<int>();
            Debug.Write('o');
   
            Thread worker2 = new Thread( (o => {
                Thread.Sleep(1000);
                asyncQueue.Add(2);
            }));
            worker2.Start();
            
            Log("before await:");
            Assert.Equal(2, await asyncQueue.TakeAsync(new CancellationToken(false)));
            Log("after:");
            worker2.Join();
        }

        private readonly ITestOutputHelper _output;
        public AsyncQueueTests(ITestOutputHelper output)
        {
            SynchronizationContext.SetSynchronizationContext(null);
            _output = output;
        }
        private void Log(string s)
        {
            _output.WriteLine("[{0,2}|{1,8}|{2:hh:mm:ss.fff}]{3}",
                Thread.CurrentThread.ManagedThreadId,
                Thread.CurrentThread.IsThreadPoolThread ? "pool" : "non-pool", DateTime.Now, s);
        }
    }
}