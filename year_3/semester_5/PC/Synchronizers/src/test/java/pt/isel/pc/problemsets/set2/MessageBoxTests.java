package pt.isel.pc.problemsets.set2;

import org.junit.Test;
import pt.isel.pc.problemsets.utils.TestUtils;

import static org.junit.Assert.*;

public class MessageBoxTests {

    @Test
    public void two_threads_test() {
        MessageBox<String> messageBox = new MessageBox<>();
        String message = "Hello, World!";

        Thread thread1 = new Thread( () -> {
            messageBox.publish(message, 1);
        });

        Thread thread2 = new Thread( () -> {
            String receivedMessage = messageBox.tryConsume();
            assertEquals(message, receivedMessage);
        });

        thread1.start();
        thread2.start();

        TestUtils.uninterruptibleJoin(thread1);
        TestUtils.uninterruptibleJoin(thread2);
    }
}
