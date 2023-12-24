package algorithms.ga;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sokoban.model.Board;

import java.util.ArrayList;
import java.util.List;

@Builder(builderMethodName = "geneticAlgorithmBuilder")
@Getter
@Setter
public class GeneticAlgorithm {
    @Builder.Default private int maxIterations = 10000;
    @Builder.Default private int populationSize = 100;
    @Builder.Default private float crossoverProbability = 0.8f;
    @Builder.Default private float mutationProbability = 0.05f;

    private final Board initialBoard;
    private Generation currentGeneration;
    private long currentIteration;

    /**
     * Executes the genetic algorithm given the parameters defined in the builder.
     * Iterates until the maxIterations is reached (or infinite iterations if maxIterations == -1) or the best individual is found.
     * Steps of the algorithm:
     *  1. Move one step all individuals in generation
     *  2. Select best individuals from generation
     *  3. Crossover selected individuals in pairs
     *  4. Mutate individuals
     *  5. Evaluate generation
     * @return last generation
     */
    public Generation execute() {
        Generation initialGeneration = createInitialPopulation(populationSize);
        currentGeneration = initialGeneration.clone();
        currentIteration = 0;

        boolean infiniteLoop = maxIterations == -1;
        while (!currentGeneration.getBestIndividual().isWin()) {
            if (!infiniteLoop && currentIteration >= maxIterations) break;
            currentIteration++;

            currentGeneration = currentGeneration
                    .select()
                    .crossover(crossoverProbability)
                    .mutate(mutationProbability)
                    .move()
                    .evaluate();
        }

        return currentGeneration;
    }

    private Generation createInitialPopulation(int populationSize) {
        Individual initialIndividual = new Individual(initialBoard);
        List<Individual> individuals = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            Individual individual = initialIndividual.clone();
            individuals.add(individual);
        }
        return new Generation(individuals);
    }

    public static GeneticAlgorithmBuilder builder(Board initialBoard) {
        return geneticAlgorithmBuilder().initialBoard(initialBoard);
    }
}
