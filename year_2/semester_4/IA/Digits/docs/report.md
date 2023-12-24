# Digits - Neural Network for identifying hand-written digits

Developed by G06 (João Nunes - 47220 & Alexandre Luís - 47222) for Artificial Intelligence course @ ISEL (2021/2022).

**Note:** Please use test and training sets from this [repo](https://pjreddie.com/projects/mnist-in-csv/).

## Report
In this section there will be a brief explanation of the developed code.
There is utilitary classes used for helping with searching in the nodes of the neural network. In this case, _Node_, _Edge_ and _Graph_ represent the neurons, connections between neurons (and their weights) and the neural network itself (all nodes and edges arranged), respectively.

The code for training runs 3 steps:
1. Create a neural network with the given number of neurons in each layer.
2. Create a training set with the given number of samples.
3. Train the neural network with the training set.
   1. For each sample the network makes use of foward propagation, which forwards each neuron's output to the next layer.
   2. After this, the network makes use of back propagation, which updates the weights of the edges between the neurons.
   3. These steps are repeated for each sample.