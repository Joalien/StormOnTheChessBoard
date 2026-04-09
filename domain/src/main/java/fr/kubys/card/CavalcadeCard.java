package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.piece.Knight;

public class CavalcadeCard extends Card<PieceToPositionCardParam> {

    public CavalcadeCard() {
        super("Cavalcade", "Déplacer n'importe laquelle de vos pièces à la manière d'un cavalier. Vous ne pouvez pas prendre ainsi une pièce adverse", CardType.REPLACE_TURN, PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null) throw new IllegalStateException();
        if (param.positionToMoveOn() == null) throw new IllegalStateException();
        if (param.piece().getColor() != chessBoard.getCurrentTurn())
            throw new CannotMoveThisColorException(param.piece().getColor());

        Knight fakeKnight = new Knight(param.piece().getColor());
        fakeKnight.setPosition(param.piece().getPosition());
        if (!fakeKnight.isPositionTheoreticallyReachable(param.positionToMoveOn()))
            throw new IllegalArgumentException("%s cannot reach %s with a knight move".formatted(param.piece(), param.positionToMoveOn()));

        chessBoard.at(param.positionToMoveOn()).getPiece().ifPresent(pieceOnTarget -> {
            if (pieceOnTarget.getColor() != param.piece().getColor())
                throw new IllegalArgumentException("You cannot capture an enemy piece with Cavalcade");
            else
                throw new IllegalArgumentException("You cannot move to a square occupied by an ally piece");
        });
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.fakeSquare(null, param.piece().getPosition());
        chessBoard.fakeSquare(param.piece(), param.positionToMoveOn());
        boolean isKingUnderAttack = chessBoard.isKingUnderAttack(param.piece().getColor());
        chessBoard.unfakeAllSquares();
        return !isKingUnderAttack;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.move(param.piece(), param.positionToMoveOn());
    }
}
