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

        chessBoard.add(new King("e1", Color.WHITE), "e1");
        chessBoard.add(new King("e8", Color.BLACK), "e8");
        chessBoard.add(new Queen("d1", Color.WHITE), "d1");
        chessBoard.add(new Queen("d8", Color.BLACK), "d8");
        chessBoard.add(new Bishop("c1", Color.WHITE), "c1");
        chessBoard.add(new Bishop("f1", Color.WHITE), "f1");
        chessBoard.add(new Bishop("f8", Color.BLACK), "f8");
        chessBoard.add(new Bishop("c8", Color.BLACK), "c8");
        chessBoard.add(new Knight("b1", Color.WHITE), "b1");
        chessBoard.add(new Knight("g1", Color.WHITE), "g1");
        chessBoard.add(new Knight("b8", Color.BLACK), "b8");
        chessBoard.add(new Knight("g8", Color.BLACK), "g8");
        chessBoard.add(new Rock("a1", Color.WHITE), "a1");
        chessBoard.add(new Rock("h1", Color.WHITE), "h1");
        chessBoard.add(new Rock("a8", Color.BLACK), "a8");
        chessBoard.add(new Rock("h8", Color.BLACK), "h8");
        IntStream.rangeClosed(MIN, MAX)
                .mapToObj(i -> BoardUtil.posToSquare(i, MIN + 1))
                .forEach(position -> chessBoard.add(new Pawn(position, Color.WHITE), position)); // FIXME
        IntStream.rangeClosed(MIN, MAX)
                .mapToObj(i -> BoardUtil.posToSquare(i, MAX - 1))
                .forEach(position -> chessBoard.add(new Pawn(position, Color.BLACK), position));
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
