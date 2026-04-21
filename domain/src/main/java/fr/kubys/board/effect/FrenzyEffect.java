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
        if (!anyCaptureAvailable()) return false;
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
        if (!anyCaptureAvailable()) {
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
     * Check if the current player can capture any enemy non-king piece.
     * Uses raw piece movement rules (isPositionTheoreticallyReachable + path check)
     * to avoid recursion through effects/blocksPosition.
     */
    private boolean anyCaptureAvailable() {
        Color currentTurn = chessBoard.getCurrentTurn();
        return chessBoard.allyPieces(currentTurn).stream()
                .filter(p -> p.findPosition().isPresent())
                .anyMatch(piece -> chessBoard.enemyPieces(currentTurn).stream()
                        .filter(enemy -> !enemy.isKing())
                        .filter(enemy -> enemy.findPosition().isPresent())
                        .anyMatch(enemy -> canRawAttack(piece, enemy.getPosition())));
    }

    /**
     * Check if a piece can reach a position using only raw movement rules,
     * bypassing all effects to avoid infinite recursion.
     */
    private boolean canRawAttack(Piece piece, Position target) {
        Color targetColor = chessBoard.at(target).getPiece().map(Piece::getColor).orElse(null);
        if (!piece.isPositionTheoreticallyReachable(target.getFile(), target.getRow(), targetColor)) return false;
        return piece.squaresOnThePath(target).stream()
                .allMatch(pos -> chessBoard.at(pos).getPiece().isEmpty());
    }
}
