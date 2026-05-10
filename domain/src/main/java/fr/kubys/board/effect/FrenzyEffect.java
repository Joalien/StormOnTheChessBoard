package fr.kubys.board.effect;

import fr.kubys.board.ChessBoard;
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
