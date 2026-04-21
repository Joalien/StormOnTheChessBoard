package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.piece.Pawn;

public class RingRoadCard extends Card<PieceToPositionCardParam> {

    public RingRoadCard() {
        super("Périph",
                "Déplacez l'une de vos pièces (sauf un Pion) depuis une case du bord de l'échiquier vers n'importe quelle autre case libre du bord de l'échiquier.",
                CardType.REPLACE_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null)
            throw new IllegalStateException("Paramètre de carte manquant");
        if (param.piece() instanceof Pawn)
            throw new IllegalArgumentException("Impossible de déplacer un Pion avec cette carte");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        if (!param.piece().getPosition().isBorder())
            throw new IllegalArgumentException("La pièce doit se trouver sur le bord de l'échiquier");
        if (!param.positionToMoveOn().isBorder())
            throw new IllegalArgumentException("La destination doit être sur le bord de l'échiquier");
        if (chessBoard.at(param.positionToMoveOn()).getPiece().isPresent())
            throw new IllegalArgumentException("La case de destination doit être libre");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.at(param.piece().getPosition()).removePiece();
        chessBoard.add(param.piece(), param.positionToMoveOn());
    }
}
