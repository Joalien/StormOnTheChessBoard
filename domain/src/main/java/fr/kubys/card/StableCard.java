package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.StableCardParam;
import fr.kubys.core.Position;

// TODO generify me to allow other swap cards
public class StableCard extends Card<StableCardParam> {

    public StableCard() {
        super("Écurie", "Sur l'échiquier, permutez l'un de vos cavaliers avec l'une de vos tours", CardType.AFTER_TURN, StableCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, StableCardParam param) {
        if (param.rock() == null) throw new IllegalStateException();
        if (param.knight() == null) throw new IllegalStateException();
        if (param.rock().getColor() != param.knight().getColor())
            throw new IllegalArgumentException("You should swap pieces of the same color");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, StableCardParam param) {
        return true;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, StableCardParam param) {
        Position rockPosition = param.rock().getPosition();
        Position knightPosition = param.knight().getPosition();

        chessBoard.removePieceFromTheBoard(param.rock());
        chessBoard.removePieceFromTheBoard(param.knight());

        chessBoard.add(param.rock(), knightPosition);
        chessBoard.add(param.knight(), rockPosition);
    }
}
