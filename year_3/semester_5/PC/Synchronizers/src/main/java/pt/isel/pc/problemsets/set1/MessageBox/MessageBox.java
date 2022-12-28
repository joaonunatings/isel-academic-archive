package pt.isel.pc.problemsets.set1.MessageBox;

import pt.isel.pc.problemsets.utils.Timeouts;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Synchronizer for sending messages between threads.
 * New version of synchronizer with serious performance improvements.
 * Kernel-style design.
 * @author joaonunatingscode (47220)
 * @param <T> Message to be sent
 */
public class MessageBox<T> {

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
    private Batch<T> batch = null;

    /**
     * Blocks invoking thread for a given time. Eventually, it gets signalled and returns its received message. If it
     * timeouts, returns no message. This method also creates a new {@link MessageBox.Batch} instance or uses existing
     * one (created by some previous thread).
     * @param timeout Time to wait for message, in milliseconds.
     * @return message received as an Optional object.
     * @throws InterruptedException while waiting for condition {@link #hasMessage}.
     */
    public Optional<T> waitForMessage(long timeout) throws InterruptedException {
        monitor.lock();
        try {
            // fast-path
            if (Timeouts.noWait(timeout)) return Optional.empty();

            if (batch == null)
                batch = new Batch<>();
            batch.waitingThreads++;
            var _batch = batch;

            // wait-path
            long deadline = Timeouts.deadlineFor(timeout);
            long remaining = Timeouts.remainingUntil(deadline);
            while(true) {
                try {
                    hasMessage.await(remaining, TimeUnit.MILLISECONDS);

                } catch (InterruptedException e) {
                    batch.waitingThreads--;
                    throw e;
                }

                if (_batch.messageSent) {
                    return Optional.of(_batch.message);
                }

                remaining = Timeouts.remainingUntil(deadline);
                if (Timeouts.isTimeout(remaining)) {
                    batch.waitingThreads--;
                    return Optional.empty();
                }
            }
        } finally {
            monitor.unlock();
        }
    }

    /**
     /**
     * Invoking thread sends message to all waiting threads in last created {@link MessageBox.Batch}.
     * @param message Message to send to waiting threads (if there are any).
     * @return exact number of waiting threads who "will" receive message. Emphasis on "will" because waiting threads
     * will concurrently return the message in {@link #waitForMessage(long)} and not all at the same time.
     */
    public int sendToAll(T message) {
        monitor.lock();
        try {
            if (batch == null) return 0;
            batch.message = message;
            batch.messageSent = true;
            hasMessage.signalAll();
            int waitingThreads = batch.waitingThreads;
            batch = null;
            return waitingThreads;
        } finally {
            monitor.unlock();
        }
    }
}