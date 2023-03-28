import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ChessBoard {
    public static final int MIN = 1;
    public static final int MAX = 8;
    private static final Map<String, String> CASTLE_MAP = Map.of("f1", "h1",
            "d1", "a1",
            "f8", "h8",
            "d8", "a8");
    private final HashMap<String, Square> board = new HashMap<>(64);
    private final HashMap<String, Square> fakeSquares = new HashMap<>();

    public Set<Piece> getOutOfTheBoardPieces() {
        return outOfTheBoardPieces;
    }

    private final Set<Piece> outOfTheBoardPieces = new HashSet<>();

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

        chessBoard.add(new King(Color.WHITE), "e1");
        chessBoard.add(new King(Color.BLACK), "e8");
        chessBoard.add(new Queen(Color.WHITE), "d1");
        chessBoard.add(new Queen(Color.BLACK), "d8");
        chessBoard.add(new Bishop(Color.WHITE), "c1");
        chessBoard.add(new Bishop(Color.WHITE), "f1");
        chessBoard.add(new Bishop(Color.BLACK), "f8");
        chessBoard.add(new Bishop(Color.BLACK), "c8");
        chessBoard.add(new Knight(Color.WHITE), "b1");
        chessBoard.add(new Knight(Color.WHITE), "g1");
        chessBoard.add(new Knight(Color.BLACK), "b8");
        chessBoard.add(new Knight(Color.BLACK), "g8");
        chessBoard.add(new Rock(Color.WHITE), "a1");
        chessBoard.add(new Rock(Color.WHITE), "h1");
        chessBoard.add(new Rock(Color.BLACK), "a8");
        chessBoard.add(new Rock(Color.BLACK), "h8");
        IntStream.rangeClosed(MIN, MAX)
                .mapToObj(i -> BoardUtil.posToSquare(i, MIN + 1))
                .forEach(position -> chessBoard.add(new Pawn(Color.WHITE), position));
        IntStream.rangeClosed(MIN, MAX)
                .mapToObj(i -> BoardUtil.posToSquare(i, MAX - 1))
                .forEach(position -> chessBoard.add(new Pawn(Color.BLACK), position));
        return chessBoard;
    }

    public Square at(String position) {
        if (invalidPosition(position)) throw new IllegalArgumentException();
        if (fakeSquares.containsKey(position)) return fakeSquares.get(position);
        board.putIfAbsent(position, new Square(position));
        return board.get(position);
    }

    @Deprecated
    public Piece[][] getBoard() {
        return null;
    }

    public void add(Piece piece, String position) {
        piece.setSquare(at(position));
        at(position).setPiece(piece);
    }

    public boolean emptyPath(Piece piece, String squareToGo) {
        return piece.squaresOnThePath(squareToGo).stream()
                .map(this::at)
                .map(Square::getPiece)
                .allMatch(Optional::isEmpty);
    }

    public boolean tryToMove(String from, String to) {
        return tryToMove(at(from).getPiece().get(), to);
    }

    public boolean tryToMove(Piece piece, String positionToMoveOn) {
        if (piece.getPosition().equals(positionToMoveOn)) throw new IllegalArgumentException();
        if (canMove(piece, positionToMoveOn)) {
            at(positionToMoveOn).getPiece().ifPresent(this::movePieceOutOfTheBoard);
            tryToCastle(piece, positionToMoveOn);
            if (piece instanceof Castlable) ((Castlable) piece).cannotCastleAnymore();
            move(piece, positionToMoveOn);

            return true;
        }
        else return false;
    }

    private void tryToCastle(Piece piece, String positionToMoveOn) {
        if ((piece instanceof King && ((King) piece).canCastle())) {
            if (piece.squaresOnThePath(positionToMoveOn).size() == 1) {
                String finalPositionOfRock = piece.squaresOnThePath(positionToMoveOn).stream().findFirst().get();
                String rockPosition = CASTLE_MAP.get(finalPositionOfRock);
                at(rockPosition)
                        .getPiece()
                        .filter(r -> r instanceof Rock)
                        .filter(r -> ((Rock) r).canCastle())
                        .ifPresent(rock -> move(rock, finalPositionOfRock));
            }
        }
    }

    private void move(Piece piece, String positionToMoveOn) {
        at(piece.getPosition()).removePiece();
        piece.setSquare(at(positionToMoveOn));
        at(positionToMoveOn).setPiece(piece);
    }

    private void movePieceOutOfTheBoard(Piece piece) {
        piece.setSquare(null);
        outOfTheBoardPieces.add(piece);
    }

    boolean canMove(Piece piece, String positionToMoveOn) {
        return piece.reachableSquares(positionToMoveOn, at(positionToMoveOn).getPiece().map(Piece::getColor))
                && emptyPath(piece, positionToMoveOn)
                && !createCheck(piece, positionToMoveOn)
                && !isAllyOnPosition(piece, positionToMoveOn)
                && isValidCastle(piece, positionToMoveOn);
    }

    boolean isValidCastle(Piece piece, String positionToMoveOn) {
        return true; // TODO
    }

    boolean isAllyOnPosition(Piece piece, String positionToMoveOn) {
        return at(positionToMoveOn).getPiece()
                .map(Piece::getColor)
                .map(color -> color == piece.getColor())
                .orElse(false);
    }

    // TODO refactor me
    boolean createCheck(Piece piece, String positionToMoveOn) {
        if (piece.getPosition().equals(positionToMoveOn)) throw new IllegalArgumentException();
        String oldPosition = piece.getPosition();
        Square fakeEmptySquare = new Square(oldPosition);
        Square fakeMovement = new Square(positionToMoveOn);
        fakeMovement.setPiece(piece);
        piece.setSquare(fakeMovement);
        fakeSquares.put(oldPosition, fakeEmptySquare);
        fakeSquares.put(positionToMoveOn, fakeMovement);
        assert fakeSquares.size() == 2;

        Optional<King> optionalKing = allyPieces(piece.getColor()).stream()
                .filter(p -> p instanceof King)
                .map(king -> (King) king)
                .findFirst();
        boolean enemyCanCheck = optionalKing
                .map(this::kingIsUnderAttack)
                .orElse(false);

        piece.setSquare(at(oldPosition));
        fakeSquares.remove(oldPosition);
        fakeSquares.remove(positionToMoveOn);
        assert fakeSquares.isEmpty();
        return enemyCanCheck;
    }

    private boolean kingIsUnderAttack(King king) {
        return enemyPieces(king.getColor()).stream()
                .filter(piece -> !fakeSquares.containsKey(piece.getPosition()))
                .anyMatch(enemyPiece -> enemyPiece.reachableSquares(king.getPosition(), Optional.of(king.getColor())) && emptyPath(enemyPiece, king.getPosition()));
    }

    private Set<Piece> allyPieces(Color allyColor) {
        return board.values().stream()
                .map(Square::getPiece)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(p -> p.getColor() == allyColor)
                .collect(Collectors.toSet());
    }

    private Set<Piece> enemyPieces(Color allyColor) {
        return board.values().stream()
                .map(Square::getPiece)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(p -> p.getColor() != allyColor)
                .collect(Collectors.toSet());
    }
}
