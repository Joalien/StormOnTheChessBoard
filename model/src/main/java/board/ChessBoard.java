package board;

import effet.Effect;
import lombok.extern.slf4j.Slf4j;
import piece.*;
import position.File;
import position.Position;
import position.Row;

import java.util.*;
import java.util.stream.Collectors;

import static position.Position.*;

@Slf4j
public class ChessBoard {

    private final HashMap<Position, Square> board = new HashMap<>(64);
    private final HashMap<Position, Square> fakeSquares = new HashMap<>();
    private final Set<Piece> outOfTheBoardPieces = new HashSet<>();
    private final Set<Effect> effects = new HashSet<>();
    // FIXME retirer les dépendances techniques
    // FIXME ArchUnit : tester l'architecture

    public static ChessBoard createEmpty() {
        log.debug("create empty chessboard");
        return new ChessBoard();
    }

    public static ChessBoard createWithInitialState() {
        log.debug("create chessboard with initial state");
        ChessBoard chessBoard = new ChessBoard();

        chessBoard.add(new King(Color.WHITE), e1);
        chessBoard.add(new King(Color.BLACK), e8);
        chessBoard.add(new Queen(Color.WHITE), d1);
        chessBoard.add(new Queen(Color.BLACK), d8);
        chessBoard.add(new Bishop(Color.WHITE), c1);
        chessBoard.add(new Bishop(Color.WHITE), f1);
        chessBoard.add(new Bishop(Color.BLACK), f8);
        chessBoard.add(new Bishop(Color.BLACK), c8);
        chessBoard.add(new Knight(Color.WHITE), b1);
        chessBoard.add(new Knight(Color.WHITE), g1);
        chessBoard.add(new Knight(Color.BLACK), b8);
        chessBoard.add(new Knight(Color.BLACK), g8);
        chessBoard.add(new Rock(Color.WHITE), a1);
        chessBoard.add(new Rock(Color.WHITE), h1);
        chessBoard.add(new Rock(Color.BLACK), a8);
        chessBoard.add(new Rock(Color.BLACK), h8);
        Arrays.stream(File.values())
                .map(file -> Position.posToSquare(file, Row.Two))
                .forEach(position -> chessBoard.add(new WhitePawn(), position));
        Arrays.stream(File.values())
                .map(file -> Position.posToSquare(file, Row.Seven))
                .forEach(position -> chessBoard.add(new BlackPawn(), position));
        return chessBoard;
    }

    public void add(Piece piece, Position position) { // HERE
        if (!fakeSquares.isEmpty())
            throw new IllegalStateException("You cannot update board if there are fake pieces on");
        if (at(position).getPiece().isPresent())
            throw new IllegalArgumentException("Cannot add %s because %s is not empty".formatted(piece, position));
        if (outOfTheBoardPieces.remove(piece)) log.info("{} go back to the life!", piece);
        piece.setSquare(at(position));
        at(position).setPiece(piece);

        effects.forEach(effect -> effect.afterMoveHook(this, piece));
    }

    public Square at(Position position) {
        if (fakeSquares.containsKey(position)) return fakeSquares.get(position);
        board.putIfAbsent(position, new Square(position)); // TODO inline return ?
        return board.get(position);
    }


    public Set<Piece> getOutOfTheBoardPieces() {
        return outOfTheBoardPieces;
    }

    public Set<Position> getAllAttackablePosition(Piece piece) {
        return generateAllPositions().stream()
                .filter(pos -> !pos.equals(piece.getPosition()))
                .filter(pos -> canAttack(piece, pos))
                .collect(Collectors.toSet());
    }

    public boolean canAttack(Piece piece, Position positionToMoveOn) {
        return (isPositionTheoreticallyReachable(piece, positionToMoveOn) || doesEffectAllowToMove(piece, positionToMoveOn))
                && emptyPath(piece, positionToMoveOn)
                && isEnemyOrEmpty(piece, positionToMoveOn);
    }

    private boolean isPositionTheoreticallyReachable(Piece piece, Position positionToMoveOn) {
        return piece.isPositionTheoreticallyReachable(positionToMoveOn, at(positionToMoveOn).getPiece().map(Piece::getColor).orElse(null));
    }

    private boolean doesEffectAllowToMove(Piece piece, Position positionToMoveOn) {
        return effects.stream()
                .anyMatch(effect -> effect.allowToMove(piece, positionToMoveOn));
    }

    public boolean emptyPath(Piece piece, Position squareToGo) {
        return piece.squaresOnThePath(squareToGo).stream()
                .map(this::at)
                .map(Square::getPiece)
                .allMatch(Optional::isEmpty);
    }

    boolean isEnemyOrEmpty(Piece piece, Position positionToMoveOn) {
        Optional<Piece> p = at(positionToMoveOn).getPiece();
        return p.isEmpty() || p
                .map(Piece::getColor)
                .map(color -> color != piece.getColor())
                .orElse(false); // color is null
    }

    public boolean tryToMove(Position from, Position to) {
        return at(from)
                .getPiece()
                .map(p -> tryToMove(p, to))
                .orElseThrow(() -> new IllegalArgumentException("%s is empty!".formatted(from)));
    }

    public boolean tryToMove(Piece piece, Position positionToMoveOn) {
        if (piece.getPosition().equals(positionToMoveOn)) throw new IllegalArgumentException();
        if (canMove(piece, positionToMoveOn)) {
            // FIXME if rock cannot castle, it will still castle
            tryToCastle(piece, positionToMoveOn);
            Optional.of(piece)
                    .filter(Castlable.class::isInstance)
                    .map(Castlable.class::cast)
                    .ifPresent(Castlable::cannotCastleAnymore);
            move(piece, positionToMoveOn);

            return true;
        }
        log.debug("fail to move {} from {} to {}", piece, piece.getPosition(), positionToMoveOn);
        return false;
    }

    void tryToCastle(Piece piece, Position positionToMoveOn) {
        boolean kingCanCastle = piece instanceof King && ((King) piece).canCastle();
        boolean kingWantToCastle = piece.squaresOnThePath(positionToMoveOn).size() == 1;
        if (kingCanCastle && kingWantToCastle) {
            Position finalPositionOfRock = piece.squaresOnThePath(positionToMoveOn).stream().findFirst().get();
            Position rockPosition = Castlable.CASTLE_MAP.get(finalPositionOfRock);
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

    public void move(Piece piece, Position positionToMoveOn) {
        effects.forEach(effect -> effect.beforeMoveHook(this, piece));

        at(positionToMoveOn).getPiece().ifPresent(this::removePieceFromTheBoard);
        log.info("{} moves from {} to {}", piece, piece.getPosition(), positionToMoveOn);
        at(piece.getPosition()).removePiece();
        add(piece, positionToMoveOn);
    }

    public Piece removePieceFromTheBoard(Piece piece) {
        if (piece instanceof King)
            throw new IllegalStateException("You should not be able to take %s".formatted(piece));
        at(piece.getPosition()).setPiece(null);
        piece.setSquare(null);
        outOfTheBoardPieces.add(piece);
        log.info("{} has been taken and removed out of the board", piece);

        effects.forEach(effect -> effect.afterRemovingPieceHook(this, piece));
        return piece;
    }

    public boolean canMove(Piece piece, Position positionToMoveOn) {
        return canAttack(piece, positionToMoveOn)
                && !doesMovingPieceCheckOurOwnKing(piece, positionToMoveOn)
                && isValidCastle(piece, positionToMoveOn);
    }

    boolean isValidCastle(Piece piece, Position positionToMoveOn) {
        return true; // TODO
    }

    public void fakeSquare(Piece piece, Position position) {
        if (fakeSquares.containsKey(position) && fakeSquares.get(position) == null)
            throw new IllegalArgumentException("You cannot re-fake over a fake piece");
        log.debug("fake that {} is on {}", Optional.ofNullable(piece).map(Objects::toString).orElse("nothing"), position);
        Square fakeSquare = new Square(position);
        Optional.ofNullable(piece).ifPresent(p -> fakeSquare.setPiece(new FakePieceDecorator(p, fakeSquare)));
        fakeSquares.put(position, fakeSquare);
    }

    public void unfakeSquare(Position position) {
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

    boolean doesMovingPieceCheckOurOwnKing(Piece piece, Position positionToMoveOn) {
        if (piece.getPosition().equals(positionToMoveOn)) throw new IllegalArgumentException();
        if (piece instanceof FakePieceDecorator) return false;

        fakeSquare(null, piece.getPosition());
        fakeSquare(piece, positionToMoveOn);
        assert fakeSquares.size() == 2; // is that a smell ?

        boolean enemyCanCheck = isKingUnderAttack(piece.getColor());

        piece.setSquare(at(piece.getPosition()));
        unfakeSquare(piece.getPosition());
        unfakeSquare(positionToMoveOn);
        assert fakeSquares.isEmpty();
        return enemyCanCheck;
    }

    public boolean isKingUnderAttack(Color color) {
        Optional<Piece> optionalKing = allyPieces(color).stream()
                .filter(Piece::isKing)
                .findFirst();
        return optionalKing
                .map(this::isKingUnderAttack)
                .orElse(false);
    }

    private boolean isKingUnderAttack(Piece king) {
        return enemyPieces(king.getColor()).stream()
                .anyMatch(enemyPiece -> canAttack(enemyPiece, king.getPosition()));
    }

    public Set<Piece> allyPieces(Color allyColor) {
        Map<Position, Square> allyPieces = new HashMap<>();
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
        Map<Position, Square> enemyPieces = new HashMap<>();
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
        log.info("{} is no longer effective", effect.getName());
        this.effects.remove(effect);
    }

    public void addEffect(Effect effect) {
        log.info("{} is now effective", effect.getName());
        this.effects.add(effect);
    }

    public Set<Effect> getEffects() {
        return Set.copyOf(this.effects);
    }
}
