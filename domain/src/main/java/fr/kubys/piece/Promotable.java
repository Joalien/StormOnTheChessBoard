package fr.kubys.piece;

/**
 * A piece that can be promoted when it reaches a specific rank.
 * <p>
 * When a {@code Promotable} piece is placed on the board via {@link fr.kubys.board.ChessBoard#add},
 * the board checks {@link #isOnPromotionRow()}. If true, the piece is automatically replaced
 * by a Queen, and the position is tracked for the player to override the promotion
 * (under-promotion to Rook, Bishop, or Knight).
 * <p>
 * Implemented by {@link Pawn} (promotes on the opponent's back rank)
 * and {@link Crab} (same promotion rules, different movement).
 */
public interface Promotable {
    boolean isOnPromotionRow();
}
