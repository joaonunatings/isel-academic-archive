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
import java.util.concurrent.CancellationException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test file containing all tests for {@link SemaphoreWithShutdown} class.
 */
public class SemaphoreWithShutdownTests {

    private static final Logger log = LoggerFactory.getLogger(MessageBoxTests.class);
    private static SemaphoreWithShutdown sws;

    private static final int N_OF_THREADS = 10;
    private static final Duration TEST_DURATION = Duration.ofSeconds(10);

    @Rule
    public TestRule watcher = new TestWatcher() {

        @Override
        protected void starting(Description description) {
            log.info("Starting test: " + description.getMethodName());
        }

        @Override
        protected void finished(Description description) {
            sws = null;
            log.info("Ending test: " + description.getMethodName());
        }
    };

    @Test
    public final void acquire_then_release_single_test() throws InterruptedException {
        sws = new SemaphoreWithShutdown(1);
        TestHelper helper = new TestHelper(TEST_DURATION);

        log.info("Starting thread...");
        helper.createAndStart(0, (ignore, isDone) -> {
            while(!isDone.get()) {
                try {
                    assertTrue("There must be units in the semaphore", sws.acquireSingle(Long.MAX_VALUE));
                } finally {
                    sws.releaseSingle();
                }
            }
        });

        helper.join();
    }

    @Test
    public final void acquire_then_release_multiple_test() throws InterruptedException {
        sws = new SemaphoreWithShutdown(1);
        TestHelper helper = new TestHelper(TEST_DURATION);

        log.info("Starting {} threads...", N_OF_THREADS);
        helper.createAndStartMultiple(N_OF_THREADS, (ignore, isDone) -> {
            while(!isDone.get()) {
                try {
                    assertTrue("There must be units in the semaphore", sws.acquireSingle(Long.MAX_VALUE));
                } finally {
                    sws.releaseSingle();
                }
            }
        });

        helper.join();
    }

    @Test
    public final void acquire_then_release_multiple_shutdown_test() throws InterruptedException {
        sws = new SemaphoreWithShutdown(1);
        TestHelper helper = new TestHelper(TEST_DURATION);

        log.info("Starting {} threads...", N_OF_THREADS);
        helper.createAndStartMultiple(N_OF_THREADS, (ignore, isDone) -> {
            while(!isDone.get()) {
                try {
                    assertTrue("There must be units in the semaphore", sws.acquireSingle(Long.MAX_VALUE));
                } catch (CancellationException e) {
                    assertTrue("Shutdown did not complete", sws.waitShutdownCompleted(Long.MAX_VALUE));
                } finally {
                    sws.releaseSingle();
                }
            }
        });

        TestUtils.sleep(TEST_DURATION.dividedBy(2));
        log.info("Start semaphore shutdown...");
        sws.startShutdown();

        assertTrue("Shutdown did not complete", sws.waitShutdownCompleted(TEST_DURATION.toMillis()));

        helper.join();
    }

    @Test
    public final void acquire_multiple_then_wait_shutdown_test() throws InterruptedException {
        sws = new SemaphoreWithShutdown(N_OF_THREADS);
        AtomicInteger currUnits = new AtomicInteger(N_OF_THREADS);
        TestHelper helper = new TestHelper(TEST_DURATION);

        log.info("Starting {} threads...", N_OF_THREADS);
        helper.createAndStartMultiple(N_OF_THREADS, (ignore, isDone) -> {
            while(!isDone.get()) {
                boolean isWaitingForShutdown = ThreadLocalRandom.current().nextBoolean();
                boolean acquired = false;
                try {
                    acquired = sws.acquireSingle(ThreadLocalRandom.current().nextInt(1000));
                    assertTrue("There must be units in the semaphore", acquired);
                    currUnits.getAndDecrement();
                } catch (CancellationException e) {
                    // Ignore exception
                } finally {
                    if (acquired) {
                        sws.releaseSingle();
                        currUnits.getAndIncrement();
                    }
                    if (isWaitingForShutdown) {
                        assertTrue("Shutdown did not complete", sws.waitShutdownCompleted(Long.MAX_VALUE));
                    }
                }
            }
        });

        TestUtils.sleep(TEST_DURATION.dividedBy(2));
        log.info("Starting semaphore shutdown...");
        sws.startShutdown();
        assertTrue("Shutdown did not complete", sws.waitShutdownCompleted(TEST_DURATION.toMillis()));

        helper.join();

        assertEquals(N_OF_THREADS, currUnits.get());
    }
}