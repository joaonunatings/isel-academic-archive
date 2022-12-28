package pt.isel.pc.problemsets.set2;

import org.junit.Test;
import pt.isel.pc.problemsets.set2.Exchanger.IExchanger;
import pt.isel.pc.problemsets.set2.Exchanger.LockFreeExchanger;
import pt.isel.pc.problemsets.set2.Exchanger.MonitorExchanger;
import pt.isel.pc.problemsets.set2.Exchanger.CancellableLockFreeExchanger;
import pt.isel.pc.problemsets.utils.TestUtils;

import java.time.Duration;

import static org.junit.Assert.*;

public class ExchangerTests {

    private static final Duration TEST_DURATION = Duration.ofSeconds(10);
    private static final int N_OF_THREADS = 10;

    @Test
    public void two_threads_test() throws InterruptedException {
        IExchanger<String> lockFreeExchanger = new LockFreeExchanger<>();
        IExchanger<String> monitorExchanger = new MonitorExchanger<>();
        IExchanger<String> semiLockFreeExchanger = new CancellableLockFreeExchanger<>();

        String str1 = "str1", str2 = "str2";
        Thread thread1 = new Thread( () -> {
            try {
                String thread1_res = lockFreeExchanger.exchange(str1);
                assertEquals(str2, thread1_res);
                thread1_res = monitorExchanger.exchange(str2);
                assertEquals(str1, thread1_res);
                thread1_res = semiLockFreeExchanger.exchange(str1);
                assertEquals(str2, thread1_res);
            } catch (InterruptedException e) {
                // Ignore exceptions
            }
        });

        Thread thread2 = new Thread( () -> {
            try {
                String thread2_res = lockFreeExchanger.exchange(str2);
                assertEquals(str1, thread2_res);
                thread2_res = monitorExchanger.exchange(str1);
                assertEquals(str2, thread2_res);
                thread2_res = semiLockFreeExchanger.exchange(str2);
                assertEquals(str1, thread2_res);
            } catch (InterruptedException e) {
                // Ignore exceptions
            }
        });

        thread1.start();
        thread2.start();

        TestUtils.uninterruptibleJoin(thread1);
        TestUtils.uninterruptibleJoin(thread2);
    }
}
