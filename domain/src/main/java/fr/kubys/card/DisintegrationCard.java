package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.piece.King;

public class DisintegrationCard extends Card<PieceCardParam> {

    public DisintegrationCard() {
        super("Désintégration",
                "Retirez de l'échiquier l'une de vos propres pièces.",
                CardType.AFTER_TURN,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException("Missing required card parameter");
        if (chessBoard.getOutOfTheBoardPieces().contains(param.piece()))
            throw new IllegalArgumentException("%s should be on the board".formatted(param.piece()));
        if (param.piece().getColor() != chessBoard.getCurrentTurn())
            throw new IllegalArgumentException("Cannot remove an enemy piece");
        if (param.piece() instanceof King)
            throw new IllegalArgumentException("Cannot remove a King");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        chessBoard.removePieceFromTheBoard(param.piece());
    }
}
