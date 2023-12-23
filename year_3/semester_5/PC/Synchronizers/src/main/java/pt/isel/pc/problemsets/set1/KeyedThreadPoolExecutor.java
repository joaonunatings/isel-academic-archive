package pt.isel.pc.problemsets.set1;

import pt.isel.pc.problemsets.utils.NodeLinkedList;
import pt.isel.pc.problemsets.utils.Timeouts;

import java.util.*;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Synchronizer using a thread pool executor service and key implementation for each task.
 * Monitor-style design.
 * @author joaonunatings
 */
public class KeyedThreadPoolExecutor {

    /**
     * Represents a task/work to be made by calling {@link #execute(Runnable, Object)}. Notice how each task has a key.
     * This makes execution of tasks with equal keys impossible.
     */

    private static class Work {
        public final Runnable runnable;
        public final Object key;

        public Work(Runnable runnable, Object key) {
            this.runnable = runnable;
            this.key = key;
        }
    }

    private final Lock monitor = new ReentrantLock();

    private final NodeLinkedList<Work> workQueue = new NodeLinkedList<>();
    private final Collection<Object> workingKeySet = Collections.synchronizedCollection(new HashSet<>()); // Synchronization from Collections
    private final NodeLinkedList<Condition> waitingWorkerThreads = new NodeLinkedList<>();

    private final int maxPoolSize, keepAliveTime;
    private int currentWorkers = 0;

    private boolean shutdownState = false, shutdownDone = false;
    private final Condition shutdownCondition = monitor.newCondition();

    /**
     * Initializes the thread pool.
     * @param maxPoolSize limits the number of {@link #currentWorkers} (threads that are RUNNING and WAITING for work)
     * @param keepAliveTime time that keeps threads in thread pool when WAITING for work. After timeout, remove thread
     * from the pool
     */
    public KeyedThreadPoolExecutor(int maxPoolSize, int keepAliveTime) {
        if (maxPoolSize <= 0 || keepAliveTime <= 0) throw new IllegalStateException();
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
    }

    /**
     * Executes a Runnable object with a key attached.
     * This method searches for worker threads in the pool that are currently waiting for work. If there's none, it
     * creates a new thread if the maximum pool size hasn't been reached yet. If none of this applies, it adds
     * the work to a queue.
     *
     * @throws RejectedExecutionException - Thrown when this pool is shutting down
     * @param runnable - The Runnable object which the thread runs
     * @param key - This key stops simultaneous execution of Runnables with the same key
     */
    public void execute(Runnable runnable, Object key) {
        monitor.lock();
        try {
            if (shutdownState) throw new RejectedExecutionException("Thread pool shutting down...");

            workQueue.enqueue(new Work(runnable, key));

            if (waitingWorkerThreads.isNotEmpty()) {
                waitingWorkerThreads.pull().value.signal();
            }
            else if (currentWorkers < maxPoolSize && !workingKeySet.contains(key)) {
                var work = workQueue.pull().value;
                workingKeySet.add(work.key);
                var th = new Thread(() -> threadLoop(work));
                th.start();
                currentWorkers++;
            }
        } finally {
            monitor.unlock();
        }
    }

    /**
     * Gets next work for invoking thread. If there's none, wait {@link #keepAliveTime} milliseconds for some work.
     * If there's no work and timeout exceeded, thread gets removed from the pool
     * @return the {@link Work} to be executed or {@link Optional#empty()} if there's none
     */
    private Optional<Work> getWorkOrWait() {
        monitor.lock();
        try {

            // fast-path
            Optional<Work> work;
            if ((work = getWork()).isPresent()) {
                return work;
            }

            // wait-path - This means the workQueue is empty or there are duplicate keys
            long deadline = Timeouts.deadlineFor(keepAliveTime);
            long remaining = Timeouts.remainingUntil(deadline);
            NodeLinkedList.Node<Condition> node = waitingWorkerThreads.enqueue(monitor.newCondition());
            while(true) {

                try {
                    node.value.await(remaining, TimeUnit.MILLISECONDS);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    waitingWorkerThreads.remove(node);
                    currentWorkers--;
                    if (isShutdownDone())
                        shutdownCondition.signalAll();
                    return Optional.empty();
                }

                if ((work = getWork()).isPresent())
                    return work;

                remaining = Timeouts.remainingUntil(deadline);
                if (Timeouts.isTimeout(remaining)) {
                    waitingWorkerThreads.remove(node);
                    currentWorkers--;
                    if (isShutdownDone())
                        shutdownCondition.signalAll();
                    return Optional.empty();
                }
            }
        } finally {
            monitor.unlock();
        }
    }

    /**
     * Non-blocking version of {@link #getWorkOrWait()}, this means, there's no waiting if there's no work to deliver.
     * @return the {@link Work} to be executed or {@link Optional#empty()} if there's none
     */
    private Optional<Work> getWork() {
        if (workQueue.isEmpty()) return Optional.empty();
        var i = 0;
        for (NodeLinkedList.Node<Work> node = workQueue.getHeadNode(); // Iterate over queue to get next job
             i < workQueue.getCount();
             ++i, node = node.next)
        {
            if (!workingKeySet.contains(node.value.key)) {
                workQueue.remove(node);
                workingKeySet.add(node.value.key);
                return Optional.of(node.value);
            }
        }
        return Optional.empty();
    }

    /**
     * This is the function each thread runs when {@link Thread#start()} is called.
     * @param firstWork runs the first time pool creates a worker thread.
     */
    private void threadLoop(Work firstWork) {
        firstWork.runnable.run();
        workingKeySet.remove(firstWork.key); // Remove key from ongoing work set

        while (true) {
            var maybeWork = getWorkOrWait();
            maybeWork.ifPresent(work -> {
                work.runnable.run();
                workingKeySet.remove(work.key);
            });
            if (maybeWork.isEmpty()) {
                return;
            }
        }
    }

    /**
     * Initializes shutdown process in the thread pool executor
     */
    public void shutdown() {
        monitor.lock();
        try {
            shutdownState = true;
            isShutdownDone();
        } finally {
            monitor.unlock();
        }
    }

    /**
     * Updates state of shutdown in the thread pool executor
     * @return true if shutdown process is completed, false otherwise
     */
    private boolean isShutdownDone() {
        shutdownDone = shutdownState && currentWorkers == 0 && workQueue.isEmpty();
        return shutdownDone;
    }

    /**
     * Synchronizes invoking threads with the shutdown process of the thread pool executor.
     * @param timeout is time that invoking thread waits for the shutdown process
     * @return true if shutdown was completed within the time given, false otherwise
     * @throws InterruptedException while waiting for condition {@link #shutdownCondition}
     */
    public boolean awaitTermination(int timeout) throws InterruptedException {
        monitor.lock();
        try {
            // fast-path
            if(shutdownDone) return true;

            if(Timeouts.isTimeout(timeout)) return false;

            // wait-path
            long deadline = Timeouts.deadlineFor(timeout);
            long remaining = Timeouts.remainingUntil(deadline);
            while(true) {
                try {
                    shutdownCondition.await(remaining, TimeUnit.MILLISECONDS);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return shutdownDone;
                }

                if (shutdownDone) return true;

                remaining = Timeouts.remainingUntil(deadline);
                if(Timeouts.isTimeout(remaining)) {
                    return false;
                }
            }
        } finally {
            monitor.unlock();
        }

    }
}
