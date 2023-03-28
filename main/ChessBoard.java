import java.util.HashMap;
import java.util.stream.IntStream;

public class ChessBoard {
    public static final int MIN = 1;
    public static final int MAX = 8;
    private final HashMap<String, Square> board = new HashMap<>(64);

    private static boolean invalidPosition(String position) {
        try {
            boolean invalidLength = position.length() != 2;
            boolean validX = MIN <= BoardUtil.getX(position) && BoardUtil.getX(position) <= MAX;
            boolean validY = MIN <= BoardUtil.getY(position) && BoardUtil.getY(position) <= MAX;
            return invalidLength || !validX || !validY;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public static ChessBoard createEmpty() {
        return new ChessBoard();
    }

    public static ChessBoard createWithInitialState() {
        ChessBoard chessBoard = new ChessBoard();

        chessBoard.add(new King(5, 1, true, 'R'), "e1");
        chessBoard.add(new King(5, 8, false, 'r'), "e8");
        chessBoard.add(new Queen(4, 1, true, 'D'), "d1");
        chessBoard.add(new Queen(4, 8, false, 'd'), "d8");
        chessBoard.add(new Bishop(3, 1, true, 'F'), "c1");
        chessBoard.add(new Bishop(6, 1, true, 'F'), "f1");
        chessBoard.add(new Bishop(6, 8, false, 'f'), "f8");
        chessBoard.add(new Bishop(3, 8, false, 'f'), "c8");
        chessBoard.add(new Knight(2, 1, true, 'C'), "b1");
        chessBoard.add(new Knight(7, 1, true, 'C'), "g1");
        chessBoard.add(new Knight(2, 8, false, 'c'), "b8");
        chessBoard.add(new Knight(7, 8, false, 'c'), "g8");
        chessBoard.add(new Rock(1, 1, true, 'T'), "a1");
        chessBoard.add(new Rock(8, 1, true, 'T'), "h1");
        chessBoard.add(new Rock(1, 8, false, 't'), "a8");
        chessBoard.add(new Rock(8, 8, false, 't'), "h8");
        IntStream.rangeClosed(1, 8)
                .mapToObj(i -> BoardUtil.posToSquare(i, 2))
                .forEach(position -> chessBoard.add(new Pawn(1, 2, true, 'P'), position));
        IntStream.rangeClosed(1, 8)
                .mapToObj(i -> BoardUtil.posToSquare(i, 7))
                .forEach(position -> chessBoard.add(new Pawn(1, 7, false, 'p'), position));
        return chessBoard;
    }

    public Square at(String position) {
        if (invalidPosition(position)) throw new IllegalArgumentException();
        board.putIfAbsent(position, new Square(position));
        return board.get(position);
    }

    public Piece[][] getBoard() {
        return null;
    }

    public void add(Piece piece, String position) {
        piece.setSquare(at(position));
        at(position).setPiece(piece);
    }
}
