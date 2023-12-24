import org.junit.jupiter.api.Test;
import util.Direction;
import util.Utils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilsTest {

    @Test
    public void Direction_inverse_test() {
        assertEquals(Direction.UP, Direction.DOWN.inverse());
        assertEquals(Direction.LEFT, Direction.RIGHT.inverse());
        assertEquals(Direction.DOWN, Direction.UP.inverse());
        assertEquals(Direction.RIGHT, Direction.LEFT.inverse());
    }

    @Test
    public void listOfRandoms_test() {
        List<Integer> list = Utils.listOfRandoms(1, 10);
        System.out.println(list);
        assertEquals(10, list.size());
    }

    @Test
    public void listOfRandomsDirections_with_nulls_test() {
        List<Direction> list = Utils.listOfRandomDirections(10, 0.2f);
        System.out.println(list);
        assertEquals(10, list.size());
    }
}
