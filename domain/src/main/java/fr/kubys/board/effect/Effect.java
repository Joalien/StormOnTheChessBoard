package fr.kubys.board.effect;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.CardType;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class Effect {

    private final String name;
    private final List<Position> positions = new ArrayList<>();

    public Effect(String name) {
        this.name = name;
    }

    public void afterMoveHook(ChessBoard chessBoard, Piece piece) {
    }

    public void beforeMoveHook(ChessBoard chessBoard, Piece piece) {
    }

    public void afterRemovingPieceHook(ChessBoard chessBoard, Piece piece) {
    }

    /**
     * Called after a card has been successfully applied to the board, in any of the
     * card-play paths (BEFORE_TURN, REPLACE_TURN, AFTER_TURN, ENEMY_TURN). Effects can
     * throw here to reject card plays that violate their invariants — e.g. Fringale
     * rejects a REPLACE_TURN that did not capture anything when a capture was required.
     */
    public void afterCardPlayHook(ChessBoard chessBoard, CardType cardType) {
    }

    public boolean allowToMove(Piece piece, Position positionToMoveOn) {
        return false;
    }

    public boolean blocksPosition(Position position) {
        return false;
    }

    public boolean blocksCapture(Piece attacker, Position target) {
        return false;
    }

    public boolean blocksAttack(Piece attacker, Position target) {
        return false;
    }

    public boolean blocksCardPlaying() {
        return false;
    }

    public boolean blocksPath(Position from, Position to, Set<Position> intermediates) {
        return intermediates.stream().anyMatch(this::blocksPosition);
    }

    public String getName() {
        return this.name;
    }

    public List<Position> getPositions() {
        return positions;
    }
}
