using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Threading;
using System.Threading.Tasks;

namespace App
{
    /*
     * Async Queue with FIFO policy and asynchronous acquisition interface.
     */
    public class AsyncQueue<T>
    {
        private readonly object _lock = new();

        /*
         * Represents a pending acquisition request.
         */
        private class Request : TaskCompletionSource<T>
        {
            public bool Done;
            
            /*
             * These fields are assigned only once during the AcquireAsync where the Request instance is created
             * and while holding the lock.
             * These fields are accessed to dispose the timer the registration, which can happen in two scenarios
             * - While holding the lock acquired during AcquireAsync, when the cancellation is synchronous (i.e.
             * happens inside the CancellationToken.Register
             * - Without holding the lock *but* after the lock was acquired on an asynchronous cancellation.
             * The fact that the lock was acquired and released by the thread previously ensures the correct
             * visibility of these fields.
             */
            public CancellationTokenRegistration? CancellationTokenRegistration ;

            public Request()
                // To ensure that completing the task doesn't run the continuations asynchronously.
                :base(TaskCreationOptions.RunContinuationsAsynchronously)
            {
            }
        }
        
        private readonly LinkedList<Request> _requests = new LinkedList<Request>();
        private readonly LinkedList<T> _items = new LinkedList<T>();

        /*
         * The cancellation handlers, created once per queue instance.
         * We avoid allocating new handlers for each acquisition call.
         */
        private readonly Action<object?> _cancellationCallback;

        public AsyncQueue()
        {
            // We have the assurance that the object passed to the handler is a LinkedListNode containing a Request
            // due to the way the handlers are registered on an acquisition.
            _cancellationCallback = node =>
            {
                TryCancel((LinkedListNode<Request>) node);
            };
        }

        public Task<T> TakeAsync(CancellationToken ct)
        {
            lock (_lock)
            {
                // fast-path
                if (_requests.Count == 0 && _items.Count > 0)
                {
                    T removed = _items.First.Value;
                    _items.RemoveFirst();
                    return Task.FromResult(removed);
                }

                // async-path
                var request = new Request();
                var requestNode = _requests.AddLast(request);

                if (ct.CanBeCanceled)
                {
                    request.CancellationTokenRegistration = ct.Register(_cancellationCallback, requestNode);
                }

                return request.Task;
            }
        }
        
        public void Add(T value)
        {
            Request? request = null;
            lock (_lock)
            {
                //fast path
                if (_requests.Count > 0 && _items.Count == 0)
                    request = Release();
                else
                    _items.AddLast(value);
            }
            
            // Important: performed outside of mutual exclusion due to dispose(CancellationTokenRegistration?.Dispose()
            // can be executed on another thread therefore a potentional deadlock situation)
            Complete(request, value);
        }
        
        /*
         * This method removes a queued acquisition request that can be satisfied,
         * updating the list
         */
        private Request Release()
        {
            Debug.Assert(Monitor.IsEntered(_lock), "Lock MUST be held");
            Debug.Assert(_requests.Count > 0, "Must have a request");
            
            Request request = _requests.First.Value;
            _requests.RemoveFirst();
            
            return request;
        }
        
        /*
         * This method
         * - Disposes the resources used by a request, the CT registration.
         * - Completes the associated task.
         */
        private void Complete(Request? request, T value)
        {
            if (request != null)
            {
                Debug.Assert(!Monitor.IsEntered(_lock), "Lock must NOT be held");
                DisposeRequest(request, false);
                request.SetResult(value);
            }
        }

        /*
         * This method *tries* to cancel a pending request.
         * It is "try", because the request may already be completed (success or cancel) by a different thread.
         * This race is resolved by evaluating the Done field while holding the lock.
         * The first completion to arrive (the lock ensures serialization) marks the request as done,
         * removes it from the queue, disposes the resources and completes the associated task.
         */
        private void TryCancel(LinkedListNode<Request> node)
        {
            lock (_lock)
            {
                if (node.Value.Done)
                {
                    // Request already completed, *absolutely nothing else* to do
                    return;
                }
                node.Value.Done = true;
                _requests.Remove(node);
                if (_items.Count > 0 && _requests.Count > 0)
                {
                    throw new ArgumentException("SHOULD NEVER GET HERE! _items > 0 and _requests > 0");
                }
            }
            // If the code reaches this point, then this cancellation won the race and it is in charge
            // of disposing the request resources and completing the task.
            // This
            // - *Must* be performed outside the lock to avoid deadlocks when disposing the CT.
            // - *Can* be performed outside the lock because only one thread will do that *after* having accessed the lock.
            // The only exception is synchronous cancellation (done inside the CT.Register call). In this case the lock
            // is held, however the registration Dispose doesn't deadlock in that case.
            DisposeRequest(node.Value, true);
            // Cancellation due to a CancellationToken
            node.Value.SetCanceled();
            
            //Complete(request, value);
        }

        private void DisposeRequest(Request request, bool isCancelling)
        {
            if (!isCancelling)
            {
                /*
                 * When the Request dispose is due to a CT registration callback,
                 * then we don't need to dispose that registration
                 * (that is done automatically when the callback is called).
                 */
                request.CancellationTokenRegistration?.Dispose();
            }
        }

        public int Count()
        {
            return _requests.Count;
        }
    }
}