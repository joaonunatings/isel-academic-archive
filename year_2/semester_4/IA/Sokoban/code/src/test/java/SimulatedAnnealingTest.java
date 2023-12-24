import algorithms.sa.SimulatedAnnealing;
import algorithms.sa.State;
import org.junit.jupiter.api.Test;
import sokoban.LevelLoader;
import sokoban.model.Board;

public class SimulatedAnnealingTest {

    public static final String BASE_LEVELS_PATH = "src/test/resources/levels/";

    @Test
    public void one_box_test() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level5");
        SimulatedAnnealing sa = SimulatedAnnealing.builder(board).build();
        System.out.println("Starting temperature: " + sa.getTemperature());
        State bestState = sa.execute();
        System.out.println("Ending temperature: " + sa.getTemperature());
        System.out.println(bestState);
    }

    @Test
    public void execute_level_with_2_boxes_test() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level6");
        SimulatedAnnealing sa = SimulatedAnnealing.builder(board).build();
        System.out.println("Starting temperature: " + sa.getTemperature());
        State bestState = sa.execute();
        System.out.println("Ending temperature: " + sa.getTemperature());
        System.out.println(bestState);
    }

    @Test
    public void execute_level_with_3_boxes_test() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level8");
        SimulatedAnnealing sa = SimulatedAnnealing.builder(board)
                .maxIterations(5)
                .minTemperature(0.0001)
                .temperature(0.5)
                .maxTemperature(0.5)
                .coolingRate(0.00001)
                .build();
        System.out.println("Test starting with temperature: " + sa.getTemperature());
        State bestState = sa.execute();
        System.out.println("Test ended with temperature: " + sa.getTemperature());
        System.out.println("Best score: " + bestState.getScore());
        System.out.println("Is winning state: " + bestState.isWinState());
        System.out.println("Best state:");
        System.out.println(bestState);
    }

    @Test
    public void execute_level_with_2_boxes_and_walls_test() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level7");
        SimulatedAnnealing sa = SimulatedAnnealing.builder(board)
                .maxIterations(5)
                .minTemperature(0.0001)
                .temperature(1.0)
                .maxTemperature(0.5)
                .coolingRate(0.00001)
                .build();
        System.out.println("Test starting with temperature: " + sa.getTemperature());
        State bestState = sa.execute();
        System.out.println("Test ended with temperature: " + sa.getTemperature());
        System.out.println("Best score: " + bestState.getScore());
        System.out.println("Is winning state: " + bestState.isWinState());
        System.out.println("Best state:");
        System.out.println(bestState);
    }

    @Test
    public void execute_problem_level() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "problem");
        SimulatedAnnealing sa = SimulatedAnnealing.builder(board)
                .maxIterations(5)
                .minTemperature(1E-7)
                .temperature(1.0)
                .maxTemperature(1.0)
                .coolingRate(0.0001)
                .build();
        System.out.println("Test starting with temperature: " + sa.getTemperature());
        State bestState = sa.execute();
        System.out.println("Test ended with temperature: " + sa.getTemperature());
        System.out.println("Best score: " + bestState.getScore());
        System.out.println("Is winning state: " + bestState.isWinState());
        System.out.println("Best state:");
        System.out.println(bestState);
    }
}
