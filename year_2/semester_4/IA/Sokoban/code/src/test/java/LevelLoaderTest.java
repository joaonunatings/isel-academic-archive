import org.junit.jupiter.api.Test;
import sokoban.LevelLoader;
import sokoban.model.Board;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LevelLoaderTest {

    public static final String BASE_LEVELS_PATH = "src/test/resources/levels/";

    @Test
    public void LevelLoader_boxes_test() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level1");
        assertEquals(1, board.getBoxes().size());
    }

    @Test
    public void LevelLoader_boxesOnGoals_test() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level1");
        assertEquals(0, board.getBoxesOnGoals());
    }

    @Test
    public void LevelLoader_numberOfGoals_test() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level1");
        assertEquals(1, board.getNumberOfGoals());
    }

    @Test
    public void LevelLoader_goals_test() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level1");
        assertEquals(1, board.getGoals().size());
    }
}
