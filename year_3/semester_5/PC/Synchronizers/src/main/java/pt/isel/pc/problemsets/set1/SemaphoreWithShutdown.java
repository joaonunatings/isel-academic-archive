package pt.isel.pc.problemsets.set1;

import pt.isel.pc.problemsets.utils.NodeLinkedList;
import pt.isel.pc.problemsets.utils.Timeouts;

import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Unary semaphore with shutdown functionality.
 * Monitor-style design.
 * @author joaonunatingscode (47220)
 */
public class SemaphoreWithShutdown {

    private final Lock monitor = new ReentrantLock();
    private final Condition shutdownCompleted = monitor.newCondition();
    private final NodeLinkedList<Condition> queue = new NodeLinkedList<>(); // Represent threads waiting for semaphore units

    private int units;
    private final int initialUnits;
    private boolean shutdownState = false;

    /**
     * Creates the semaphore and sets its {@link #units} equal to passed {@link #initialUnits}.
     * @param initialUnits from semaphore
     */
    public SemaphoreWithShutdown(int initialUnits) {
        if (initialUnits <= 0) throw new IllegalStateException("Initial semaphore units must be greater than 0");
        units = this.initialUnits = initialUnits;
    }

    /**
     * Invoking thread tries to acquire (decrement) a single unit from semaphore. If there are units available and no other thread
     * is waiting for it, it returns immediately decrementing a unit from the semaphore. If there are no units
     * available, it waits on a condition from {@link #queue}.
     * @param timeout Time to wait for condition signal, in milliseconds
     * @return true if successfully acquired a unit, false if occurred timeout.
     * @throws InterruptedException if thread gets interrupted in await call of {@link #queue} condition
     * @throws CancellationException if semaphore is in shutdown state
     */
    public boolean acquireSingle(long timeout) throws InterruptedException, CancellationException {
        monitor.lock();
        try {
            // fast-path
            if (shutdownState) {
                signalIfNeeded(); // Forces shutdown by signalling other threads
                if (initialUnits == units) shutdownCompleted.signalAll();
                throw new CancellationException("Semaphore shutting down...");
            }

            if (queue.isEmpty() && units > 0) {
                units--;
                return true;
            }

            if (Timeouts.noWait(timeout))
                return false;

            // wait-path
            long deadline = Timeouts.deadlineFor(timeout);
            long remaining = Timeouts.remainingUntil(deadline);
            NodeLinkedList.Node<Condition> node = queue.enqueue(monitor.newCondition());
            while (true) {
                try {
                    node.value.await(remaining, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    queue.remove(node);
                    if (units > 0 || shutdownState) signalIfNeeded(); // Get another thread to complete interrupted acquire
                    throw e;
                }

                if (shutdownState) {
                    queue.remove(node);
                    signalIfNeeded();
                    throw new CancellationException("Semaphore shutting down...");
                }

                if (units > 0) {
                    queue.remove(node);
                    units--;
                    return true;
                }

                remaining = Timeouts.remainingUntil(deadline);
                if (Timeouts.isTimeout(remaining)) {
                    queue.remove(node);
                    return false;
                }
            }
        } finally {
            monitor.unlock();
        }
    }

    /**
     * Invoking thread tries to release (increment) a single unit to the semaphore. The increment only happens if limit
     * is not yet reached {@link #initialUnits}. Signals a thread waiting on a condition in {@link #queue}. Signals all
     * threads waiting for shutdown if this process has been completed (only valid if {@link #startShutdown()} has
     * been previously called)
     */
    public void releaseSingle() {
        monitor.lock();
        try {
            if (units < initialUnits) units++;
            signalIfNeeded();
            if (shutdownState && units == initialUnits) shutdownCompleted.signalAll();
        } finally {
            monitor.unlock();
        }
    }

    /**
     * Initializes the shutdown process in the semaphore
     */
    public void startShutdown() {
        monitor.lock();
        try {
            if ((shutdownState = true) && units == initialUnits) shutdownCompleted.signalAll();
            else signalIfNeeded();
        } finally {
            monitor.unlock();
        }
    }

    /**
     * Invoking thread waits for shutdown process to be completed. Completed shutdown means that there are no waiting
     * threads in {@link #queue} and current {@link #units} reached the {@link #initialUnits}.
     * @param timeout Time to wait for the shutdown to be completed, in milliseconds
     * @return true if shutdown was completed within time given, false if timeout before shutdown completion
     * @throws InterruptedException when waiting in {@link #shutdownCompleted} condition
     */
    public boolean waitShutdownCompleted(long timeout) throws InterruptedException {
        monitor.lock();
        try {

            // fast-path
            if (shutdownState && units == initialUnits) {
                shutdownCompleted.signalAll();
                return true;
            }

            if (Timeouts.noWait(timeout)) return false;

            // wait-path
            long deadline = Timeouts.deadlineFor(timeout);
            long remaining = Timeouts.remainingUntil(deadline);
            while (true) {
                try {
                    shutdownCompleted.await(remaining, TimeUnit.MILLISECONDS);

                } catch (InterruptedException e) {
                    if (shutdownState) {
                        Thread.currentThread().interrupt();
                        return true;
                    }
                    throw e;
                }

                if (shutdownState)
                    return true;

                remaining = Timeouts.remainingUntil(deadline);
                if (Timeouts.isTimeout(remaining)) return false;
            }

        } finally {
            monitor.unlock();
        }
    }

    /**
     * Signals older waiting thread on {@link #queue}. This keeps FIFO order in this semaphore
     */
    private void signalIfNeeded() {
        if (queue.isNotEmpty()) {
            queue.getHeadValue().signal();
        }
    }
}
