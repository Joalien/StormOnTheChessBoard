package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.MagnetismEffect;
import fr.kubys.card.params.PieceCardParam;

public class MagnetismCard extends Card<PieceCardParam> {

    private PieceCardParam param;

    public MagnetismCard() {
        super("Magnetisme", "", CardType.REPLACE_TURN);
    }

    @Override
    protected void setupParams(PieceCardParam params) {
        this.param = params;
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (param.piece() == null) throw new IllegalStateException();
        if (param.piece().getPosition() == null)
            throw new IllegalArgumentException("%s is not on the board!".formatted(param.piece()));
        if (param.piece().getColor() != isPlayedBy) throw new CannotMoveThisColorException(isPlayedBy.opposite());
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard) {
        return true;
    }

    @Override
    protected void doAction(ChessBoard chessBoard) {
        chessBoard.addEffect(new MagnetismEffect(param.piece()));
    }
}
