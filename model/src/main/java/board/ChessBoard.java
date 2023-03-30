package board;

import lombok.extern.slf4j.Slf4j;
import piece.*;
import position.PositionUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class ChessBoard {
    private static final int MIN = 1;
    private static final int MAX = 8;
    private static final Map<String, String> CASTLE_MAP = Map.of("f1", "h1",
            "d1", "a1",
            "f8", "h8",
            "d8", "a8");
    private final HashMap<String, Square> board = new HashMap<>(64);
    private final HashMap<String, Square> fakeSquares = new HashMap<>();
    private final Set<Piece> outOfTheBoardPieces = new HashSet<>();

    public static ChessBoard createEmpty() {
        log.debug("create empty chessboard");
        return new ChessBoard();
    }

    public static ChessBoard createWithInitialState() {
        log.debug("create chessboard with initial state");
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
                .mapToObj(i -> PositionUtil.posToSquare(i, MIN + 1))
                .forEach(position -> chessBoard.add(new WhitePawn(), position));
        IntStream.rangeClosed(MIN, MAX)
                .mapToObj(i -> PositionUtil.posToSquare(i, MAX - 1))
                .forEach(position -> chessBoard.add(new BlackPawn(), position));
        return chessBoard;
    }

    public void add(Piece piece, String position) {
        if (at(position).getPiece().isPresent())
            throw new IllegalArgumentException(String.format("Cannot add %s because %s is not empty", piece, position));
        piece.setSquare(at(position));
        at(position).setPiece(piece);
    }

    public Square at(String position) {
        if (invalidPosition(position)) throw new IllegalArgumentException("square is invalid");
        if (fakeSquares.containsKey(position)) return fakeSquares.get(position);
        board.putIfAbsent(position, new Square(position)); // TODO inline return ?
        return board.get(position);
    }

    private static boolean invalidPosition(String position) {
        try {
            boolean invalidLength = position.length() != 2;
            boolean validX = MIN <= PositionUtil.getX(position) && PositionUtil.getX(position) <= MAX;
            boolean validY = MIN <= PositionUtil.getY(position) && PositionUtil.getY(position) <= MAX;
            return invalidLength || !validX || !validY;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public static String twoSquaresForward(Pawn pawn) {
        if (pawn instanceof WhitePawn) return PositionUtil.posToSquare(pawn.getX(), pawn.getY() + 2);
        else return PositionUtil.posToSquare(pawn.getX(), pawn.getY() - 2);
    }

    public static String oneSquaresForward(Pawn pawn) {
        if (pawn instanceof WhitePawn) return PositionUtil.posToSquare(pawn.getX(), pawn.getY() + 1);
        else return PositionUtil.posToSquare(pawn.getX(), pawn.getY() - 1);
    }

    public Set<Piece> getOutOfTheBoardPieces() {
        return outOfTheBoardPieces;
    }

    public boolean emptyPath(Piece piece, String squareToGo) {
        return piece.squaresOnThePath(squareToGo).stream()
                .map(this::at)
                .map(Square::getPiece)
                .allMatch(Optional::isEmpty);
    }

    public boolean tryToMove(String from, String to) {
        return at(from)
                .getPiece()
                .map(p -> tryToMove(p, to))
                .orElse(false);
    }

    public boolean tryToMove(Piece piece, String positionToMoveOn) {
        if (piece.getPosition().equals(positionToMoveOn)) throw new IllegalArgumentException();
        if (canMove(piece, positionToMoveOn)) {
            // FIXME if rock cannot castle, it will still castle
            tryToCastle(piece, positionToMoveOn);
            Optional.of(piece)
                    .filter(Castlable.class::isInstance)
                    .map(Castlable.class::cast)
                    .ifPresent(Castlable::cannotCastleAnymore);
            at(positionToMoveOn).getPiece().ifPresent(this::removePieceFromTheBoard);
            move(piece, positionToMoveOn);

            return true;
        }
        log.debug("fail to move {} from {} to {}", piece, piece.getPosition(), positionToMoveOn);
        return false;
    }

    private void tryToCastle(Piece piece, String positionToMoveOn) {
        boolean kingCanCastle = piece instanceof King && ((King) piece).canCastle();
        boolean kingWantToCastle = piece.squaresOnThePath(positionToMoveOn).size() == 1;
        if (kingCanCastle && kingWantToCastle) {
            String finalPositionOfRock = piece.squaresOnThePath(positionToMoveOn).stream().findFirst().get();
            String rockPosition = CASTLE_MAP.get(finalPositionOfRock);
            at(rockPosition)
                    .getPiece()
                    .filter(Rock.class::isInstance)
                    .map(Rock.class::cast)
                    .filter(Rock::canCastle)
                    .ifPresentOrElse(rock -> {
                        log.info("{} is castling", piece);
                        move(rock, finalPositionOfRock);
                    }, () -> log.debug("{} can castle but rock cannot", piece));
        }
    }

    public void move(Piece piece, String positionToMoveOn) {
        log.info(piece + "{} moves from {} to {}", piece, piece.getPosition(), positionToMoveOn);
        at(piece.getPosition()).removePiece();
        piece.setSquare(at(positionToMoveOn));
        at(positionToMoveOn).setPiece(piece);
    }

    public void removePieceFromTheBoard(Piece piece) {
        if (piece instanceof King)
            throw new IllegalStateException(String.format("You should not be able to take %s", piece));
        piece.setSquare(null);
        outOfTheBoardPieces.add(piece);
        log.info("{} has been taken", piece);
    }

    public boolean canMove(Piece piece, String positionToMoveOn) {
        return piece.reachableSquares(positionToMoveOn, at(positionToMoveOn).getPiece().map(Piece::getColor))
                && emptyPath(piece, positionToMoveOn)
                && !doesMovingPieceCheckOurOwnKing(piece, positionToMoveOn)
                && isEnemyOrEmpty(piece, positionToMoveOn)
                && isValidCastle(piece, positionToMoveOn);
    }

    boolean isValidCastle(Piece piece, String positionToMoveOn) {
        return true; // TODO
    }

    boolean isEnemyOrEmpty(Piece piece, String positionToMoveOn) {
        Optional<Piece> p = at(positionToMoveOn).getPiece();
        return p.isEmpty() || p
                .map(Piece::getColor)
                .map(color -> color != piece.getColor())
                .orElse(false); // color is null
    }

    public void fakeSquare(String position, Piece piece) {
        log.debug("fake that {} is on {}", piece == null ? "nothing" : piece, position);
        Square fakeSquare = new Square(position);
        Optional.ofNullable(piece).ifPresent(piece1 -> fakeSquare.setPiece(new FakePiece(piece, fakeSquare)));
        fakeSquares.put(position, fakeSquare);
    }

    public void unfakeSquare(String position) {
        Square square = fakeSquares.get(position);
        log.debug("unfake that {} is on {}", square == null ? "nothing" : square.getPosition(), position);
        fakeSquares.remove(position);
    }

    public void unfakeAllSquares() {
        log.debug("unfake {} fake squares}", fakeSquares.size());
        fakeSquares.clear();
    }

    public int getNumberOfFakeSquares() {
        return fakeSquares.size();
    }

    boolean doesMovingPieceCheckOurOwnKing(Piece piece, String positionToMoveOn) {
        if (piece.getPosition().equals(positionToMoveOn)) throw new IllegalArgumentException();

        fakeSquare(piece.getPosition(), null);
        fakeSquare(positionToMoveOn, piece);
        assert fakeSquares.size() == 2;

        boolean enemyCanCheck = isKingUnderAttack(piece.getColor());

        piece.setSquare(at(piece.getPosition()));
        unfakeSquare(piece.getPosition());
        unfakeSquare(positionToMoveOn);
        assert fakeSquares.isEmpty();
        return enemyCanCheck;
    }

    public boolean isKingUnderAttack(Color color) {
        Optional<Piece> optionalKing = allyPieces(color).stream()
                .filter(p -> Character.toUpperCase(p.getType()) == 'K')
                .findFirst();
        return optionalKing
                .map(this::isKingUnderAttack)
                .orElse(false);
    }

    private boolean isKingUnderAttack(Piece king) {
        return enemyPieces(king.getColor()).stream()
                .filter(piece -> !fakeSquares.containsKey(piece.getPosition()))
                .anyMatch(enemyPiece -> enemyPiece.reachableSquares(king.getPosition(), Optional.of(king.getColor())) && emptyPath(enemyPiece, king.getPosition()));
    }

    public Set<Piece> allyPieces(Color allyColor) {
        Map<String, Square> allyPieces = new HashMap<>();
        allyPieces.putAll(board);
        allyPieces.putAll(fakeSquares);
        return allyPieces.values().stream()
                .map(Square::getPiece)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(p -> p.getColor() == allyColor)
                .collect(Collectors.toSet());
    }

    public Set<Piece> enemyPieces(Color allyColor) {
        Map<String, Square> enemyPieces = new HashMap<>();
        enemyPieces.putAll(board);
        enemyPieces.putAll(fakeSquares);
        return enemyPieces.values().stream()
                .map(Square::getPiece)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(p -> p.getColor() != allyColor)
                .collect(Collectors.toSet());
    }
}
