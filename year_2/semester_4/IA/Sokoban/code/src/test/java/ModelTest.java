import org.junit.jupiter.api.Test;
import sokoban.model.entities.Box;
import sokoban.model.entities.Piece;
import sokoban.model.entities.Player;
import util.Direction;
import util.Pos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SuppressWarnings("ALL")
public class ModelTest {

    @Test
    public void Pos_equals_test() {
        Pos pos = new Pos(1, 2);
        Pos pos2 = pos;
        pos2.setX(2);
        assertEquals(pos, pos2);
    }

    @Test
    public void Pos_clone_test() {
        Pos pos = new Pos(1, 2);
        Pos clone = pos.clone();
        pos.setX(2);
        assertNotEquals(pos, clone);
    }

    @Test
    public void Piece_clone_test1() {
        Piece piece = new Piece(new Pos(1, 2), false, false, false);
        Piece clone = piece.clone();
        assertEquals(piece, clone);
    }

    @Test
    public void Piece_clone_test2() {
        Piece piece = new Piece(new Pos(1, 2), false, false, false);
        Piece clone = piece.clone();
        piece.setGoal(true);
        assertNotEquals(piece, clone);
    }

    @Test
    public void Piece_clone_test3() {
        Piece piece = new Piece(new Pos(1, 2), false, false, false);
        Piece clone = piece.clone();
        Pos pos = piece.getPos();
        pos.setX(2);
        assertNotEquals(piece, clone);
    }

    @Test
    public void Box_clone_test1() {
        Box box = new Box(new Pos(1, 1), false);
        Box clone = (Box) box.clone();
        assertEquals(box, clone);
    }

    @Test
    public void Box_clone_test2() {
        Box box = new Box(new Pos(1, 1), false);
        Box clone = (Box) box.clone();
        box.setGoal(true);
        assertNotEquals(box, clone);
    }

    @Test
    public void Box_clone_test3() {
        Box box = new Box(new Pos(1, 1), false);
        Box clone = (Box) box.clone();
        Pos pos = box.getPos();
        pos.setX(2);
        assertNotEquals(box, clone);
    }

    @Test
    public void Box_different_history_test() {
        Box box = new Box(new Pos(1, 1), false);
        Box clone = (Box) box.clone();
        assertEquals(box, clone);
        box.getHistory().add(Direction.RIGHT);
        assertNotEquals(box, clone);
    }

    @Test
    public void Player_clone_test1() {
        Player player = new Player(new Pos(1, 1), false);
        Player clone = (Player) player.clone();
        assertEquals(player, clone);
    }

    @Test
    public void Player_clone_test2() {
        Player player = new Player(new Pos(1, 1), false);
        Player clone = (Player) player.clone();
        player.setGoal(true);
        assertNotEquals(player, clone);
    }

    @Test
    public void Player_clone_test3() {
        Player player = new Player(new Pos(1, 1), false);
        Player clone = (Player) player.clone();
        Pos pos = player.getPos();
        pos.setX(2);
        assertNotEquals(player, clone);
    }
}
