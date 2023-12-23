package pt.isel.pc.problemsets.set1;

import org.jetbrains.annotations.NotNull;
import pt.isel.pc.problemsets.utils.NodeLinkedList;
import pt.isel.pc.problemsets.utils.Timeouts;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Synchronizer that sends messages between threads using consumers and producers with FIFO order.
 * Kernel-style design.
 * @author joaonunatings
 * @param <E> is generic for type of message
 */
public class BlockingMessageQueue<E> {

    /**
     * Represents a dequeue request. This means that when invoking thread calls {@link #dequeue()} and there's no
     * message in the {@link #messages} queue, it adds a {@link DequeueRequest} to a queue to be handled later.
     * @param <E> is generic for type of message
     */
    private static class DequeueRequest<E> {
        public E message = null;
        public final Condition condition;
        public boolean isCancelled, isDone;

        public DequeueRequest(Lock lock) {
            condition = lock.newCondition();
            isCancelled = isDone = false;
        }
    }

    private static class EnqueueRequest<E> {
        public final E message;
        public final Condition condition;

        public EnqueueRequest(E message, Lock lock) {
            this.message = message;
            condition = lock.newCondition();
        }
    }

    private final int capacity;

    private final Lock monitor = new ReentrantLock();
    private final NodeLinkedList<E> messages = new NodeLinkedList<>();
    private final NodeLinkedList<DequeueRequest<E>> dequeueRequests = new NodeLinkedList<>();
    private final NodeLinkedList<EnqueueRequest<E>> enqueueRequests = new NodeLinkedList<>();

    /**
     * Initializes the message queue with its message capacity.
     * @param capacity is number of maximum messages that can fit in {@link #messages} queue waiting to be dequeued
     */
    public BlockingMessageQueue(int capacity) {
        if (capacity <= 0) throw new IllegalStateException("Capacity must be greater than 0");
        this.capacity = capacity;
    }

    /**
     * Enqueues a message to {@link #messages} queue. If this queue is full (given its {@link #capacity} it waits a
     * timeout and then tries to add the message again.
     * @param message to be added to the {@link #messages} queue
     * @param timeout is time where
     * @return true if message enqueued successfully, false otherwise
     * @throws InterruptedException when waiting for space in the {@link #messages}
     */
    public boolean enqueue(E message, long timeout) throws InterruptedException {
        monitor.lock();
        try {

            // fast-path
            // When there are dequeueRequests and no messages in queue
            if (dequeueRequests.isNotEmpty() && messages.isEmpty()) {
                NodeLinkedList.Node<DequeueRequest<E>> dequeueNode = dequeueRequests.pull();
                dequeueNode.value.message = message;
                dequeueNode.value.isDone = true;
                dequeueNode.value.condition.signal();
                return true;
            }

            // No dequeueRequests, so we just add to messages queue
            if(messages.getCount() < capacity) {
                messages.enqueue(message);
                return true;
            }

            if(Timeouts.isTimeout(timeout)) return false;

            // wait-path
            long deadline = Timeouts.deadlineFor(timeout);
            long remaining = Timeouts.remainingUntil(deadline);
            // If capacity is reached
            NodeLinkedList.Node<EnqueueRequest<E>> enqueueNode = enqueueRequests.enqueue(new EnqueueRequest<>(message, monitor));
            while(true) {
                try {
                    enqueueNode.value.condition.await(remaining, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    enqueueRequests.remove(enqueueNode);
                    Thread.currentThread().interrupt();
                    return false;
                }

                // When there are dequeueRequests and no messages in queue
                if (dequeueRequests.isNotEmpty() && messages.isEmpty()) {
                    NodeLinkedList.Node<DequeueRequest<E>> dequeueNode = dequeueRequests.pull();
                    dequeueNode.value.message = message;
                    dequeueNode.value.isDone = true;
                    dequeueNode.value.condition.signal();
                    enqueueRequests.remove(enqueueNode);
                    return true;
                }

                // No dequeueRequests, so we just add to messages queue
                if(messages.getCount() < capacity) {
                    messages.enqueue(message);
                    enqueueRequests.remove(enqueueNode);
                    return true;
                }

                remaining = Timeouts.remainingUntil(deadline);
                if (Timeouts.isTimeout(remaining)) {
                    enqueueRequests.remove(enqueueNode);
                    return false;
                }
            }
        } finally {
            monitor.unlock();
        }
    }

    /**
     * Non-blocking dequeue a message from the {@link #messages} queue.
     * @return a "to-be" message in the form of {@link Future}. If the message is already present it returns a
     * {@link CompletedFuture} instead.
     */
    public Future<E> dequeue() {
        monitor.lock();
        try {

            // fast-path
            if(messages.isNotEmpty()) {
                E message = messages.pull().value;
                if(enqueueRequests.isNotEmpty()) {
                    enqueueRequests.getHeadValue().condition.signal();
                }
                return new CompletedFuture<>(message);
            }

            // wait-path
            NodeLinkedList.Node<DequeueRequest<E>> node = dequeueRequests.enqueue(new DequeueRequest<>(monitor));
            return new NodeBasedFuture(node);
        } finally {
            monitor.unlock();
        }
    }

    /**
     * This class represents a task "to-be" yet completed. In this case, the invoking thread of {@link #dequeue()}
     * receives this implementation of {@link Future} if there's no message waiting in {@link #messages}.
     */
    private class NodeBasedFuture implements Future<E> {

        private final NodeLinkedList.Node<DequeueRequest<E>> node;

        public NodeBasedFuture(NodeLinkedList.Node<DequeueRequest<E>> node) {
            this.node = node;
        }

        @Override
        public boolean cancel(boolean b) {
            monitor.lock();
            try {
                if (node.value.isDone)
                    return false;
                if (node.value.isCancelled)
                    return true;

                dequeueRequests.remove(node);
                node.value.isCancelled = true;
                node.value.condition.signal();
                return true;
            } finally {
                monitor.unlock();
            }
        }

        @Override
        public boolean isCancelled() {
            monitor.lock();
            try {
                return node.value.isCancelled;
            } finally {
                monitor.unlock();
            }
        }

        @Override
        public boolean isDone() {
            monitor.lock();
            try {
                return node.value.isDone;
            } finally {
                monitor.unlock();
            }
        }

        @Override
        public E get() throws InterruptedException {
            monitor.lock();
            try {
                while(true) {
                    if(node.value.isDone) {
                        return node.value.message;
                    }
                    if(node.value.isCancelled) {
                        throw new CancellationException();
                    }
                    node.value.condition.await();
                }
            } finally {
                monitor.unlock();
            }
        }

        @Override
        public E get(long l, @NotNull TimeUnit timeUnit) throws InterruptedException, TimeoutException {
            monitor.lock();
            try {

                long deadline = Timeouts.deadlineFor(l, timeUnit);
                long remaining = Timeouts.remainingUntil(deadline);
                while(true) {
                    if (node.value.isDone) {
                        return node.value.message;
                    }

                    if (node.value.isCancelled) {
                        throw new CancellationException();
                    }

                    if (Timeouts.isTimeout(remaining))
                        throw new TimeoutException();

                    node.value.condition.await(remaining, timeUnit);

                    remaining = Timeouts.remainingUntil(deadline);
                }
            } finally {
                monitor.unlock();
            }
        }
    }

    /**
     * This represents a {@link Future} that already accomplished its result. In this case, it already contains a
     * message to return.
     * @param <E> type of message
     */
    private static class CompletedFuture<E> implements Future<E> {

        private final E message;

        public CompletedFuture(E message) {
            this.message = message;
        }

        @Override
        public boolean cancel(boolean b) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public E get() {
            return message;
        }

        @Override
        public E get(long l, @NotNull TimeUnit timeUnit) {
            return message;
        }
    }
}
