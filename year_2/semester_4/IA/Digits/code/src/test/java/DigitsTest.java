import neuralnetwork.Digits;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sample.SampleLoader;

import java.io.IOException;

public class DigitsTest {

    private static final String testSetFilePath = "src/test/resources/mnist_test.csv";

    @Test
    @DisplayName("Test with first sample (number 7)")
    void testWithFirstSampleNumber7() throws IOException {
        var sample = SampleLoader.load(testSetFilePath, 1);
        var digits = new Digits(0.3, 784, 200, 10);
        digits.train(sample);
    }
    
    @Test
    @DisplayName("Test with first and second sample (number 7 and 2)")
    void testWithFirstAndSecondSampleNumber7And2() throws IOException {
        var samples = SampleLoader.load(testSetFilePath, 2);
        var digits = new Digits(0.3, 784, 200, 10);
        digits.train(samples);
    }

    @Test
    @DisplayName("Test with all testing samples")
    void testWithAllTestingSamples() throws IOException {
        var samples = SampleLoader.load(testSetFilePath);
        var digits = new Digits(0.3, 784, 200, 10);
        digits.train(samples);
    }


}
