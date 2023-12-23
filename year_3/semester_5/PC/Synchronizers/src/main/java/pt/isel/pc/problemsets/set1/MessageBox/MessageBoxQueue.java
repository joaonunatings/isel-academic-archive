package pt.isel.pc.problemsets.set1.MessageBox;

import pt.isel.pc.problemsets.utils.NodeLinkedList;
import pt.isel.pc.problemsets.utils.Timeouts;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Synchronizer for sending messages between threads.
 * Old version of synchronizer, check {@link MessageBox} for updated version.
 * Kernel-style design.
 * @author joaonunatings
 * @param <T> Message to be sent
 */
@Deprecated
public class MessageBoxQueue<T> {

    /**
     * A {@link Batch} represents a group of threads waiting for a message.
     * @param <T> Message to be sent
     */
    private static class Batch<T> {
        public int waitingThreads = 0;
        public T message = null;
        public boolean messageSent = false;
    }

    private final Lock monitor = new ReentrantLock();
    private final Condition hasMessage = monitor.newCondition();
    private final NodeLinkedList<Batch<T>> batches = new NodeLinkedList<>();

    /**
     * Blocks invoking thread for a given time. Eventually, it gets signalled and returns its received message. If it
     * timeouts, returns no message. This method also creates a {@link Batch} or uses existing one accordingly
     * to {@link #batches} queue state.
     * @param timeout Time to wait for message, in milliseconds.
     * @return message received as an Optional object.
     * @throws InterruptedException while waiting for condition {@link #hasMessage}. Even though the interrupt
     * occurs, it tries to return the message
     */
    public Optional<T> waitForMessage(long timeout) throws InterruptedException {
        monitor.lock();
        try {
            if (Timeouts.noWait(timeout)) return Optional.empty();

            var node =
                    (batches.isEmpty() || batches.getHeadValue().messageSent) ?
                            batches.enqueue(new Batch<>()) :
                            batches.getHeadNode();
            node.value.waitingThreads++;

            long deadline = Timeouts.deadlineFor(timeout);
            long remaining = Timeouts.remainingUntil(deadline);
            while(true) {
                try {
                    hasMessage.await(remaining, TimeUnit.MILLISECONDS);

                } catch (InterruptedException e) { // Return message if possible and rearm/rethrow interrupt exception.
                    node.value.waitingThreads--;
                    if (node.value.messageSent) {
                        if (node.value.waitingThreads == 0) batches.remove(node);
                        Thread.currentThread().interrupt();
                        return Optional.of(node.value.message);
                    }
                    throw e;
                }

                if (node.value.messageSent) {
                    node.value.waitingThreads--;
                    if (node.value.waitingThreads == 0) batches.remove(node);
                    return Optional.of(node.value.message);
                }

                remaining = Timeouts.remainingUntil(deadline);
                if (Timeouts.isTimeout(remaining)) {
                    node.value.waitingThreads--;
                    if (node.value.waitingThreads == 0) batches.remove(node);
                    return Optional.empty();
                }
            }
        } finally {
            monitor.unlock();
        }
    }

    /**
     * Invoking thread sends message to all waiting threads in last added {@link Batch}.
     * @param message Message to send to waiting threads (if there are any).
     * @return exact number of waiting threads who "will" receive message. Emphasis on "will" because waiting threads
     * will concurrently return the message in {@link #waitForMessage(long)} and not all at the same time.
     */
    public int sendToAll(T message) {
        monitor.lock();
        try {
            if (batches.isEmpty() || batches.getHeadValue().messageSent) return 0;
            Batch<T> batch = batches.getHeadValue();
            batch.message = message;
            batch.messageSent = true;
            hasMessage.signalAll();
            return batch.waitingThreads;
        } finally {
            monitor.unlock();
        }
    }
}