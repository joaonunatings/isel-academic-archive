import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sample.SampleLoader;

import java.io.IOException;

public class SampleLoaderTest {

    private static final String trainSetFilePath = "src/test/resources/mnist_train.csv";
    private static final String testSetFilePath = "src/test/resources/mnist_test.csv";

    @Test
    @DisplayName("Load train set and print first sample")
    void loadTrainSetAndPrintFirstSample() throws IOException {
        var samples = SampleLoader.load(trainSetFilePath);
        System.out.println(samples.get(0));
    }

    @Test
    @DisplayName("Load test set and print first sample")
    void loadTestSetAndPrintFirstSample() throws IOException {
        var samples = SampleLoader.load(testSetFilePath);
        System.out.println(samples.get(0));
    }
}
