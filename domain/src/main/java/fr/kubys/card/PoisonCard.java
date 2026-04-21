package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.PoisonEffect;
import fr.kubys.card.params.PieceCardParam;

public class PoisonCard extends Card<PieceCardParam> implements Effectable<PoisonEffect> {

    public PoisonCard() {
        super("Poison",
                "Désignez l'une de vos pièces dont la chair est, définitivement, empoisonnée. Si une pièce adverse prend la pièce empoisonnée, elles sont toutes deux retirées de l'échiquier.",
                CardType.AFTER_TURN,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        if (param.piece().isKing())
            throw new IllegalArgumentException("Impossible d'empoisonner le Roi");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceCardParam param) {
        return true;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        chessBoard.addEffect(new PoisonEffect(param.piece()));
    }
}
