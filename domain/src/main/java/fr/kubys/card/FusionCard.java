package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.piece.extra.FusedPiece;
import fr.kubys.piece.Piece;

public class FusionCard extends Card<PieceToPositionCardParam> {

    public FusionCard() {
        super("Fusion",
                "Déplacez l'une de vos pièces vers une case occupée par une autre de vos pièces. Les deux pièces fusionnent en une nouvelle, qui se déplacera désormais comme l'une ou l'autre des deux pièces d'origine. Le Roi ne peut pas fusionner.",
                CardType.REPLACE_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null)
            throw new IllegalStateException("Paramètre de carte manquant");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        if (param.piece().isKing())
            throw new IllegalArgumentException("Le Roi ne peut pas fusionner");
        Piece target = chessBoard.at(param.positionToMoveOn()).getPiece()
                .orElseThrow(() -> new IllegalArgumentException("La case de destination doit être occupée par une de vos pièces"));
        if (target.getColor() != chessBoard.getCurrentTurn())
            throw new IllegalArgumentException("La case de destination doit être occupée par une de vos pièces");
        if (target.isKing())
            throw new IllegalArgumentException("Le Roi ne peut pas fusionner");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.fakeSquare(null, param.piece().getPosition());
        boolean safe = !chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeSquare(param.piece().getPosition());
        return safe;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        Piece movingPiece = param.piece();
        Piece targetPiece = chessBoard.at(param.positionToMoveOn()).getPiece().orElseThrow();

        chessBoard.at(movingPiece.getPosition()).removePiece();
        chessBoard.removePieceFromTheBoard(movingPiece);
        chessBoard.at(param.positionToMoveOn()).removePiece();
        chessBoard.removePieceFromTheBoard(targetPiece);

        FusedPiece fusedPiece = new FusedPiece(movingPiece, targetPiece);
        chessBoard.add(fusedPiece, param.positionToMoveOn());
    }
}
