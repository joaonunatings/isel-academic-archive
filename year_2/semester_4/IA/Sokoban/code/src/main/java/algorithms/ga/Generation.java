package algorithms.ga;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a generation of individuals.
 * Each generatiopn has a list of individuals of a given size.
 * Each generation has a totalFitness value.
 */
@Getter
@Setter
public class Generation implements Cloneable {

    private List<Individual> individuals;

    private Individual bestIndividual;

    private int totalFitness;

    public Generation(List<Individual> individuals) {
        this.individuals = individuals;
        int bestFitness = Integer.MAX_VALUE;
        for (Individual individual : individuals) {
            if (individual.getFitness() < bestFitness) {
                bestFitness = individual.getFitness();
                bestIndividual = individual;
            }
            totalFitness += individual.getFitness();
        }
    }

    /**
     * Moves individuals one step in their genetic code of moves.
     * @return generation
     */
    public Generation move() {
        for (Individual individual : individuals) {
            individual.move();
        }
        return this;
    }

    /**
     * Binary tournament selection.
     * Clones selected individual into a new generation for each tournament.
     * @return generation
     */
    public Generation select() {
        List<Individual> selectedIndividuals = new ArrayList<>(individuals.size());
        List<Integer> randomIndexes = Utils.listOfRandoms(0, individuals.size() - 1);
        for (int i = 0; i < individuals.size(); i++) {
            int randomIndex = randomIndexes.get(i);
            if (individuals.get(i).getFitness() < individuals.get(randomIndex).getFitness()) {
                selectedIndividuals.add(individuals.get(i).clone());
            } else {
                selectedIndividuals.add(individuals.get(randomIndex).clone());
            }
        }
        return new Generation(selectedIndividuals);
    }

    /**
     * One-point crossover of selected individuals based on a given probability.
     * @param crossoverProbability crossover probability
     * @return generation
     */
    public Generation crossover(float crossoverProbability) {
        List<Integer> randomIndexes = Utils.listOfRandoms(0, individuals.size() - 1);
        for (int i = 0; i < individuals.size(); i++) {
            int randomIndex = randomIndexes.get(i);
            if (Math.random() < crossoverProbability) {
                individuals.get(i).crossover(individuals.get(randomIndex));
            }
        }
        return this;
    }

    /**
     * Mutates the genetic code of the individuals based on given probability.
     * Individual gets mutated based on random probability ranging from 0% to mutation probability. This probability times the number of moves is the number of mutations to perform.
     * @param mutationProbability mutation probability
     * @return generation
     */
    public Generation mutate(float mutationProbability) {
        for (Individual individual : individuals) {
            double prob = Math.random();
            if (prob < mutationProbability) {
                individual.mutate((int)(individual.getMoves().size() * prob));
            }
        }
        return this;
    }

    /**
     * Evaluates the generation and updates the best individual and total fitness.
     * @return generation
     */
    public Generation evaluate() {
        Individual bestIndividual = individuals.get(0);
        int totalFitness = 0;
        for (Individual individual : individuals) {
            totalFitness += individual.getFitness();
            if (individual.getFitness() < bestIndividual.getFitness()) {
                bestIndividual = individual;
            }
        }
        setBestIndividual(bestIndividual);
        setTotalFitness(totalFitness);
        return this;
    }

    @Override
    @SneakyThrows
    public Generation clone() {
        Generation clone = (Generation) super.clone();
        List<Individual> clonedIndividuals = new ArrayList<>(individuals.size());
        for (Individual individual : individuals) {
            Individual newIndividual = individual.clone();
            if (individual == bestIndividual) {
                clone.setBestIndividual(newIndividual);
            }
            clonedIndividuals.add(newIndividual);
        }
        clone.setIndividuals(clonedIndividuals);
        return new Generation(clonedIndividuals);
    }
}
