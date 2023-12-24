package neuralnetwork;

import sample.Sample;
import utils.Graph;
import utils.Node;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;

public class Digits {

    private final double learningRate;
    private final ArrayList<Node> inputLayer;
    private final ArrayList<Node> hiddenLayer;
    private final ArrayList<Node> outputLayer;

    private final Graph network;

    private final Function<Double, Double> activationFunction = (x) -> 1 / (1 + Math.exp(-x));
    private final Function<Double, Double> derivativeActivationFunction = (x) -> activationFunction.apply(x) * (1 - activationFunction.apply(x));

    public Digits(double learningRate, int inputLayerSize, int hiddenLayerSize, int outputLayerSize) {
        this.learningRate = learningRate;

        // Init layers
        inputLayer = new ArrayList<>(inputLayerSize);
        hiddenLayer = new ArrayList<>(hiddenLayerSize);
        outputLayer = new ArrayList<>(outputLayerSize);
        for (int i = 0; i < inputLayerSize; i++) inputLayer.add(new Node());
        for (int i = 0; i < hiddenLayerSize; i++) hiddenLayer.add(new Node());
        for (int i = 0; i < outputLayerSize; i++) outputLayer.add(new Node());

        // Init graph used for relations between nodes
        network = new Graph(inputLayerSize + hiddenLayerSize + outputLayerSize);

        // Connect nodes in network and assign weights
        var random = new Random();
        // Input layer -> hidden layer
        for (var inputNeuron : inputLayer)
            for (var hiddenNeuron : hiddenLayer)
                network.add(inputNeuron, hiddenNeuron, random.nextDouble());
        // Hidden layer -> output layer
        for (var hiddenNeuron : hiddenLayer)
            for (var outputNeuron : outputLayer)
                network.add(hiddenNeuron, outputNeuron, random.nextDouble());
    }

    public void train(List<Sample> samples) {
        long initialTime = System.currentTimeMillis();
        long totalCorrect = 0;
        for (int i = 1; i <= samples.size(); i++) {
            if (train(samples.get(i - 1))) totalCorrect++;
            System.out.println("Total trained: " + i);
            System.out.println("Total correct: " + totalCorrect);
            double successRate = (double) totalCorrect / i * 100;
            DecimalFormat numberFormat = new DecimalFormat("0.00");
            System.out.println("Success rate: " + numberFormat.format(successRate) + "%\n");
            System.out.flush();
        }

        System.out.println("Training time: " + (System.currentTimeMillis() - initialTime) + "ms");
    }

    private boolean train(Sample sample) {
        // Map sample data to input layer

        for (int y = 0; y < 28; y++)
            for (int x = 0; x < 28; x++) {
                var node = inputLayer.get(x + y * 28);
                var value = sample.getData()[y][x];
                node.setValue(value);
            }

        // Forward propagation for hidden layer
        for (var hiddenNode : hiddenLayer) {
            var edges = network.getEdgesContainingRight(hiddenNode);
            // Calculate weighted sum
            for (var edge : edges) {
                var value = hiddenNode.getValue() + edge.getLeftNode().getValue() * edge.getWeight();
                hiddenNode.setValue(value);
            }
            // Calculate activation value
            hiddenNode.setActivationValue(activationFunction.apply(hiddenNode.getValue()));
        }

        // Forward & backward propagation for output layer
        var maxActivationValue = Double.MIN_VALUE;
        int activatedNumber = -1;
        for (int i = 0; i < outputLayer.size(); i++) {
            // Forward propagation
            var outputNode = outputLayer.get(i);
            var edges = network.getEdgesContainingRight(outputNode);
            // Calculate weighted sum
            for (var edge : edges) {
                var value = outputNode.getValue() + edge.getLeftNode().getValue() * edge.getWeight();
                outputNode.setValue(value);
            }
            // Calculate activation value
            var activationValue = activationFunction.apply(outputNode.getValue());
            outputNode.setActivationValue(activationValue);

            // Backward propagation
            // Calculate error
            if (outputNode.getActivationValue() > maxActivationValue) {
                maxActivationValue = outputNode.getActivationValue();
                activatedNumber = i;
            }
            if (i == sample.getNumber()) outputNode.setError(1 - activationValue);
            else outputNode.setError(0 - activationValue);
            // Calculate delta
            outputNode.setDelta(derivativeActivationFunction.apply(outputNode.getValue()) * outputNode.getError());
        }

        // Backward propagation for hidden layer
        for (var hiddenNode : hiddenLayer) {
            var edges = network.getEdgesContainingRight(hiddenNode);
            // Calculate error
            for (var edge : edges) hiddenNode.setError(hiddenNode.getError() + edge.getWeight() * edge.getRightNode().getDelta());
            // Calculate delta
            hiddenNode.setDelta(derivativeActivationFunction.apply(hiddenNode.getValue()) * hiddenNode.getError());
        }

        // Update weights
        for (var hiddenNode : hiddenLayer) {
            var leftEdges = network.getEdgesContainingRight(hiddenNode);
            for (var edge : leftEdges) {
                var weight = edge.getWeight() + learningRate * hiddenNode.getDelta() * edge.getLeftNode().getActivationValue();
                if (weight != edge.getWeight()) System.out.println("Old weight: " + edge.getWeight() + " | New weight: " + weight);
                edge.setWeight(weight);
            }
            var rightEdges = network.getEdgesContainingLeft(hiddenNode);
            for (var edge : rightEdges) {
                var weight = edge.getWeight() + learningRate * edge.getLeftNode().getDelta() * hiddenNode.getActivationValue();
                if (weight != edge.getWeight()) System.out.println("Old weight: " + edge.getWeight() + " | New weight: " + weight);
                edge.setWeight(weight);
            }
        }
        return activatedNumber == sample.getNumber();
    }

    public void query(Sample sample) {
        // Map sample data to input layer
        for (int y = 0; y < 28; y++)
            for (int x = 0; x < 28; x++)
                inputLayer.set(x + y * 28, new Node(sample.getData()[y][x]));

        // Forward propagation for hidden layer
        for (var hiddenNode : hiddenLayer) {
            var edges = network.getEdgesContainingRight(hiddenNode);
            // Calculate weighted sum
            for (var edge : edges) {
                var value = hiddenNode.getValue() + edge.getLeftNode().getValue() * edge.getWeight();
                hiddenNode.setValue(value);
            }
            // Calculate activation value
            hiddenNode.setActivationValue(activationFunction.apply(hiddenNode.getValue()));
        }

        // Forward & backward propagation for output layer
        var maxActivationValue = Double.MIN_VALUE;
        int activatedNumber = -1;
        for (int i = 0; i < outputLayer.size(); i++) {
            // Forward propagation
            var outputNode = outputLayer.get(i);
            var edges = network.getEdgesContainingRight(outputNode);
            // Calculate weighted sum
            for (var edge : edges) {
                var value = outputNode.getValue() + edge.getLeftNode().getValue() * edge.getWeight();
                outputNode.setValue(value);
            }
            // Calculate activation value
            var activationValue = activationFunction.apply(outputNode.getValue());
            outputNode.setActivationValue(activationValue);

            // Backward propagation
            // Calculate error
            if (outputNode.getActivationValue() > maxActivationValue) {
                maxActivationValue = outputNode.getActivationValue();
                activatedNumber = i;
            }
        }
        System.out.println("Expected result: " + sample.getNumber());
        System.out.println("Actual result: " + activatedNumber);
    }
}
