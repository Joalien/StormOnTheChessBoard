package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.Pawn;

public class FrightCard extends Card<PieceToPositionCardParam> {

    public FrightCard() {
        super("Frayeur",
                "Faites reculer un pion adverse d'une ou deux cases.",
                CardType.AFTER_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (!(param.piece() instanceof Pawn))
            throw new IllegalArgumentException("Vous ne pouvez repousser qu'un Pion");
        if (!param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new IllegalArgumentException("Vous devez cibler un Pion adverse");
        if (param.piece().getFile() != param.positionToMoveOn().getFile())
            throw new IllegalArgumentException("Le Pion doit rester sur la même colonne");
        int distance = param.piece().getRow().distanceTo(param.positionToMoveOn().getRow());
        if (distance < 1 || distance > 2)
            throw new IllegalArgumentException("Le Pion doit reculer de 1 ou 2 cases");
        // Must move backward (toward its own side)
        boolean isBackward = (param.piece().getColor() == Color.WHITE)
                ? param.positionToMoveOn().getRow().getRowNumber() < param.piece().getRow().getRowNumber()
                : param.positionToMoveOn().getRow().getRowNumber() > param.piece().getRow().getRowNumber();
        if (!isBackward)
            throw new IllegalArgumentException("Le Pion doit reculer");
        if (chessBoard.at(param.positionToMoveOn()).getPiece().isPresent())
            throw new IllegalArgumentException("La case cible n'est pas vide");
        if (chessBoard.getEffects().stream().anyMatch(e -> e.blocksPosition(param.positionToMoveOn())))
            throw new IllegalArgumentException("La case cible est bloquée par un effet");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.at(param.piece().getPosition()).removePiece();
        chessBoard.add(param.piece(), param.positionToMoveOn());
    }
}
