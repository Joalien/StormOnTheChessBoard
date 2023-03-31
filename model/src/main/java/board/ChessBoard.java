package board;

import effet.Effect;
import lombok.extern.slf4j.Slf4j;
import piece.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static position.PositionUtil.*;

@Slf4j
public class ChessBoard {

    private static final Map<String, String> CASTLE_MAP = Map.of("f1", "h1",
            "d1", "a1",
            "f8", "h8",
            "d8", "a8");
    private final HashMap<String, Square> board = new HashMap<>(64);
    private final HashMap<String, Square> fakeSquares = new HashMap<>();
    private final Set<Piece> outOfTheBoardPieces = new HashSet<>();
    private final Set<Effect> effects = new HashSet<>();

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
                .mapToObj(i -> posToSquare(i, MIN + 1))
                .forEach(position -> chessBoard.add(new WhitePawn(), position));
        IntStream.rangeClosed(MIN, MAX)
                .mapToObj(i -> posToSquare(i, MAX - 1))
                .forEach(position -> chessBoard.add(new BlackPawn(), position));
        return chessBoard;
    }

    public void add(Piece piece, String position) { // HERE
        if (!fakeSquares.isEmpty()) throw new IllegalStateException("You cannot update board if there are fake pieces on");
        if (at(position).getPiece().isPresent())
            throw new IllegalArgumentException(String.format("Cannot add %s because %s is not empty", piece, position));
        if (outOfTheBoardPieces.remove(piece)) log.info("{} go back to the life!", piece);
        piece.setSquare(at(position));
        at(position).setPiece(piece);

        effects.forEach(effect -> effect.afterMoveHook(this, piece));
    }

    public Square at(String position) {
        if (invalidPosition(position)) throw new IllegalArgumentException("square is invalid");
        if (fakeSquares.containsKey(position)) return fakeSquares.get(position);
        board.putIfAbsent(position, new Square(position)); // TODO inline return ?
        return board.get(position);
    }



    public Set<Piece> getOutOfTheBoardPieces() {
        return outOfTheBoardPieces;
    }

    public Set<String> getAllAttackablePosition(Piece piece) {
        return generateAllPositions().stream()
                .filter(pos -> !pos.equals(piece.getPosition()))
                .filter(pos -> canAttack(piece, pos))
                .collect(Collectors.toSet());
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
        at(positionToMoveOn).getPiece().ifPresent(this::removePieceFromTheBoard);
        log.info("{} moves from {} to {}", piece, piece.getPosition(), positionToMoveOn);
        at(piece.getPosition()).removePiece();
        add(piece, positionToMoveOn);
    }

    public Piece removePieceFromTheBoard(Piece piece) { // HERE
        if (piece instanceof King)
            throw new IllegalStateException(String.format("You should not be able to take %s", piece));
        at(piece.getPosition()).setPiece(null);
        piece.setSquare(null);
        outOfTheBoardPieces.add(piece);
        log.info("{} has been taken and removed out of the board", piece);
        return piece;
    }

    public boolean canMove(Piece piece, String positionToMoveOn) {
        return canAttack(piece, positionToMoveOn)
                && !doesMovingPieceCheckOurOwnKing(piece, positionToMoveOn)
                && isValidCastle(piece, positionToMoveOn);
    }

    public boolean canAttack(Piece piece, String positionToMoveOn) {
        return piece.isPositionTheoreticallyReachable(positionToMoveOn, at(positionToMoveOn).getPiece().map(Piece::getColor))
                && emptyPath(piece, positionToMoveOn)
                && isEnemyOrEmpty(piece, positionToMoveOn);
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

    public void fakeSquare(Piece piece, String position) {
        if (fakeSquares.containsKey(position) && fakeSquares.get(position) == null) throw new IllegalArgumentException("You cannot re-fake over a fake piece");
        log.debug("fake that {} is on {}", Optional.ofNullable(piece).map(Objects::toString).orElse("nothing"), position);
        Square fakeSquare = new Square(position);
        Optional.ofNullable(piece).ifPresent(p -> fakeSquare.setPiece(new FakePieceDecorator(p, fakeSquare)));
        fakeSquares.put(position, fakeSquare);
    }

    public void unfakeSquare(String position) {
        Square square = fakeSquares.get(position);
        log.debug("unfake that {} is on {}", square.getPiece().map(Piece::toString).orElse("nothing"), position);
        fakeSquares.remove(position);
    }

    public void unfakeAllSquares() {
        log.debug("unfake {} fake squares", fakeSquares.size());
        fakeSquares.clear();
    }

    public int getNumberOfFakeSquares() {
        return fakeSquares.size();
    }

    boolean doesMovingPieceCheckOurOwnKing(Piece piece, String positionToMoveOn) {
        if (piece.getPosition().equals(positionToMoveOn)) throw new IllegalArgumentException();
        if (piece instanceof FakePieceDecorator) return false;

        fakeSquare(null, piece.getPosition());
        fakeSquare(piece, positionToMoveOn);
//        assert fakeSquares.size() == 2;

        boolean enemyCanCheck = isKingUnderAttack(piece.getColor());

        piece.setSquare(at(piece.getPosition()));
        unfakeSquare(piece.getPosition());
        unfakeSquare(positionToMoveOn);
//        assert fakeSquares.isEmpty();
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
//                .filter(piece -> !fakeSquares.containsKey(piece.getPosition())) // Why ?
                .anyMatch(enemyPiece -> canAttack(enemyPiece, king.getPosition()));
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

    public void removeEffect(Effect effect) {
        this.effects.remove(effect);
    }

    public void addEffect(Effect effect) {
        this.effects.add(effect);
    }
}
