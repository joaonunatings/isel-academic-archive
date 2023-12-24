import neuralnetwork.Digits;
import sample.SampleLoader;

import java.io.IOException;

public class Main {

    private static double learningRate = 0.3;
    private static final String trainingSetFilePath = "src/main/resources/mnist_train.csv";

    public static void main(String[] args) throws IOException {
        System.out.println("Digits Neural Network - Developed by G06");
//        System.out.println("Usage: java -jar Digits.jar <learningRate> <trainSetFilePath>");
        if (args.length > 0) {
            learningRate = Double.parseDouble(args[0]);
        }
        var digits = new Digits(learningRate, 784, 200, 10);
        System.out.println("Loading samples... (Path: " + trainingSetFilePath + ")");
        var samples = SampleLoader.load(trainingSetFilePath);
        System.out.println("Samples loaded");
        System.out.println("Training...");
        digits.train(samples);
    }
}
