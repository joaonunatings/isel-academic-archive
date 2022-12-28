package pt.isel.pc.problemsets.set1;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.isel.pc.problemsets.set1.MessageBox.MessageBox;
import pt.isel.pc.problemsets.utils.TestHelper;
import pt.isel.pc.problemsets.utils.TestUtils;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test file containing all tests for {@link MessageBox} class.
 */
public class MessageBoxTests {

    private static final Logger log = LoggerFactory.getLogger(MessageBoxTests.class);
    private static MessageBox<String> mb;

    private static final int N_OF_THREADS = 10;
    private static final Duration TEST_DURATION = Duration.ofSeconds(10);

    @Rule
    public TestRule watcher = new TestWatcher() {

        @Override
        protected void starting(Description description) {
            mb = new MessageBox<>();
            log.info("Starting test: " + description.getMethodName());
        }

        @Override
        protected void finished(Description description) {
            mb = null;
            log.info("Ending test: " + description.getMethodName());
        }
    };

    private static class MessageCounter {
        final AtomicInteger nReceived = new AtomicInteger(0);
        final AtomicInteger nSent = new AtomicInteger(0);
        static AtomicInteger nTimeouts;

        public MessageCounter() {
            nTimeouts = new AtomicInteger(0);
        }

        public boolean isEqual() {
            return nReceived.get() == nSent.get();
        }

        @Override
        public String toString() {
            return "nReceived: " + nReceived + " | nSent: " + nSent + " | nTimeouts: " + nTimeouts;
        }
    }

    @Test
    public final void one_waiting_thread_one_message_thread_test() throws InterruptedException {
        var message = TestUtils.createMessage();

        TestUtils.ExceptionSensibleThread consumerThread = new TestUtils.ExceptionSensibleThread( () -> {
            try {
                log.info("Waiting for message...");
                var receivedMessage = mb.waitForMessage(TEST_DURATION.toMillis());
                assertTrue(receivedMessage.isPresent());
                log.info("Received message: {}", receivedMessage.get());
                assertEquals(message, receivedMessage.get());
            } catch (InterruptedException e) {
                throw new AssertionError("Thread was interrupted in uninterruptible test");
            }
        });

        TestUtils.ExceptionSensibleThread producerThread = new TestUtils.ExceptionSensibleThread( () -> {
            log.info("Sending message: {}", message);
            int nThreads = mb.sendToAll(message);
            assertEquals(1, nThreads);
        });

        consumerThread.start();
        TestUtils.sleep(TEST_DURATION.dividedBy(2));
        producerThread.start();

        consumerThread.join();
        producerThread.join();
    }

    @Test
    public final void one_waiting_thread_one_message_thread_timeout_test() throws InterruptedException {
        var message = TestUtils.createMessage();
        long timeoutInMillis = TEST_DURATION.toMillis() / 2;

        TestUtils.ExceptionSensibleThread consumerThread = new TestUtils.ExceptionSensibleThread( () -> {
            try {
                log.info("Waiting for message...");
                var receivedMessage = mb.waitForMessage(timeoutInMillis);
                assertTrue(receivedMessage.isEmpty());
                log.info("Timeout occurred, no message received.");
            } catch (InterruptedException e) {
                throw new AssertionError("Thread was interrupted in uninterruptible test");
            }
        });

        TestUtils.ExceptionSensibleThread producerThread = new TestUtils.ExceptionSensibleThread( () -> {
            log.info("Sending message: {}", message);
            int nThreads = mb.sendToAll(message);
            assertEquals(0, nThreads);
        });

        consumerThread.start();
        TestUtils.sleep(TEST_DURATION);
        producerThread.start();

        consumerThread.join();
        producerThread.join();
    }

    @Test
    public final void one_waiting_thread_interrupted_test() throws InterruptedException {
        var message = TestUtils.createMessage();
        long timeoutInMillis = TEST_DURATION.toMillis() / 2;

        TestUtils.ExceptionSensibleThread consumerThread = new TestUtils.ExceptionSensibleThread( () -> {
            Optional<String> receivedMessage = Optional.empty();
            try {
                log.info("Waiting for message...");
                receivedMessage = mb.waitForMessage(TEST_DURATION.toMillis());
                throw new AssertionError("Should never reach here, this test always throws InterruptedException");
            } catch (InterruptedException e) {
                log.info("Thread interrupted.");
            } finally {
                assertTrue(receivedMessage.isEmpty());
            }
        });

        TestUtils.ExceptionSensibleThread producerThread = new TestUtils.ExceptionSensibleThread( () -> {
            log.info("Sending message: {}", message);
            int nThreads = mb.sendToAll(message);
            assertEquals(0, nThreads);
        });

        consumerThread.start();
        TestUtils.sleep(Duration.ofMillis(timeoutInMillis));
        log.info("Interrupting thread...");
        consumerThread.interrupt();
        producerThread.start();

        consumerThread.join();
        producerThread.join();
    }

    @Test
    public final void multiple_waiting_threads_one_message_thread_test() throws InterruptedException {
        MessageCounter mc = new MessageCounter();
        var message = TestUtils.createMessage();
        final int numOfCreatedThreads = N_OF_THREADS * 100;

        TestHelper helper = new TestHelper(TEST_DURATION);

        log.info("Starting {} threads. Each is waiting for message...", numOfCreatedThreads);
        helper.createAndStartMultiple(numOfCreatedThreads, (ignore, isDone) -> {
            var receivedMessage = mb.waitForMessage(TEST_DURATION.toMillis());
            assertTrue(receivedMessage.isPresent());
            assertEquals(message, receivedMessage.get());
            mc.nReceived.getAndIncrement();
        });

        Thread producerThread = new Thread( () -> {
            log.info("Sending message: {}", message);
            int numOfThreads = mb.sendToAll(message);
            mc.nSent.getAndAdd(numOfThreads);
        });

        TestUtils.sleep(TEST_DURATION.dividedBy(2)); // Give time to start consumer threads
        producerThread.start();

        helper.join();
        producerThread.join();

        assertTrue(mc.isEqual());
        assertEquals(numOfCreatedThreads, mc.nReceived.get());
        log.info(mc.toString());
    }

    @Test
    public final void multiple_waiting_threads_one_message_thread_timeout_test() throws InterruptedException {
        MessageCounter mc = new MessageCounter();
        var message = TestUtils.createMessage();
        final int numOfCreatedThreads = N_OF_THREADS * 100;
        long timeout = TEST_DURATION.toMillis() / 2;

        TestHelper helper = new TestHelper(TEST_DURATION);

        log.info("Starting {} threads. Each is waiting for message for {} seconds...", numOfCreatedThreads, TEST_DURATION.toSeconds() / 2);
        helper.createAndStartMultiple(numOfCreatedThreads, (ignore, isDone) -> {
            var receivedMessage = mb.waitForMessage(timeout);
            assertTrue(receivedMessage.isEmpty());
            MessageCounter.nTimeouts.getAndIncrement();
        });

        Thread producerThread = new Thread( () -> {
            log.info("Sending message: {}", message);
            int numOfThreads = mb.sendToAll(message);
            assertEquals(0, numOfThreads);
        });

        log.info("Waiting {} seconds before sending message", TEST_DURATION.toSeconds());
        TestUtils.sleep(TEST_DURATION); // Give time to exceed timeout

        producerThread.start();

        helper.join();
        producerThread.join();

        assertTrue(mc.isEqual());
        assertEquals(numOfCreatedThreads, MessageCounter.nTimeouts.get());
        log.info(mc.toString());
    }

    @Test
    public final void multiple_threads_one_message_loop_test() throws InterruptedException {
        MessageCounter mc = new MessageCounter();
        var message = TestUtils.createMessage();

        TestHelper helper = new TestHelper(TEST_DURATION);
        log.info("Starting loop...");
        helper.createAndStartMultiple(N_OF_THREADS, (ignore, isDone) -> {
            while (!isDone.get()) {
                if (ThreadLocalRandom.current().nextBoolean()) {
                    int __nSentThreads = mb.sendToAll(message);
                    mc.nSent.getAndAdd(__nSentThreads);
                } else {
                    var res = mb.waitForMessage(ThreadLocalRandom.current().nextInt(0, 1000));
                    if (res.isPresent() && res.get().equals(message))
                        mc.nReceived.getAndIncrement();
                    else MessageCounter.nTimeouts.getAndIncrement();
                }
            }
        });

        helper.join();

        log.info("Ending loop...");
        log.info(mc.toString());

        assertTrue(mc.isEqual());
    }

    @Test
    public final void multiple_threads_multiple_messages_loop_test() throws InterruptedException {
        var messages = TestUtils.createMessages(N_OF_THREADS);
        Map<String, MessageCounter> messagesMap = new ConcurrentHashMap<>(N_OF_THREADS);
        messages.forEach((message) -> messagesMap.put(message, new MessageCounter()));
        TestHelper helper = new TestHelper(TEST_DURATION);

        log.info("Starting loop...");
        helper.createAndStartMultiple(N_OF_THREADS, (index, isDone) -> {
            while (!isDone.get()) {
                boolean isSendToAllThread = ThreadLocalRandom.current().nextBoolean();
                if (isSendToAllThread) {
                    int nSentThreads = mb.sendToAll(messages.get(index));
                    messagesMap.get(messages.get(index)).nSent.getAndAdd(nSentThreads);
                } else {
                    var res = mb.waitForMessage(ThreadLocalRandom.current().nextInt(0, 1000));
                    if (res.isPresent())
                        messagesMap.get(res.get()).nReceived.getAndIncrement();
                    else
                        MessageCounter.nTimeouts.getAndIncrement();
                }
            }
        });

        helper.join();
        log.info("Ending loop...");

        messages.forEach((message) -> {
            MessageCounter mc = messagesMap.get(message);
            assertTrue(mc.isEqual());
            log.info("message: {} | {}", message, mc.toString());
        });
    }

    @Test
    public final void multiple_threads_multiple_messages_with_interrupt_ending_loop_test() throws InterruptedException {
        var messages = TestUtils.createMessages(N_OF_THREADS);
        Map<String, MessageCounter> messagesMap = new ConcurrentHashMap<>(N_OF_THREADS);
        messages.forEach((message) -> messagesMap.put(message, new MessageCounter()));
        TestHelper helper = new TestHelper(TEST_DURATION);

        log.info("Starting loop...");
        helper.createAndStartMultiple(N_OF_THREADS, (index, isDone) -> {
            while (!isDone.get()) {
                boolean isSendToAllThread = ThreadLocalRandom.current().nextBoolean();

                if (isSendToAllThread) {
                    int nSentThreads = mb.sendToAll(messages.get(index));
                    messagesMap.get(messages.get(index)).nSent.getAndAdd(nSentThreads);
                } else {
                    var res = mb.waitForMessage(ThreadLocalRandom.current().nextInt(0, 1000));
                    if (res.isPresent())
                        messagesMap.get(res.get()).nReceived.getAndIncrement();
                    else
                        MessageCounter.nTimeouts.getAndIncrement();
                }
            }
        });

        TestUtils.sleep(TEST_DURATION.dividedBy(2));

        log.info("Interrupting threads...");
        helper.interruptAndJoin();
        log.info("Ending loop...");

        messages.forEach((message) -> {
            MessageCounter mc = messagesMap.get(message);
            assertTrue(mc.isEqual());
            log.info("message: {} | {}", message, mc.toString());
        });
    }

    @Test
    public final void multiple_threads_multiple_message_with_interrupts_loop_test() throws InterruptedException {
        var messages = TestUtils.createMessages(N_OF_THREADS);
        Map<String, MessageCounter> messagesMap = new ConcurrentHashMap<>(N_OF_THREADS);
        messages.forEach((message) -> messagesMap.put(message, new MessageCounter()));
        TestHelper helper = new TestHelper(TEST_DURATION);

        log.info("Starting loop...");
        helper.createAndStartMultiple(N_OF_THREADS, (index, isDone) -> {
            while (!isDone.get()) {
                boolean isSendToAllThread = ThreadLocalRandom.current().nextBoolean();
                boolean isToBeInterrupted = ThreadLocalRandom.current().nextInt(95) == 0; // 5%

                if (isToBeInterrupted) Thread.currentThread().interrupt();
                else if (isSendToAllThread) {
                    int nSentThreads = mb.sendToAll(messages.get(index));
                    messagesMap.get(messages.get(index)).nSent.getAndAdd(nSentThreads);
                } else {
                    var res = mb.waitForMessage(ThreadLocalRandom.current().nextInt(0, 1000));
                    if (res.isPresent())
                        messagesMap.get(res.get()).nReceived.getAndIncrement();
                    else
                        MessageCounter.nTimeouts.getAndIncrement();
                }
            }
        });

        TestUtils.sleep(TEST_DURATION.dividedBy(2));

        helper.interruptAndJoin();
        log.info("Ending loop...");

        messages.forEach((message) -> {
            MessageCounter mc = messagesMap.get(message);
            assertTrue(mc.isEqual());
            log.info("message: {} | {}", message, mc.toString());
        });
    }
}