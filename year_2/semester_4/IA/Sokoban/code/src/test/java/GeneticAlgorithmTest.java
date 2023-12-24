import algorithms.ga.Generation;
import algorithms.ga.GeneticAlgorithm;
import algorithms.sa.SimulatedAnnealing;
import algorithms.sa.State;
import org.junit.jupiter.api.Test;
import sokoban.LevelLoader;
import sokoban.model.Board;

public class GeneticAlgorithmTest {

    public static final String BASE_LEVELS_PATH = "src/test/resources/levels/";

    @Test
    public void one_box_test() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level5");
        GeneticAlgorithm ga = GeneticAlgorithm.builder(board).build();
        printInitialSetup(ga);
        Generation lastGen = ga.execute();
        printFinal(ga, lastGen);
    }

    @Test
    public void two_boxes_test() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level6");
        GeneticAlgorithm ga = GeneticAlgorithm.builder(board).build();
        printInitialSetup(ga);
        Generation lastGen = ga.execute();
        printFinal(ga, lastGen);
    }

    @Test
    public void three_boxes_test() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level7");
        GeneticAlgorithm ga = GeneticAlgorithm.builder(board).build();
        printInitialSetup(ga);
        Generation lastGen = ga.execute();
        printFinal(ga, lastGen);
    }

    @Test
    public void multiple_boxes_on_goal_test() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level4");
        GeneticAlgorithm ga = GeneticAlgorithm.builder(board).build();
        printInitialSetup(ga);
        Generation lastGen = ga.execute();
        printFinal(ga, lastGen);
    }

    @Test
    public void complex_test() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "complex");
        GeneticAlgorithm ga = GeneticAlgorithm.builder(board).maxIterations(-1).build();
        printInitialSetup(ga);
        Generation lastGen = ga.execute();
        printFinal(ga, lastGen);
    }

    private void printInitialSetup(GeneticAlgorithm ga) {
        System.out.println("Initial setup:");
        System.out.println("Starting fitness: " + (ga.getInitialBoard().getBoxes().size() - ga.getInitialBoard().getBoxesOnGoals()));
        System.out.println("Population size: " + ga.getPopulationSize());
        System.out.println("Crossover probability: " + ga.getCrossoverProbability());
        System.out.println("Mutation probability: " + ga.getMutationProbability());
        System.out.println("Max iterations: " + (ga.getMaxIterations() == -1 ? "unlimited" : ga.getMaxIterations()));
        System.out.println("Initial board:\n" + ga.getInitialBoard());
    }

    private void printFinal(GeneticAlgorithm ga, Generation lastGen) {
        System.out.println("Ended with:");
        System.out.println("Best fitness: " + lastGen.getBestIndividual().getFitness());
        System.out.println("Total iterations: " + ga.getCurrentIteration());
        System.out.println("Best board:\n" + lastGen.getBestIndividual().getBoard());
    }

    @Test
    public void execute_problem_level() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "problem");
        GeneticAlgorithm ga = GeneticAlgorithm.builder(board).maxIterations(-1).build();
        printInitialSetup(ga);
        ga.execute();
        printFinal(ga, ga.getCurrentGeneration());
    }
}
