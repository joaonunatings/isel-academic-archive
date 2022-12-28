package pt.isel.pc.problemsets.set1;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.isel.pc.problemsets.utils.TestHelper;
import pt.isel.pc.problemsets.utils.TestUtils;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test file containing all tests for {@link BlockingMessageQueue} class.
 */
public class BlockingMessageQueueTests {

    private static final Logger log = LoggerFactory.getLogger(MessageBoxTests.class);
    private static BlockingMessageQueue<String> bmq;

    private static final Duration TEST_DURATION = Duration.ofSeconds(10);
    private static final int N_OF_THREADS = 10;

    @Rule
    public TestRule watcher = new TestWatcher() {

        @Override
        protected void starting(Description description) {
            log.info("Starting test: " + description.getMethodName());
        }

        @Override
        protected void finished(Description description) {
            bmq = null;
            log.info("Ending test: " + description.getMethodName());
        }
    };

    @Test
    public final void one_enqueue_one_dequeue_test() throws InterruptedException {
        bmq = new BlockingMessageQueue<>(1);
        var message = TestUtils.createMessage();

        TestUtils.ExceptionSensibleThread producerThread = new TestUtils.ExceptionSensibleThread( () -> {
            try {
                log.info("Enqueue message: {}", message);
                boolean res = bmq.enqueue(message, TEST_DURATION.toMillis());
                assertTrue(res);
            } catch (InterruptedException e) {
                throw new AssertionError("Thread was interrupted in uninterruptible test");
            }
        });

        TestUtils.ExceptionSensibleThread consumerThread = new TestUtils.ExceptionSensibleThread( () -> {
            log.info("Dequeue message...");
            var receivedFuture = bmq.dequeue();
            try {
                var receivedMessage = receivedFuture.get();
                log.info("Message received: {}", receivedMessage);
                assertEquals(message, receivedMessage);
            } catch (InterruptedException | ExecutionException e) {
                log.info("{}", e.toString());
                throw new AssertionError("Should not reach here.");
            }
        });

        producerThread.start();
        TestUtils.sleep(TEST_DURATION.dividedBy(2));
        consumerThread.start();

        producerThread.join();
        consumerThread.join();
    }

    @Test
    public final void one_dequeue_one_enqueue_test() throws InterruptedException {
        bmq = new BlockingMessageQueue<>(1);

        var message = TestUtils.createMessage();

        TestUtils.ExceptionSensibleThread producerThread = new TestUtils.ExceptionSensibleThread( () -> {
            try {
                log.info("Enqueue message: {}", message);
                boolean res = bmq.enqueue(message, TEST_DURATION.toMillis());
                assertTrue(res);
            } catch (InterruptedException e) {
                throw new AssertionError("Thread was interrupted in uninterruptible test");
            }
        });

        TestUtils.ExceptionSensibleThread consumerThread = new TestUtils.ExceptionSensibleThread( () -> {
            log.info("Dequeue message...");
            var receivedFuture = bmq.dequeue();
            try {
                var receivedMessage = receivedFuture.get();
                log.info("Message received: {}", receivedMessage);
                assertEquals(message, receivedMessage);
            } catch (InterruptedException | ExecutionException e) {
                log.info("{}", e.toString());
                throw new AssertionError("Should not reach here.");
            }
        });

        consumerThread.start();
        TestUtils.sleep(TEST_DURATION.dividedBy(2));
        producerThread.start();

        producerThread.join();
        consumerThread.join();
    }

    @Test
    public final void one_dequeue_two_enqueues_full_capacity_test() throws InterruptedException {
        bmq = new BlockingMessageQueue<>(1);

        var messages = TestUtils.createMessages(2);

        TestUtils.ExceptionSensibleThread producerThread = new TestUtils.ExceptionSensibleThread( () -> {
            try {
                log.info("Enqueue first message: {}", messages.get(0));
                boolean res = bmq.enqueue(messages.get(0), TEST_DURATION.toMillis());
                assertTrue(res);

                log.info("Enqueue second message: {}", messages.get(1));
                res = bmq.enqueue(messages.get(1), TEST_DURATION.toMillis());
                assertTrue(res);
            } catch (InterruptedException e) {
                throw new AssertionError("Thread was interrupted in uninterruptible test");
            }
        });

        TestUtils.ExceptionSensibleThread consumerThread = new TestUtils.ExceptionSensibleThread( () -> {
            log.info("Dequeue message...");
            var receivedFuture = bmq.dequeue();
            try {
                var receivedMessage = receivedFuture.get();
                log.info("Message received: {}", receivedMessage);
                assertEquals(messages.get(0), receivedMessage);
            } catch (InterruptedException | ExecutionException e) {
                log.info("{}", e.toString());
                throw new AssertionError("Should not reach here.");
            }
        });

        consumerThread.start();
        TestUtils.sleep(TEST_DURATION.dividedBy(2));
        producerThread.start();

        producerThread.join();
        consumerThread.join();
    }

    @Test
    public void one_dequeue_timeout_one_enqueue_test() throws InterruptedException {
        bmq = new BlockingMessageQueue<>(2);

        var message = TestUtils.createMessage();

        TestUtils.ExceptionSensibleThread producerThread = new TestUtils.ExceptionSensibleThread( () -> {
            try {
                log.info("Enqueue message: {}", message);
                boolean res = bmq.enqueue(message, TEST_DURATION.toMillis());
                assertTrue(res);
            } catch (InterruptedException e) {
                throw new AssertionError("Thread was interrupted in uninterruptible test");
            }
        });

        TestUtils.ExceptionSensibleThread consumerThread = new TestUtils.ExceptionSensibleThread( () -> {
            log.info("Dequeue message...");
            var receivedFuture = bmq.dequeue();
            try {
                receivedFuture.get(TEST_DURATION.toMillis() / 2, TimeUnit.MILLISECONDS);
                throw new AssertionError("Should not reach here.");
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.info("Thread timeout");
            }
        });

        consumerThread.start();
        TestUtils.sleep(TEST_DURATION);
        producerThread.start();

        producerThread.join();
        consumerThread.join();
    }

    @Test
    public void order_of_enqueue_test() throws InterruptedException {
        bmq = new BlockingMessageQueue<>(N_OF_THREADS);
        var messages = TestUtils.createMessages(N_OF_THREADS);
        ConcurrentLinkedQueue<String> messageQueue = new ConcurrentLinkedQueue<>(messages);
        ConcurrentLinkedQueue<String> receivedMessages = new ConcurrentLinkedQueue<>();

        TestHelper helper = new TestHelper(TEST_DURATION);

        log.info("Creating producer threads...");
        helper.createAndStart(0, (index, isDone) -> {
            while(!messageQueue.isEmpty()) {
                assertTrue(bmq.enqueue(messageQueue.poll(), TEST_DURATION.toMillis()));
            }
        });

        TestUtils.sleep(TEST_DURATION.dividedBy(2));

        log.info("Creating consumer threads...");
        helper.createAndStart(0, (ignore, isDone) -> {
            while (receivedMessages.size() < messages.size()) {
                receivedMessages.add(bmq.dequeue().get());
            }
        });

        helper.join();

        assertEquals(messages.toString(), receivedMessages.toString());
    }

    @Test
    public void order_of_enqueue_requests_test() throws InterruptedException {
        bmq = new BlockingMessageQueue<>(1);
        var messages = TestUtils.createMessages(N_OF_THREADS);
        ConcurrentLinkedQueue<String> messageQueue = new ConcurrentLinkedQueue<>(messages);
        ConcurrentLinkedQueue<String> receivedMessages = new ConcurrentLinkedQueue<>();

        TestHelper helper = new TestHelper(TEST_DURATION);

        log.info("Creating producer threads...");
        helper.createAndStart(0, (index, isDone) -> {
            while(!messageQueue.isEmpty()) {
                assertTrue(bmq.enqueue(messageQueue.poll(), TEST_DURATION.toMillis()));
            }
        });

        log.info("Creating consumer thread...");
        helper.createAndStart(0, (ignore, isDone) -> {
            while (receivedMessages.size() < messages.size()) {
                var receivedFuture = bmq.dequeue();
                var receivedMessage = receivedFuture.get();
                receivedMessages.add(receivedMessage);
                TestUtils.sleep(TEST_DURATION.dividedBy(messages.size()));
            }
        });

        helper.join();

        assertEquals(messages.toString(), receivedMessages.toString());
    }

    @Test
    public final void one_dequeue_cancel_test() throws InterruptedException, CancellationException {
        bmq = new BlockingMessageQueue<>(1);
        AtomicReference<Future<String>> receivedFutureRef = new AtomicReference<>(null);

        TestUtils.ExceptionSensibleThread consumerThread = new TestUtils.ExceptionSensibleThread( () -> {
            try {
                log.info("Dequeue message...");
                receivedFutureRef.set(bmq.dequeue());
                log.info("Getting message... (Future.get())");
                receivedFutureRef.get().get();
                throw new AssertionError("Should not reach here.");
            } catch (CancellationException | InterruptedException | ExecutionException e) {
                log.info("Future.get() cancelled. Error message: {}", e.toString());
                assert e instanceof CancellationException;
            }
        });

        consumerThread.start();
        TestUtils.sleep(TEST_DURATION.dividedBy(2));
        log.info("Cancelling Future.get()...");
        boolean res = receivedFutureRef.get().cancel(false);
        assertTrue(res);

        consumerThread.join();
    }

    @Test
    public final void one_dequeue_interrupted_test() throws InterruptedException {
        bmq = new BlockingMessageQueue<>(1);

        TestUtils.ExceptionSensibleThread consumerThread = new TestUtils.ExceptionSensibleThread( () -> {
            try {
                log.info("Dequeue message...");
                var receivedFuture = bmq.dequeue();
                log.info("Getting message... (Future.get())");
                receivedFuture.get();
                throw new AssertionError("Should not reach here.");
            } catch (CancellationException | InterruptedException | ExecutionException e) {
                log.info("Future.get() interrupted. Error message: {}", e.toString());
                assert e instanceof InterruptedException;
            }
        });

        consumerThread.start();
        TestUtils.sleep(TEST_DURATION.dividedBy(2));
        consumerThread.interrupt();
        consumerThread.join();
    }
}
