package fr.kubys.ai.card;

import fr.kubys.ai.MaterialStrategy;
import fr.kubys.api.ChessBoardReadService;
import fr.kubys.api.GameResult;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;

import java.util.Map;
import java.util.Optional;

public final class BoardEvaluator {

    public static final int CHECKMATE_SCORE = 10_000;
    public static final int CHECK_BONUS = 20;
    private static final int MATERIAL_MULTIPLIER = 10;
    private static final int MOBILITY_PER_MOVE = 1;
    private static final int PAWN_ADVANCE_PER_RANK = 2;

    private static final Map<GameResult, Color> WINNERS = Map.of(
            GameResult.WHITE_WINS, Color.WHITE,
            GameResult.BLACK_WINS, Color.BLACK
    );

    private BoardEvaluator() {}

    /**
     * Evaluates the position from the perspective of the supplied color. Material is scaled by
     * {@value #MATERIAL_MULTIPLIER} so a pawn weighs 10. Positional bonuses are designed to
     * reward pieces that gain mobility or move closer to the centre, on a continuous
     * gradient rather than a binary "developed/not developed" check. They sum to a few units
     * in a typical opening — small enough that any real material trade dominates, but big
     * enough that cards improving piece placement (Charge, Cylindre, Skating, …) score
     * positively even when material is unchanged. Wins always dominate; opponent-in-check is
     * a small bonus; neutral pieces are ignored.
     */
    public static int evaluate(ChessBoardReadService board, Color perspective) {
        return Optional.ofNullable(WINNERS.get(board.getGameResult()))
                .map(winner -> winner == perspective ? CHECKMATE_SCORE : -CHECKMATE_SCORE)
                .orElseGet(() -> materialBalance(board, perspective)
                        + mobilityBalance(board, perspective)
                        + positionalBalance(board, perspective)
                        + checkBonus(board, perspective));
    }

    private static int materialBalance(ChessBoardReadService board, Color perspective) {
        return board.getPieces().stream()
                .mapToInt(piece -> sideSign(piece, perspective) * MaterialStrategy.pieceValue(piece) * MATERIAL_MULTIPLIER)
                .sum();
    }

    /**
     * Sums the legal-move count of each side. A piece that gains moves (because a card opened
     * a line, removed a blocker, granted alternative movement, …) directly raises the side's
     * mobility, even if no other term changes.
     */
    private static int mobilityBalance(ChessBoardReadService board, Color perspective) {
        return (countLegalMoves(board, perspective) - countLegalMoves(board, perspective.opposite())) * MOBILITY_PER_MOVE;
    }

    private static int countLegalMoves(ChessBoardReadService board, Color color) {
        return board.getPieces().stream()
                .filter(p -> p.getColor() == color)
                .mapToInt(p -> board.getLegalMoves(p.getPosition()).size())
                .sum();
    }

    private static int positionalBalance(ChessBoardReadService board, Color perspective) {
        return board.getPieces().stream()
                .mapToInt(piece -> sideSign(piece, perspective) * (pawnAdvancement(piece) + centerProximity(piece)))
                .sum();
    }

    private static int sideSign(Piece piece, Color perspective) {
        Color color = piece.getColor();
        if (color == perspective) return 1;
        if (color == perspective.opposite()) return -1;
        return 0;
    }

    private static int checkBonus(ChessBoardReadService board, Color perspective) {
        if (!board.isCurrentPlayerInCheck()) return 0;
        return board.getCurrentPlayer().getColor() == perspective ? -CHECK_BONUS : CHECK_BONUS;
    }

    private static int pawnAdvancement(Piece piece) {
        return Optional.of(piece)
                .filter(Pawn.class::isInstance)
                .map(Piece::getPosition)
                .map(pos -> {
                    int pawnHome = piece.getColor() == Color.WHITE ? 2 : 7;
                    return Math.abs(pos.getRow().getRowNumber() - pawnHome) * PAWN_ADVANCE_PER_RANK;
                })
                .orElse(0);
    }

    /**
     * Continuous gradient that rewards any piece moving toward the geometric centre of the
     * board, not just the four central squares. Uses Chebyshev (king) distance to the centre
     * point (4.5, 4.5): the four innermost squares score the highest, and each ring outward
     * loses one point. Applied to every piece (including pawns and king) so cards that
     * displace pieces toward d/e files and rows 4-5 are rewarded smoothly.
     */
    private static int centerProximity(Piece piece) {
        return Optional.ofNullable(piece.getPosition())
                .map(BoardEvaluator::chebyshevToCenter)
                .map(d -> (int) Math.round(4 - d))
                .orElse(0);
    }

    private static double chebyshevToCenter(Position position) {
        double fileDist = Math.abs(position.getFile().getFileNumber() - 4.5);
        double rowDist = Math.abs(position.getRow().getRowNumber() - 4.5);
        return Math.max(fileDist, rowDist); // 0.5 (centre) … 3.5 (corner)
    }
}
