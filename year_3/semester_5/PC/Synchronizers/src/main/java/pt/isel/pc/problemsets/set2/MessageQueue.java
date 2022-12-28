package pt.isel.pc.problemsets.set2;

import pt.isel.pc.problemsets.utils.Timeouts;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Synchronizer that allows communication between threads. Producer threads deliver the messages in FIFO order
 * while consumer threads don't. This implementation uses the famously known
 * <a href="https://www.cs.rochester.edu/~scott/papers/1996_PODC_queues.pdf">Michael-Scott Queue</a>.
 * @param <E> - type of message
 */
public class MessageQueue<E> {

    private static class Node<E> {
        public final E value;
        public final AtomicReference<Node<E>> next = new AtomicReference<>(null);
        public Node(E value) {
            this.value = value;
        }
    }

    private final AtomicReference<Node<E>> head, tail;

    private volatile int waiters = 0;
    private final Object lock = new Object();

    public MessageQueue() {
        Node<E> node = new Node<>(null);
        head = new AtomicReference<>(node);
        tail = new AtomicReference<>(node);
    }

    /**
     * Non-blocking method which delivers message to queue. There's two situations that can happen in this method:
     * 1. Invoking thread sees tail in correct position then adds next node to the queue.
     * 2. Invoking thread sees tail in incorrect position, updates it and tries to enqueue again.
     * In the end, if there are any dequeuing waiters, signal one of them (order does not matter here).
     * @param message to enqueue
     */
    public void enqueue(E message) {
        var node = new Node<>(message);
        Node<E> observedTail;

        // Lock-free enqueue
        while(true) {
            observedTail = tail.get();
            Node<E> observedTailNext = observedTail.next.get();

            if (observedTail == tail.get()) {
                if (observedTailNext == null) {
                    // Observed tail is correct, write node after tail
                    if (observedTail.next.compareAndSet(null, node)) { // node is inserted
                        tail.compareAndSet(observedTail, node); // update tail to node
                        break;
                    }
                } else {
                    tail.compareAndSet(observedTail, observedTailNext); // move tail to next
                }
            }
        }

        // Notify waiters
        if(waiters == 0) return;
        synchronized (lock) {
            lock.notify();
        }
    }

    /**
     * Non-blocking helper method which tries do dequeue a message from the queue. There's two situations that can
     * happen in this method:
     * 1. Invoking thread sees tail in correct position and dequeues node next to it, returning it from method.
     * 2. Invoking thread sees tail in incorrect position. It checks if queue is empty or tail is falling behind.
     * @return {@code Optional<E>} with message dequeue succeeded or {@code Optional.empty()} if queue is empty
     */
    private Optional<E> tryDequeue() {
        while(true) {
            var observedHead = head.get();
            var observedTail = tail.get();
            var observedHeadNext = observedHead.next.get();

            if (observedHead == head.get()) {
                // Queue is empty or tail falling behind?
                if (observedHead == observedTail) {
                    // Is queue empty?
                    if (observedHeadNext == null) {
                        return Optional.empty();
                    }
                    // Advance tail
                    tail.compareAndSet(observedTail, observedHeadNext);
                } else {
                    if(head.compareAndSet(observedHead, observedHeadNext)) {
                        return Optional.of(observedHeadNext.value);
                    }
                }
            }
        }
    }

    /**
     * Potentially blocking method which tries to dequeue a message from the queue if there's any. If there's no
     * message, invoking thread waits and increment the {@link #waiters} number.
     * @param timeout - maximum time to wait
     * @return {@code Optional<E>} with message or {@code Optional.empty()} if there was no message if timeout occurred
     * or thread was interrupted
     * @throws InterruptedException - if the current thread was interrupted while waiting
     */
    public Optional<E> dequeue(long timeout) throws InterruptedException {

        // fast-path
        Optional<E> res;
        if ((res = tryDequeue()).isPresent()) {
            return res;
        }

        // wait-path
        synchronized (lock) {
            long deadline = Timeouts.deadlineFor(timeout);
            long remaining = Timeouts.remainingUntil(deadline);
            while(true) {
                waiters++;

                try {
                    lock.wait(remaining);
                } catch (InterruptedException e) {
                    waiters--;
                    Thread.currentThread().interrupt();
                    return res;
                }

                waiters--;

                remaining = Timeouts.remainingUntil(deadline);
                if (Timeouts.isTimeout(remaining)) return res;

                if ((res = tryDequeue()).isPresent()) return res;
            }
        }
    }
}