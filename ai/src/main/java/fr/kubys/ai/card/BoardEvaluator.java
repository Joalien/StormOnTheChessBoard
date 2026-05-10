package fr.kubys.ai.card;

import fr.kubys.ai.MaterialStrategy;
import fr.kubys.api.ChessBoardReadService;
import fr.kubys.api.GameResult;
import fr.kubys.core.Color;
import fr.kubys.piece.Piece;

public final class BoardEvaluator {

    public static final int CHECKMATE_SCORE = 10_000;
    public static final int CHECK_BONUS = 20;
    private static final int MATERIAL_MULTIPLIER = 10;

    private BoardEvaluator() {}

    /**
     * Evaluates the position from the perspective of the supplied color. Material values are
     * scaled by {@value #MATERIAL_MULTIPLIER} to align with move scoring in {@link
     * MaterialStrategy}, so card-play scores and move scores are directly comparable. Wins
     * dominate, opponent-in-check is a small bonus, neutral pieces are ignored.
     */
    public static int evaluate(ChessBoardReadService board, Color perspective) {
        GameResult result = board.getGameResult();
        if (result == GameResult.WHITE_WINS) return perspective == Color.WHITE ? CHECKMATE_SCORE : -CHECKMATE_SCORE;
        if (result == GameResult.BLACK_WINS) return perspective == Color.BLACK ? CHECKMATE_SCORE : -CHECKMATE_SCORE;

        int score = 0;
        for (Piece piece : board.getPieces()) {
            int value = MaterialStrategy.pieceValue(piece) * MATERIAL_MULTIPLIER;
            if (piece.getColor() == perspective) score += value;
            else if (piece.getColor() == perspective.opposite()) score -= value;
        }

        Color currentTurn = board.getCurrentPlayer().getColor();
        if (board.isCurrentPlayerInCheck() && currentTurn != perspective) score += CHECK_BONUS;
        if (board.isCurrentPlayerInCheck() && currentTurn == perspective) score -= CHECK_BONUS;

        return score;
    }
}
