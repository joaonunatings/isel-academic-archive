import org.junit.jupiter.api.Test;
import utils.Base64Stream;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class Base64StreamTest {

    @Test
    public void encode_then_decode() throws IOException {
        String input = "Hello World! This is a test. How are you? I'm fine. Thanks.";
        String filename = "files/output/base64test";
        Base64Stream b64Out = new Base64Stream(filename);
        b64Out.write(input.getBytes());
        b64Out.close();

        Base64Stream b64In = new Base64Stream(filename);
        byte[] buffer = new byte[10];
        byte[] output = new byte[input.length()];
        int read;
        int lastRead = 0;
        while ((read = b64In.read(buffer)) != -1) {
            System.arraycopy(buffer, 0, output, lastRead, read);
            lastRead += read;
        }
        b64In.close();

        assertEquals(input, new String(output));
    }
}
