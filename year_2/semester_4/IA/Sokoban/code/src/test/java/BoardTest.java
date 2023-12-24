import org.junit.jupiter.api.Test;
import sokoban.LevelLoader;
import sokoban.model.Board;
import sokoban.model.entities.Box;
import sokoban.model.entities.Piece;
import util.Direction;
import util.Pos;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    public static final String BASE_LEVELS_PATH = "src/test/resources/levels/";

    @Test
    public void Board_clone_test() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level1");
        Board clone = board.clone();
        assertEquals(board, clone);
    }

    @Test
    public void Board_equals_test() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level1");
        Board clone = board.clone();
        Pos pos1 = new Pos(2, 1);
        Pos pos2 = new Pos(2, 2);
        Piece piece1 = board.getPiece(pos1); // Get box
        Piece piece2 = board.getPiece(pos2); // Get floor
        assertEquals(board, clone);
        piece1.setGoal(true);
        piece2.setGoal(false);
        assertNotEquals(board, clone);

        // Change positions
        piece1.setPos(pos2);
        piece2.setPos(pos1);

        // Set new pieces in board
        board.setPiece(piece1);
        board.setPiece(piece2);

        assertNotEquals(board, clone);
    }

    @Test
    public void moveBox_test() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level1");
        Board clone = board.clone();
        Box box = (Box) board.getBoxes().toArray()[0];
        assertEquals(board, clone);
        assertFalse(board.isWin());
        System.out.println(board);

        board.moveBox(box, Direction.RIGHT);
        assertNotEquals(board, clone);
        System.out.println(board);

        assertTrue(board.isWin());
        board.moveBox(box, Direction.LEFT);
        assertEquals(board, clone);
        System.out.println(board);
        assertFalse(board.isWin());
    }

    @Test
    public void movePlayer_test() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level1");
        Board clone = board.clone();
        Box box = (Box) board.getBoxes().toArray()[0];
        assertEquals(board, clone);
        assertFalse(board.isWin());
        System.out.println(board);

        board.move(Direction.RIGHT);
        assertNotEquals(board, clone);
        System.out.println(board);
        assertTrue(board.isWin());

        board.move(Direction.LEFT);
        assertNotEquals(board, clone);
        System.out.println(board);
        assertTrue(board.isWin());
    }

    @Test
    public void movePlayer_test2() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level2");
        board.move(Direction.LEFT);
        board.move(Direction.DOWN);
        board.move(Direction.DOWN);
        board.move(Direction.RIGHT);

        assertTrue(board.getPlayer().isGoal());
        System.out.println(board);

        board.move(Direction.LEFT);
        assertFalse(board.getPlayer().isGoal());
        System.out.println(board);
    }

    @Test
    public void tryMove_test() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level4");
        System.out.println(board);
        board.tryMove(Direction.DOWN);
        System.out.println(board);
        board.tryMove(Direction.UP);
        System.out.println(board);
        board.tryMove(Direction.LEFT);
        System.out.println(board);
    }

    @Test
    public void tryMove_test2() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level5");
        board.tryMove(Direction.DOWN, 4);
        System.out.println(board);
        board.tryMove(Direction.RIGHT);
        System.out.println(board);
        board.tryMove(Direction.UP);
        System.out.println(board);
    }


    @Test
    public void tryPull_test() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level4");
        System.out.println(board);
        board.tryPull(Direction.UP);
        System.out.println(board);
    }

    @Test
    public void tryMove_and_tryPull_test() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level4");
        System.out.println(board);
        board.tryMove(Direction.LEFT);
        System.out.println(board);
        board.tryPull(Direction.RIGHT);
        System.out.println(board);
        board.tryPull(Direction.RIGHT);
        System.out.println(board);
    }

    @Test
    public void tryMove_and_tryPull_test2() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level4");
        System.out.println(board);
        board.tryMove(Direction.UP);
        System.out.println(board);
        board.tryMove(Direction.LEFT);
        System.out.println(board);
        board.tryMove(Direction.DOWN);
        System.out.println(board);
        board.tryMove(Direction.DOWN);
        System.out.println(board);
        board.tryPull(Direction.UP);
        System.out.println(board);
        board.tryPull(Direction.LEFT);
        System.out.println(board);
        board.tryPull(Direction.UP);
        System.out.println(board);
        board.tryPull(Direction.UP);
        System.out.println(board);
    }

    @Test
    public void tryMove_and_tryPull_test3() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level5");
        board.tryMove(Direction.RIGHT, 4);
        board.tryMove(Direction.DOWN, 2);
        board.tryMove(Direction.LEFT, 2);
        board.tryMove(Direction.UP);
        board.tryMove(Direction.LEFT);
        board.tryMove(Direction.DOWN, 2);
        assertTrue(board.isWin());
        board.tryPull(Direction.UP);
        assertFalse(board.isWin());
    }

    @Test
    public void tryMove_and_tryPull_test4() {
        Board board = LevelLoader.load(BASE_LEVELS_PATH + "level6");
        board.tryMove(Direction.RIGHT, 3);
        board.tryMove(Direction.DOWN, 2);
        board.tryMove(Direction.RIGHT);
        board.tryMove(Direction.DOWN);
        board.tryMove(Direction.LEFT, 4);
        board.tryPull(Direction.RIGHT);
        System.out.println(board);
    }
}
