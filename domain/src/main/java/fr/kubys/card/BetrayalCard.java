package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.Pawn;

public class BetrayalCard extends Card<PieceCardParam> {

    public BetrayalCard() {
        super("Trahison",
                "Un Pion adverse qui se trouve déjà de votre côté de l'échiquier change soudain de couleur et passe dans votre camp.",
                CardType.BEFORE_TURN,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (!(param.piece() instanceof Pawn))
            throw new IllegalArgumentException("Vous devez sélectionner un Pion");
        if (param.piece().getColor() == chessBoard.getCurrentTurn())
            throw new IllegalArgumentException("Vous devez sélectionner un Pion adverse");
        if (!isOnMySide(param.piece(), chessBoard.getCurrentTurn()))
            throw new IllegalArgumentException("Le Pion doit se trouver de votre côté de l'échiquier");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        param.piece().setColor(chessBoard.getCurrentTurn());
    }

    private boolean isOnMySide(fr.kubys.piece.Piece piece, Color myColor) {
        int row = piece.getRow().getRowNumber();
        if (myColor == Color.WHITE) return row <= 4;
        else return row >= 5;
    }
}
