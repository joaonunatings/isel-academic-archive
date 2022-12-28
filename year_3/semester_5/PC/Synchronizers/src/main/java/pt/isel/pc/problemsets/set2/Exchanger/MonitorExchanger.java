package pt.isel.pc.problemsets.set2.Exchanger;

import pt.isel.pc.problemsets.utils.Timeouts;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Exchanger implementation based on monitors.
 * @see IExchanger for more documentation
 */
public class MonitorExchanger<V> implements IExchanger<V> {

    private static class ExchangerItem<V> {
        V item;
        boolean isExchanged = false;

        public ExchangerItem(V item) {
            this.item = item;
        }
    }

    private ExchangerItem<V> exchangerNode = null;

    private final Object lock = new Object();

    @Override
    public V exchange(V x) throws InterruptedException {
        synchronized(lock) {

            // fast-path
            if (exchangerNode != null) {
                V receivedItem = exchangerNode.item;

                exchangerNode.item = x;
                exchangerNode.isExchanged = true;
                exchangerNode = null;

                lock.notify();

                return receivedItem;
            }

            // wait-path
            exchangerNode = new ExchangerItem<>(x);
            ExchangerItem<V> localExchangerItem = exchangerNode;

            while (true) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    if (localExchangerItem.isExchanged) {
                        Thread.currentThread().interrupt();
                        return localExchangerItem.item;
                    }
                    throw e;
                }

                if (localExchangerItem.isExchanged)
                    return localExchangerItem.item;
            }
        }
    }

    @Override
    public V exchange(V x, long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        synchronized(lock) {

            // fast-path
            if (exchangerNode != null) {
                V receivedItem = exchangerNode.item;

                exchangerNode.item = x;
                exchangerNode.isExchanged = true;
                exchangerNode = null;

                lock.notify();

                return receivedItem;
            }

            if (timeout <= 0) throw new TimeoutException();

            // wait-path
            exchangerNode = new ExchangerItem<>(x);
            ExchangerItem<V> localExchangerItem = exchangerNode;

            long deadline = Timeouts.deadlineFor(timeout, unit);
            long remaining = Timeouts.remainingUntil(deadline);
            while(true) {
                try {
                    lock.wait(remaining);
                } catch (InterruptedException e) {
                    if (localExchangerItem.isExchanged) {
                        Thread.currentThread().interrupt();
                        return localExchangerItem.item;
                    }
                    throw e;
                }

                remaining = Timeouts.remainingUntil(deadline);
                if (Timeouts.isTimeout(remaining)) throw new TimeoutException();

                if (localExchangerItem.isExchanged) return localExchangerItem.item;
            }
        }
    }
}