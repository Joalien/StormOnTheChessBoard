package fr.kubys.board;

import fr.kubys.board.effect.Effect;
import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.*;
import fr.kubys.piece.PromotionPiece;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.kubys.core.Position.*;

public class ChessBoard {
    private static final Logger log = LoggerFactory.getLogger(ChessBoard.class);
//    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BombingEffect.class);

    private final Map<Position, Square> board = new HashMap<>(64);
    private final Map<Position, Square> fakeSquares = new HashMap<>();
    private final Set<PieceRemoval> outOfTheBoardPieces = new HashSet<>();
    private final Set<Effect> effects = new HashSet<>();
    private final List<Position> promotedPositions = new ArrayList<>();

    private Color currentTurn = Color.WHITE;
    private int turnNumber = 0;

    public static ChessBoard createEmpty() {
//        log.debug("create empty chessboard");
        return new ChessBoard();
    }

    private ChessBoard() {}

    public static ChessBoard createWithInitialState() {
//        log.debug("create chessboard with initial state");
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
                .map(file -> posToSquare(file, Row.Two))
                .forEach(position -> chessBoard.add(new WhitePawn(), position));
        Arrays.stream(File.values())
                .map(file -> posToSquare(file, Row.Seven))
                .forEach(position -> chessBoard.add(new BlackPawn(), position));
        return chessBoard;
    }

    public void add(Piece piece, Position position) {
        if (!fakeSquares.isEmpty())
            // Should never happen
            throw new IllegalStateException("You cannot update board if there are fake pieces on");
        if (doesEffectBlockSquare(position))
            throw new IllegalArgumentException("Cannot add %s because %s is blocked".formatted(piece, position));
        if (at(position).getPiece().isPresent())
            throw new IllegalArgumentException("Cannot add %s because %s is not empty".formatted(piece, position));
        outOfTheBoardPieces.removeIf(r -> r.piece() == piece);
        piece.setPosition(position);
        at(position).setPiece(piece);

        new ArrayList<>(effects).forEach(effect -> effect.afterMoveHook(this, piece));

        if (piece instanceof Pawn pawn && pawn.isOnPromotionRow()) {
            promote(pawn);
        }
    }

    public Square at(Position position) {
        if (fakeSquares.containsKey(position)) return fakeSquares.get(position);
        board.putIfAbsent(position, new Square(position)); // TODO inline return ?
        return board.get(position);
    }


    public Set<Piece> getOutOfTheBoardPieces() {
        return outOfTheBoardPieces.stream().map(PieceRemoval::piece).collect(Collectors.toSet());
    }

    public Set<PieceRemoval> getPieceRemovals() {
        return Set.copyOf(outOfTheBoardPieces);
    }

    public Set<Position> getAllAttackablePosition(Piece piece) {
        return generateAllPositions().stream()
                .filter(pos -> !pos.equals(piece.getPosition()))
                .filter(pos -> canAttack(piece, pos))
                .collect(Collectors.toSet());
    }

    public boolean canAttack(Piece piece, Position positionToMoveOn) {
        return canAttack(piece, positionToMoveOn, piece.getColor());
    }

    private boolean canAttack(Piece piece, Position positionToMoveOn, Color effectiveColor) {
        Color targetPieceColor = at(positionToMoveOn).getPiece().map(Piece::getColor).orElse(null);
        boolean reachable = piece.isPositionTheoreticallyReachable(positionToMoveOn.getFile(), positionToMoveOn.getRow(), targetPieceColor, effectiveColor);
        return ((reachable && emptyPath(piece, positionToMoveOn)) || doesEffectAllowToMove(piece, positionToMoveOn))
                && isEnemyOrEmpty(piece, positionToMoveOn, effectiveColor);
    }

    private boolean doesEffectAllowToMove(Piece piece, Position positionToMoveOn) {
        return effects.stream()
                .anyMatch(effect -> effect.allowToMove(piece, positionToMoveOn));
    }

    private boolean doesEffectBlockSquare(Position position) {
        return effects.stream()
                .anyMatch(effect -> effect.blocksPosition(position));
    }

    public boolean emptyPath(Piece piece, Position squareToGo) {
        Set<Position> intermediates = piece.squaresOnThePath(squareToGo);
        if (effects.stream().anyMatch(e -> e.blocksPath(piece.getPosition(), squareToGo, intermediates))) return false;
        Stream<Optional<Piece>> piecesOnPath = intermediates.stream()
                .map(this::at)
                .map(Square::getPiece);
        return piece.hasEmptyPath().test(piecesOnPath);
    }

    boolean isEnemyOrEmpty(Piece piece, Position positionToMoveOn) {
        return isEnemyOrEmpty(piece, positionToMoveOn, piece.getColor());
    }

    boolean isEnemyOrEmpty(Piece piece, Position positionToMoveOn, Color effectiveColor) {
        if (doesEffectBlockSquare(positionToMoveOn)) return false;
        Optional<Piece> p = at(positionToMoveOn).getPiece();
        return p.isEmpty() || p
                .map(Piece::getColor)
                .map(color -> color != effectiveColor)
                .orElse(false); // color is null
    }

    public void tryToMove(Position from, Position to) {
        at(from)
                .getPiece()
                .ifPresentOrElse(p -> tryToMove(p, to), () -> {
                    throw new IllegalArgumentException("There is no piece on %s".formatted(from));
                });
    }

    public void tryToMove(Piece piece, Position positionToMoveOn) {
        if (piece.getPosition().equals(positionToMoveOn)) throw new IllegalArgumentException();
        if (canMove(piece, positionToMoveOn)) {
            // FIXME if rock cannot castle, it will still castle
            tryToCastle(piece, positionToMoveOn);
            move(piece, positionToMoveOn);
        }
    }

    Optional<Position> getCastleRookPassSquare(Piece piece, Position positionToMoveOn) {
        if (!piece.isKing() || !piece.canCastle()) return Optional.empty();
        Row homeRow = piece.getColor().homeRow();
        Position startingPos = posToSquare(File.E, homeRow);
        if (!piece.getPosition().equals(startingPos)) return Optional.empty();

        Position kingSideTarget = posToSquare(File.G, homeRow);
        Position queenSideTarget = posToSquare(File.C, homeRow);

        if (positionToMoveOn.equals(kingSideTarget)) return Optional.of(posToSquare(File.F, homeRow));
        if (positionToMoveOn.equals(queenSideTarget)) return Optional.of(posToSquare(File.D, homeRow));
        return Optional.empty();
    }

    void tryToCastle(Piece piece, Position positionToMoveOn) {
        getCastleRookPassSquare(piece, positionToMoveOn).ifPresent(rookPassSquare -> {
            Position rockPosition = Castlable.CASTLE_MAP.get(rookPassSquare);
            at(rockPosition)
                    .getPiece()
                    .filter(Rock.class::isInstance)
                    .map(Rock.class::cast)
                    .filter(Piece::canCastle)
                    .ifPresentOrElse(rock -> {
                        move(rock, rookPassSquare);
                        rock.cannotCastleAnymore();
                        piece.cannotCastleAnymore();
                    }, () -> {
//                        log.debug("{} can castle but rock cannot", piece)
                    });
        });
    }

    public void move(Piece piece, Position positionToMoveOn) {
        new ArrayList<>(effects).forEach(effect -> effect.beforeMoveHook(this, piece));

        at(positionToMoveOn).getPiece().ifPresent(captured -> removePieceFromTheBoard(captured, PieceRemoval.RemovalReason.CAPTURED));
//        log.info("{} moves from {} to {}", piece, piece.getPosition(), positionToMoveOn);
        at(piece.getPosition()).removePiece();
        add(piece, positionToMoveOn);
    }

    private void promote(Pawn pawn) {
        Position position = pawn.getPosition();
        removePieceFromTheBoard(pawn);
        add(new Queen(pawn.getColor()), position);
        promotedPositions.add(position);
    }

    public List<Position> drainPromotedPositions() {
        List<Position> result = new ArrayList<>(promotedPositions);
        promotedPositions.clear();
        return result;
    }

    public void overridePromotion(Position position, PromotionPiece promotionPiece) {
        Piece current = at(position).getPiece().orElseThrow();
        Color color = current.getColor();
        removePieceFromTheBoard(current);
        add(promotionPiece.toPiece(color), position);
    }

    public Piece removePieceFromTheBoard(Piece piece) {
        return removePieceFromTheBoard(piece, PieceRemoval.RemovalReason.EFFECT);
    }

    public Piece removePieceFromTheBoard(Piece piece, PieceRemoval.RemovalReason reason) {
        if (piece.isKing())
            log.warn("{} is removed from the board, are you sure? Or should you throw CannotTakeKingException?", piece);
        at(piece.getPosition()).setPiece(null);
        piece.setPosition(null);
        outOfTheBoardPieces.add(new PieceRemoval(piece, turnNumber, reason));

        new ArrayList<>(effects).forEach(effect -> effect.afterRemovingPieceHook(this, piece));
        return piece;
    }

    // Throw exception instead of false to send a message to explain why it is not possible to move
    public boolean canMove(Piece piece, Position positionToMoveOn) {
        boolean isCastleMove = getCastleRookPassSquare(piece, positionToMoveOn).isPresent();
        if (!isCastleMove && !canAttack(piece, positionToMoveOn, piece.getEffectiveColor(currentTurn)))
            throw new IllegalMoveException("You cannot move %s to %s".formatted(piece, positionToMoveOn));
        if (doesMovingPieceCheckOurOwnKing(piece, positionToMoveOn))
            throw new CheckException();
        return true;
    }

    public void fakeSquare(Piece piece, Position position) {
        if (fakeSquares.containsKey(position) && fakeSquares.get(position) == null)
            throw new IllegalArgumentException("You cannot re-fake over a fake piece");
//        log.debug("fake that {} is on {}", Optional.ofNullable(piece).map(Objects::toString).orElse("nothing"), position);
        Square fakeSquare = new Square(position);
        Optional.ofNullable(piece).ifPresent(p -> fakeSquare.setPiece(new FakePieceDecorator(p, position)));
        fakeSquares.put(position, fakeSquare);
    }

    public void unfakeSquare(Position position) {
        Square square = fakeSquares.get(position);
//        log.debug("unfake that {} is on {}", square.getPiece().map(Piece::toString).orElse("nothing"), position);
        fakeSquares.remove(position);
    }

    public void unfakeAllSquares() {
//        log.debug("unfake {} fake squares", fakeSquares.size());
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

        // Check if the king of the player performing the move is under attack
        boolean enemyCanCheck = isKingUnderAttack(piece.getEffectiveColor(currentTurn));

        piece.setPosition(piece.getPosition());
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

    public Set<Piece> getPieces() {
        Map<Position, Square> pieces = new HashMap<>();
        pieces.putAll(board);
        pieces.putAll(fakeSquares);
        return pieces.values().stream()
                .map(Square::getPiece)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    public Set<Piece> allyPieces(Color allyColor) {
        return getPieces().stream()
                .filter(p -> p.getColor() == allyColor)
                .collect(Collectors.toSet());
    }

    public Set<Piece> enemyPieces(Color allyColor) {
        return getPieces().stream()
                .filter(p -> p.getColor() != allyColor)
                .collect(Collectors.toSet());
    }

    public void removeEffect(Effect effect) {
//        log.info("{} is no longer effective", effect.getName());
        this.effects.remove(effect);
    }

    public void addEffect(Effect effect) {
//        log.info("{} is now effective", effect.getName());
        this.effects.add(effect);
    }

    public Set<Effect> getEffects() {
        return Set.copyOf(this.effects);
    }

    public Color getCurrentTurn() {
        return currentTurn;
    }

    public void setTurn(Color currentPlayer) {
        this.currentTurn = currentPlayer;
        this.turnNumber++;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

}
