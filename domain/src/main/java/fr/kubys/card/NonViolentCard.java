package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.NonViolentEffect;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.piece.King;

public class NonViolentCard extends Card<PieceCardParam> implements Effectable<NonViolentEffect> {

    public NonViolentCard() {
        super("Non Violent",
                "L'une de vos pièces, excepté le Roi, devient non violente jusqu'à la fin de la partie. Cette pièce ne peut plus prendre de pièce adverse, ni être prise, mais elle peut toujours donner échec.",
                CardType.AFTER_TURN,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (chessBoard.getOutOfTheBoardPieces().contains(param.piece()))
            throw new IllegalArgumentException("%s devrait être sur le plateau".formatted(param.piece()));
        if (param.piece() instanceof King)
            throw new IllegalArgumentException("Impossible de rendre le Roi non violent");
        if (param.piece().getColor() != chessBoard.getCurrentTurn())
            throw new IllegalArgumentException("Vous devez cibler une de vos propres pièces");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        chessBoard.addEffect(new NonViolentEffect(param.piece()));
    }
}
