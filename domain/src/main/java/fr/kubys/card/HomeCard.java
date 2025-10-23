package fr.kubys.card;

import fr.kubys.board.CannotTakeKingException;
import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;

public class HomeCard extends Card<PieceToPositionCardParam> {

    public HomeCard() {
        super("Maison", "Ramener l'une de vos pièces (pas un pion) sur l'une des cases où elle pouvait se trouver en début de partie. Vous pouvez même prendre ainsi une pièce adverse", CardType.REPLACE_TURN, PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null) throw new IllegalStateException();
        if (param.positionToMoveOn() == null) throw new IllegalStateException();
        if (param.piece() instanceof Pawn) throw new IllegalArgumentException("You cannot rollback a pawn!");
        if (param.piece().getColor() != chessBoard.getCurrentTurn()) throw new CannotMoveThisColorException(param.piece().getColor());
        boolean positionToMoveOnIsNotStartingPositionOfPiece = ChessBoard.createWithInitialState()
                .allyPieces(param.piece().getColor())
                .stream()
                .filter(piece1 -> piece1.getClass() == param.piece().getClass())
                .map(Piece::getPosition)
                .noneMatch(pos -> pos.equals(param.positionToMoveOn()));
        if (positionToMoveOnIsNotStartingPositionOfPiece)
            throw new IllegalArgumentException("%s didn't start the game on square %s".formatted(param.piece(), param.positionToMoveOn()));

        Boolean positionToMoveOnHasSameColorPiece = chessBoard.at(param.positionToMoveOn())
                .getPiece()
                .map(Piece::getColor)
                .map(color -> color == param.piece().getColor())
                .orElse(false);
        if (positionToMoveOnHasSameColorPiece)
            throw new IllegalArgumentException("You cannot rollback on a square occupied by an ally piece");
        if (chessBoard.at(param.positionToMoveOn()).getPiece().map(Piece::isKing).orElse(false)) {
            throw new CannotTakeKingException();
        }

    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceToPositionCardParam param) {
        return true; // FIXME
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.at(param.positionToMoveOn()).getPiece().ifPresent(chessBoard::removePieceFromTheBoard);
        chessBoard.move(param.piece(), param.positionToMoveOn());
    }
}
