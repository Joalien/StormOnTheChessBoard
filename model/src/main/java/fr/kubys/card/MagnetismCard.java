package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.MagnetismEffect;
import fr.kubys.card.params.PieceCardParam;

public class MagnetismCard extends Card<PieceCardParam> {

    public MagnetismCard() {
        super("Magnetisme", "", CardType.REPLACE_TURN, PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException();
        if (param.piece().getPosition() == null)
            throw new IllegalArgumentException("%s is not on the board!".formatted(param.piece()));
        if (param.piece().getColor() != chessBoard.getCurrentTurn()) throw new CannotMoveThisColorException(chessBoard.getCurrentTurn().opposite());
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceCardParam param) {
        return true;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        chessBoard.addEffect(new MagnetismEffect(param.piece()));
    }
}
