package fr.kubys.ai.card;

import fr.kubys.ai.MaterialStrategy;
import fr.kubys.api.ChessBoardReadService;
import fr.kubys.api.GameResult;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;

import java.util.Set;

public final class BoardEvaluator {

    public static final int CHECKMATE_SCORE = 10_000;
    public static final int CHECK_BONUS = 20;
    private static final int MATERIAL_MULTIPLIER = 10;
    private static final int MOBILITY_PER_MOVE = 1;
    private static final int PAWN_ADVANCE_PER_RANK = 2;
    private static final int DEVELOPED_PIECE_BONUS = 5;
    private static final int CENTER_OCCUPATION_BONUS = 3;

    private static final Set<Position> CENTER_SQUARES = Set.of(Position.d4, Position.d5, Position.e4, Position.e5);

    private BoardEvaluator() {}

    /**
     * Evaluates the position from the perspective of the supplied color. Material is scaled by
     * {@value #MATERIAL_MULTIPLIER} so a pawn weighs 10. Positional bonuses (mobility, pawn
     * advancement, piece development, center control) sum to a few units in a typical opening
     * — small enough that any real material trade dominates, but big enough that cards which
     * push pawns or open lines (Charge, Cylindre, Skating, etc.) come out positive even when
     * the material count is unchanged. Wins always dominate, opponent-in-check is a small
     * bonus, neutral pieces are ignored.
     */
    public static int evaluate(ChessBoardReadService board, Color perspective) {
        GameResult result = board.getGameResult();
        if (result == GameResult.WHITE_WINS) return perspective == Color.WHITE ? CHECKMATE_SCORE : -CHECKMATE_SCORE;
        if (result == GameResult.BLACK_WINS) return perspective == Color.BLACK ? CHECKMATE_SCORE : -CHECKMATE_SCORE;

        int score = 0;
        score += materialBalance(board, perspective);
        score += mobilityBalance(board, perspective);
        score += positionalBalance(board, perspective);

        Color currentTurn = board.getCurrentPlayer().getColor();
        if (board.isCurrentPlayerInCheck() && currentTurn != perspective) score += CHECK_BONUS;
        if (board.isCurrentPlayerInCheck() && currentTurn == perspective) score -= CHECK_BONUS;

        return score;
    }

    private static int materialBalance(ChessBoardReadService board, Color perspective) {
        int score = 0;
        for (Piece piece : board.getPieces()) {
            int value = MaterialStrategy.pieceValue(piece) * MATERIAL_MULTIPLIER;
            if (piece.getColor() == perspective) score += value;
            else if (piece.getColor() == perspective.opposite()) score -= value;
        }
        return score;
    }

    private static int mobilityBalance(ChessBoardReadService board, Color perspective) {
        int own = countLegalMoves(board, perspective);
        int enemy = countLegalMoves(board, perspective.opposite());
        return (own - enemy) * MOBILITY_PER_MOVE;
    }

    private static int countLegalMoves(ChessBoardReadService board, Color color) {
        return board.getPieces().stream()
                .filter(p -> p.getColor() == color)
                .mapToInt(p -> board.getLegalMoves(p.getPosition()).size())
                .sum();
    }

    private static int positionalBalance(ChessBoardReadService board, Color perspective) {
        int score = 0;
        for (Piece piece : board.getPieces()) {
            Color color = piece.getColor();
            if (color != perspective && color != perspective.opposite()) continue;
            int sign = (color == perspective) ? 1 : -1;
            score += sign * pawnAdvancement(piece);
            score += sign * developmentBonus(piece);
            score += sign * centerOccupation(piece);
        }
        return score;
    }

    private static int pawnAdvancement(Piece piece) {
        if (!(piece instanceof Pawn)) return 0;
        if (piece.getPosition() == null) return 0;
        // White pawns start on row 2, black on row 7. Distance from that home rank measures
        // how far the pawn has been pushed toward the opponent.
        int pawnHome = piece.getColor() == Color.WHITE ? 2 : 7;
        int pawnDistance = Math.abs(piece.getRow().getRowNumber() - pawnHome);
        return pawnDistance * PAWN_ADVANCE_PER_RANK;
    }

    private static int developmentBonus(Piece piece) {
        if (piece instanceof Pawn) return 0;
        if (piece.isKing()) return 0;
        if (piece.getPosition() == null) return 0;
        return piece.getRow().getRowNumber() == piece.getColor().homeRow().getRowNumber() ? 0 : DEVELOPED_PIECE_BONUS;
    }

    private static int centerOccupation(Piece piece) {
        if (piece.getPosition() == null) return 0;
        return CENTER_SQUARES.contains(piece.getPosition()) ? CENTER_OCCUPATION_BONUS : 0;
    }
}
