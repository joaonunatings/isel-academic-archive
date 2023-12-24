package sokoban.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import sokoban.model.entities.Box;
import sokoban.model.entities.Floor;
import sokoban.model.entities.Piece;
import sokoban.model.entities.Player;
import util.Direction;
import util.Pos;

import java.util.HashSet;

/**
 * Object which represents the board and its pieces.
 * Has all the methods to move pieces and check if the game is finished.
 */
@SuppressWarnings("DuplicatedCode")
@Getter
@Setter
public class Board implements Cloneable {
    @NonNull
    private Piece[][] pieces;
    private int height, width;
    private Player player;
    private int playerMoves = 0;
    private HashSet<Box> boxes = new HashSet<>();
    private HashSet<Piece> goals = new HashSet<>();
    private int numberOfGoals, boxesOnGoals;

    public Board(int height, int width) {
        if (height < 0 || width < 0)
            throw new IllegalArgumentException("Height and width must be positive.");
        this.height = height;
        this.width = width;
        pieces = new Piece[height][width];
    }

    public Piece getPiece(Pos pos) {
        return pieces[pos.getY()][pos.getX()];
    }

    public void setPiece(Piece piece) {
        pieces[piece.getPos().getY()][piece.getPos().getX()] = piece;
    }

    /**
     * Moves player to given direction. This does not check for any borders/collisions.
     * @param direction direction to move player
     */
    public void move(Direction direction) {
        Pos toPos = direction.getPos(player.getPos());
        Piece piece = getPiece(toPos);
        Floor floor = new Floor(player.getPos().clone(), player.isGoal());

        player.setGoal(piece.isGoal());
        player.setPos(toPos);

        if (piece instanceof Box) {
            Box box = (Box) piece;
            moveBox(box, direction);
            box.getHistory().add(direction);
        }

        setPiece(floor);
        setPiece(player);
    }

    /**
     * Check if player can move to given direction.
     * @param direction direction to check movement
     * @return true if player can move to given direction, false otherwise
     */
    public boolean canMove(Direction direction) {
        Pos toPos = direction.getPos(player.getPos());
        if (!(toPos.getX() > 0 && toPos.getX() < getWidth() - 1 && toPos.getY() > 0 && toPos.getY() < getHeight() - 1)) return false;

        Piece piece = getPiece(toPos);
        if (piece.isWalkable()) return true;

        // Only boxes can be moved
        if (!(piece instanceof Box)) return false;

        // Check if box can be moved
        Box box = (Box) piece;
        return canBoxMove(box, direction);
    }

    /**
     * Try move player to given direction (check if he can move, if he can, move him).
     * @param direction direction to move player
     * @return true if player was moved, false otherwise
     */
    public boolean tryMove(Direction direction) {
        if (!canMove(direction)) return false;

        move(direction);
        playerMoves++;
        return true;
    }

    /**
     * Check if player can move to given direction.
     * @param direction direction to move player
     * @param times number of times to move player
     * @return true if player can move to given direction, false otherwise
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean tryMove(Direction direction, int times) {
        for (int i = 0; i < times; i++) {
            if (!tryMove(direction)) return false;
        }
        return true;
    }

    /**
     * Check if player can pull box.
     * Player can only pull if it's touching a box which the last movement is equal to the given inverse direction.
     * @param direction direction to pull box
     * @return true if player can pull box, false otherwise
     */
    public boolean canPull(Direction direction) {
        Pos toPos = direction.getPos(player.getPos());
        if (!(toPos.getX() > 0 && toPos.getX() < getWidth() - 1 && toPos.getY() > 0 && toPos.getY() < getHeight() - 1)) return false;

        // Piece cannot be movable when pulling or if it's not walkable (i.e.: box, wall)
        Piece piece = getPiece(toPos);
        if (piece.isMovable() || !piece.isWalkable()) return false;

        // Player can only pull boxes
        Piece pullingPiece = getPiece(direction.inverse().getPos(player.getPos()));
        if (!(pullingPiece instanceof Box)) return false;

        // Check if box can be pulled based on its history
        Box box = (Box) pullingPiece;
        return box.getHistory().size() > 0 && box.getHistory().getLast() == direction.inverse();
    }

    /**
     * Try pull a box to given direction (check if player can pull, if he can, pull it).
     * @param direction direction to pull box
     * @return true if box was pulled, false otherwise
     */
    public boolean tryPull(Direction direction) {
        if (!canPull(direction)) return false;

        Box box = (Box) getPiece(direction.inverse().getPos(player.getPos()));
        move(direction);
        moveBox(box, direction);
        box.getHistory().removeLast();

        playerMoves--;
        return true;
    }

    /**
     * Check if player can pull box.
     * @param direction direction to pull box
     * @param times number of times to pull box
     * @return true if player can pull box, false otherwise
     */
    public boolean tryPull(Direction direction, int times) {
        for (int i = 0; i < times; i++) {
            if (!tryPull(direction)) return false;
        }
        return true;
    }

    /**
     * Moves box to given direction. This does not check for any borders/collisions.
     * @param box box to move
     * @param direction direction to move box
     */
    public void moveBox(Box box, Direction direction) {
        Pos toPos = direction.getPos(box.getPos());
        Piece piece = getPiece(toPos);
        boolean wasGoal = box.isGoal();
        boolean isGoal = piece.isGoal();
        Floor floor = new Floor(box.getPos().clone(), box.isGoal());

        box.setGoal(piece.isGoal());
        box.setPos(toPos);

        setPiece(floor);
        setPiece(box);

        if (wasGoal && !isGoal) boxesOnGoals--;
        if (!wasGoal && isGoal) boxesOnGoals++;
    }

    /**
     * Check if box can be moved to given direction. Check for borders/collisions.
     * @param box box to move
     * @param direction direction to move box
     * @return true if box can be moved to given direction, false otherwise
     */
    public boolean canBoxMove(Box box, Direction direction) {
        Pos toPos = direction.getPos(box.getPos());
        if (!(toPos.getX() > 0 && toPos.getX() < getWidth() - 1 && toPos.getY() > 0 && toPos.getY() < getHeight() - 1)) return false;

        // Piece is walkable and is not a box (i.e.: box with another box in the way)
        Piece piece = getPiece(toPos);
        return piece.isWalkable() && !(piece instanceof Box);
    }


    /**
     * Check if board is solved.
     * @return true if board is solved, false otherwise
     */
    public boolean isWin() {
        return boxesOnGoals == numberOfGoals;
    }

    /**
     * Optimized equals, this only checks equality in moving pieces.
     * Only use this method on the same initial board (but different player or boxes state).
     * @param obj object to compare
     * @return true if objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Board)) return false;

        Board other = (Board) obj;
        if (!player.equals(other.player)) return false;
        return boxes.containsAll(other.boxes);
    }

    @Override
    @SneakyThrows
    public Board clone() {
        Board clone = (Board) super.clone();

        HashSet<Box> boxes = new HashSet<>();
        HashSet<Piece> goals = new HashSet<>();

        // Clone pieces
        Pos pos = new Pos(0, 0);
        Piece[][] board = new Piece[getHeight()][getWidth()];
        for (int y = 0; y < getHeight(); y++) {
            pos.setY(y);
            for (int x = 0; x < getWidth(); x++) {
                pos.setX(x);
                Piece piece = getPiece(pos).clone();
                board[y][x] = piece;
                if (piece.isGoal()) goals.add(piece);
                if (piece instanceof Box) boxes.add((Box) piece);
                if (piece instanceof Player) clone.setPlayer((Player) piece);
            }
        }
        clone.setPieces(board);
        clone.setBoxes(boxes);
        clone.setGoals(goals);
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Piece[] pieces : pieces) {
            for (Piece piece : pieces) {
                sb.append(piece);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
