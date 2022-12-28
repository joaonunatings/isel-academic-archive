package pt.isel.pc.problemsets.set2.Exchanger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A synchronization point at which threads can pair and swap elements within pairs.
 * @param <V> - type of object that may be exchanged
 */
public interface IExchanger<V> {

    /**
     * Waits for another thread to arrive at this exchange point (unless the current thread is interrupted), and then
     * transfers the given object to it, receiving its object in return.
     * @param x - object to exchange
     * @return the object provided by the other thread
     * @throws InterruptedException - if the current thread was interrupted while waiting
     */
    V exchange(V x) throws InterruptedException;

    /**
     * Waits for another thread to arrive at this exchange point (unless the current thread is interrupted or the
     * specified waiting time elapses), and then transfers the given object to it, receiving its object in return.
     * @param x - object to exchange
     * @param timeout - maximum time to wait
     * @param unit - time unit for {@code timeout}
     * @return the object provided by the other thread
     * @throws InterruptedException - if the current thread was interrupted while waiting
     * @throws TimeoutException - if the specified waiting time elapses before another thread enters the exchange
     */
    V exchange(V x, long timeout, TimeUnit unit) throws InterruptedException, TimeoutException;
}