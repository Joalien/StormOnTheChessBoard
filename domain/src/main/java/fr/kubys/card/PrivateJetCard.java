package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PositionCardParam;
import fr.kubys.piece.Piece;

public class PrivateJetCard extends Card<PositionCardParam> {

    public PrivateJetCard() {
        super("Avion privé",
                "Déplacez votre Roi pour l'amener sur n'importe quelle case libre de l'échiquier.",
                CardType.REPLACE_TURN,
                PositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PositionCardParam param) {
        if (param.position() == null) throw new IllegalStateException();
        if (chessBoard.at(param.position()).getPiece().isPresent())
            throw new IllegalArgumentException("Target square must be empty");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PositionCardParam param) {
        Piece king = findKing(chessBoard);
        chessBoard.fakeSquare(king, param.position());
        chessBoard.fakeSquare(null, king.getPosition());
        boolean check = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeAllSquares();
        return !check;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PositionCardParam param) {
        Piece king = findKing(chessBoard);
        chessBoard.at(king.getPosition()).removePiece();
        chessBoard.add(king, param.position());
    }

    private Piece findKing(ChessBoard chessBoard) {
        return chessBoard.allyPieces(chessBoard.getCurrentTurn()).stream()
                .filter(Piece::isKing)
                .findFirst()
                .orElseThrow();
    }
}
