package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;

public class NeutralityCard extends Card<PieceCardParam> {

    public NeutralityCard() {
        super("Neutralité",
                "Transformez une pièce adverse (sauf Roi ou Dame) en pièce neutre. Une pièce neutre peut être utilisée par les deux joueurs et peut prendre des pièces appartenant aux deux joueurs.",
                CardType.AFTER_TURN,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException();
        if (chessBoard.getOutOfTheBoardPieces().contains(param.piece()))
            throw new IllegalArgumentException("%s should be on the board".formatted(param.piece()));
        if (param.piece() instanceof King)
            throw new IllegalArgumentException("Cannot neutralize a King");
        if (param.piece() instanceof Queen)
            throw new IllegalArgumentException("Cannot neutralize a Queen");
        if (param.piece().getColor() == chessBoard.getCurrentTurn())
            throw new IllegalArgumentException("Cannot neutralize your own piece");
        if (param.piece().getColor() == Color.NONE)
            throw new IllegalArgumentException("Piece is already neutral");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceCardParam param) {
        Color originalColor = param.piece().getColor();
        param.piece().setColor(Color.NONE);
        boolean check = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        param.piece().setColor(originalColor);
        return !check;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() instanceof Pawn) {
            var position = param.piece().getPosition();
            chessBoard.removePieceFromTheBoard(param.piece());
            chessBoard.add(new Pawn(Color.NONE), position);
        } else {
            param.piece().setColor(Color.NONE);
        }
    }
}
