package fr.kubys.board.effect;

import fr.kubys.board.ChessBoard;
import fr.kubys.piece.Piece;

public class OriginsEffect extends Effect {

    public OriginsEffect() {
        super("OriginsEffect");
    }

    @Override
    public boolean blocksCardPlaying() {
        return true;
    }

    @Override
    public void afterMoveHook(ChessBoard board, Piece piece) {
        // Remove effect only when the enemy king is under attack (check)
        if (board.isKingUnderAttack(board.getCurrentTurn().opposite())) {
            board.removeEffect(this);
        }
    }
}
