package pt.isel.pc.problemsets.set2.Exchanger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Exchanger lock-free implementation. Does not handle interruptions and uses busy-waiting when waiting for another
 * thread.
 * @see IExchanger for more documentation
 */
public class LockFreeExchanger<V> implements IExchanger<V> {

    private static class ExchangerItem<V> {
        V item;
        boolean isExchanged = false;

        public ExchangerItem(V item) {
            this.item = item;
        }
    }

    private final AtomicReference<ExchangerItem<V>> itemReference = new AtomicReference<>(null);

    @Override
    public V exchange(V x) throws InterruptedException { // Exemplo: th1 primeiro a chegar, th2 a seguir.

        while (true) {

            ExchangerItem<V> observedItem = itemReference.get(); // th1: null | th2: ref(x1)

            if(observedItem == null) { // th1 entra
                ExchangerItem<V> newItem = new ExchangerItem<>(x);
                if(itemReference.compareAndSet(null, newItem)) { // AR = ref(x1)
                    while (!newItem.isExchanged) { // Enquanto x1.isExchanged = false
                        Thread.yield(); // Non-blocking while
                    }
                    return newItem.item; // x = ref(x2)
                }
            } else { // th2 entra
                V receivedItem = observedItem.item; // received = x1;
                if (itemReference.compareAndSet(observedItem, null)) { // AR = null
                    observedItem.item = x; // ref(x1) = x2
                    observedItem.isExchanged = true;
                    return receivedItem; // x1
                }
            }
        }
    }

    @Override
    public V exchange(V x, long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

}