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
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.assertTrue;

public class KeyedThreadPoolExecutorTests {

    private static final Logger log = LoggerFactory.getLogger(MessageBoxTests.class);
    private static KeyedThreadPoolExecutor ktpe;

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
            ktpe = null;
            log.info("Ending test: " + description.getMethodName());
        }
    };

    @Test
    public final void execute_runnable_then_shutdown_test() throws InterruptedException {
        ktpe = new KeyedThreadPoolExecutor(1, 500);
        var key = TestUtils.createMessage();

        ktpe.execute( () -> log.info("Runnable running"), key);


        TestUtils.sleep(TEST_DURATION.dividedBy(2));
        log.info("Starting pool shutdown process...");
        ktpe.shutdown();

        assertTrue("Shutdown did not complete", ktpe.awaitTermination(Integer.MAX_VALUE));
    }

    @Test
    public void execute_multiple_runnables_then_shutdown_test() throws InterruptedException {
        ktpe = new KeyedThreadPoolExecutor(N_OF_THREADS / 2, 500);
        var keys = TestUtils.createMessages(N_OF_THREADS);
        ConcurrentLinkedQueue<String> completedKeys = new ConcurrentLinkedQueue<>();

        log.info("Executing runnables...");
        for(var key : keys) {
            ktpe.execute( () -> {
                TestUtils.sleep(TEST_DURATION.dividedBy(N_OF_THREADS));
                completedKeys.add(key);
            }, key);
        }

        TestUtils.sleep(TEST_DURATION.dividedBy(2));
        log.info("Starting pool shutdown...");
        ktpe.shutdown();

        assertTrue("Pool did not shutdown", ktpe.awaitTermination(Integer.MAX_VALUE));

        for(var key : keys) {
            assertTrue(completedKeys.contains(key));
        }
    }
}
