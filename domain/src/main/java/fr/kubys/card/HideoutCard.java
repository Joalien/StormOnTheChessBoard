package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.HideoutEffect;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.piece.King;

public class HideoutCard extends Card<PieceCardParam> implements Effectable<HideoutEffect> {

    public HideoutCard() {
        super("Planque",
                "Une pièce adverse de votre choix est endormie. Elle ne peut plus bouger, et ne sera réveillée que lorsque le Roi de votre adversaire aura été mis en échec.",
                CardType.AFTER_TURN,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (chessBoard.getOutOfTheBoardPieces().contains(param.piece()))
            throw new IllegalArgumentException("%s devrait être sur le plateau".formatted(param.piece()));
        if (param.piece() instanceof King)
            throw new IllegalArgumentException("Impossible d'endormir le Roi");
        if (param.piece().getColor() == chessBoard.getCurrentTurn())
            throw new IllegalArgumentException("Vous devez cibler une pièce adverse");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceCardParam param) {
        return true;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        chessBoard.addEffect(new HideoutEffect(param.piece()));
    }
}
