package pt.isel.pc.problemsets.set2;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Synchronizer that allows a message to be consumed by one or more consumer threads.
 * @param <M> - type of message
 */
public class MessageBox<M> {

    /**
     * Represents an holder in order to use inside an {@link AtomicReference} to keep operations atomic and allow
     * CAS usage.
     * @param <M> - type of message
     */
    private static class MsgHolder<M> {
        final M msg;
        int lives;

        public MsgHolder(M msg, int lives) {
            this.msg = msg;
            this.lives = lives;
        }
    }

    private final AtomicReference<MsgHolder<M>> holderReference = new AtomicReference<>(null);

    /**
     * A thread can publish a message to be consumed. Whenever another thread publishes a message, it replaces the
     * previous message with the new one.
     * @param m - message to publish
     * @param lvs - level of consumption (times message can be consumed)
     */
    public void publish(M m, int lvs) {
        holderReference.set(new MsgHolder<>(m, lvs));
    }

    /**
     * Non-blocking method where calling thread tries consuming message from the holder.
     * @return message if CAS succeeds or null if there's no holder or message has been consumed (no more lives)
     */
    public M tryConsume() {
        while(true) {
            MsgHolder<M> observedHolder = holderReference.get();
            if (observedHolder != null && observedHolder.lives > 0) {
                if (holderReference.compareAndSet(observedHolder, new MsgHolder<>(observedHolder.msg, observedHolder.lives - 1)))
                    return observedHolder.msg;
            } else
            return null;
        }
    }
}