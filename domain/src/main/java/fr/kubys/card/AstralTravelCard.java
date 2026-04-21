package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.AstralTravelEffect;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.piece.Piece;

public class AstralTravelCard extends Card<PieceCardParam> implements Effectable<AstralTravelEffect> {

    public AstralTravelCard() {
        super("Voyage Astral",
                "Enlevez de l'échiquier l'une de vos pièces ou une pièce adverse, que vous remettez à sa place après votre déplacement. Vous pouvez donc franchir librement cette case, mais ne pouvez pas y arrêter la pièce que vous déplacez.",
                CardType.BEFORE_TURN,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException("Paramètre de carte manquant");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceCardParam param) {
        // Temporarily removing a piece shouldn't cause check since we put it back after the move
        return true;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        chessBoard.addEffect(new AstralTravelEffect(param.piece(), param.piece().getPosition()));
        chessBoard.removePieceFromTheBoard(param.piece());
    }
}
