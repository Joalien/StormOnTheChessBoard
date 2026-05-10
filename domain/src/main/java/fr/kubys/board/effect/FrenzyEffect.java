package fr.kubys.board.effect;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.CardType;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class FrenzyEffect extends Effect {

    private final ChessBoard chessBoard;
    private boolean duringMove = false;
    /**
     * Re-entry guard for {@link #noCaptureAvailable()}. The capture probe calls
     * {@link ChessBoard#canAttack}, which itself consults {@link #blocksPosition} —
     * which would call back into the probe, causing infinite recursion. The flag
     * suppresses our own block during a re-entrant probe so that other effects
     * (Hideout, NonViolent, Ceasefire…) are still consulted and a piece they
     * disable is correctly excluded from the "can capture?" answer.
     */
    private boolean evaluatingCaptures = false;

    public FrenzyEffect(ChessBoard chessBoard) {
        super("Fringale");
        this.chessBoard = chessBoard;
    }

    @Override
    public List<Position> getPositions() {
        return Collections.emptyList();
    }

    @Override
    public boolean blocksPosition(Position position) {
        if (duringMove) return false;
        if (evaluatingCaptures) return false;
        if (noCaptureAvailable()) return false;
        // Block positions that do NOT have an enemy non-king piece
        return chessBoard.at(position).getPiece()
                .filter(p -> !p.isKing())
                .filter(p -> p.getColor() != chessBoard.getCurrentTurn())
                .isEmpty();
    }

    @Override
    public boolean blocksPath(Position from, Position to, Set<Position> intermediates) {
        // Frenzy only blocks the destination, not intermediate path squares
        return false;
    }

    @Override
    public void beforeMoveHook(ChessBoard chessBoard, Piece piece) {
        if (noCaptureAvailable()) {
            chessBoard.removeEffect(this);
            return;
        }
        duringMove = true;
    }

    @Override
    public void afterMoveHook(ChessBoard chessBoard, Piece piece) {
        duringMove = false;
    }

    /**
     * REPLACE_TURN cards bypass the standard move pipeline (they call {@code board.move}
     * directly inside {@code Card.doAction}), so {@link #blocksPosition} never gets a
     * chance to reject a non-capture move embedded in a card. We catch that here, after
     * the card has been applied: if Frenzy is still active (i.e. a capture was actually
     * available) and no capture occurred during the card's execution, reject the play.
     * <p>
     * BEFORE_TURN and AFTER_TURN paths are unaffected: BEFORE_TURN is followed by a
     * normal move that goes through {@code blocksPosition}; AFTER_TURN is played after
     * the move that already had to capture (or Frenzy was already inactive).
     */
    @Override
    public void afterCardPlayHook(ChessBoard chessBoard, CardType cardType) {
        if (cardType != CardType.REPLACE_TURN) return;
        if (chessBoard.lastMoveWasCapture()) return;
        throw new IllegalStateException(
                "Fringale exige que vous preniez une pièce ce tour-ci. Cette carte n'en capture aucune.");
    }

    /**
     * True iff the current player has no ally piece that can really capture an enemy
     * non-king piece. Goes through {@link ChessBoard#canAttack}, so other effects such
     * as Hideout (sleeping pieces), NonViolent and Ceasefire correctly remove
     * unavailable attackers from consideration.
     */
    private boolean noCaptureAvailable() {
        evaluatingCaptures = true;
        try {
            Color currentTurn = chessBoard.getCurrentTurn();
            return chessBoard.allyPieces(currentTurn).stream()
                    .filter(piece -> piece.findPosition().isPresent())
                    .noneMatch(piece -> chessBoard.enemyPieces(currentTurn).stream()
                            .filter(enemy -> !enemy.isKing())
                            .filter(enemy -> enemy.findPosition().isPresent())
                            .anyMatch(enemy -> chessBoard.canAttack(piece, enemy.getPosition())));
        } finally {
            evaluatingCaptures = false;
        }
    }
}
