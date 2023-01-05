using System;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Threading;
using System.Threading.Tasks;

namespace App{
    public class AsyncTaskSynchronizer{

        private readonly object _lock = new object();
        private readonly LinkedList<RequestWithoutCancel> _requests = new LinkedList<RequestWithoutCancel>();
        private readonly LinkedList<RequestWithoutCancel> _waiters = new LinkedList<RequestWithoutCancel>();
        public int Count{ get; private set; }

        public LinkedListNode<RequestWithoutCancel> Add(){
            lock (_lock){
                Count += 1;
                return _requests.AddLast(new RequestWithoutCancel());
            }
        }

        public void Remove(LinkedListNode<RequestWithoutCancel> node){
            lock (_lock){
                Count -= 1;
                _requests.Remove(node);
                if (Count == 0){
                    SignalAll();
                }
            }
        }

        private void SignalAll(){
            foreach (var waiter in _waiters){
                waiter.SetResult(true);
            }
        }

        public Task WaitAsync(){
            lock (_lock){
                //fast-path
                if (Count == 0){
                    return Task.CompletedTask;
                }

                var request = new RequestWithoutCancel();
                _waiters.AddLast(request);
                return request.Task;
            }
        }
    }
}