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
        if (param.piece() == null) throw new IllegalStateException("Missing required card parameter");
        if (param.positionToMoveOn() == null) throw new IllegalStateException("Missing required card parameter");
        if (param.piece() instanceof Pawn) throw new IllegalArgumentException("You cannot rollback a pawn!");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn())) throw new CannotMoveThisColorException(param.piece().getColor());
        boolean positionToMoveOnIsNotStartingPositionOfPiece = ChessBoard.createWithInitialState()
                .allyPieces(param.piece().getColor())
                .stream()
                .filter(piece1 -> piece1.getClass() == param.piece().getClass())
                .map(Piece::getPosition)
                .noneMatch(pos -> pos.equals(param.positionToMoveOn()));
        if (positionToMoveOnIsNotStartingPositionOfPiece)
            throw new IllegalArgumentException("%s didn't start the game on square %s".formatted(param.piece(), param.positionToMoveOn()));

        boolean positionToMoveOnHasSameColorPiece = chessBoard.at(param.positionToMoveOn())
                .getPiece()
                .filter(p -> p.getColor() == param.piece().getColor())
                .isPresent();
        if (positionToMoveOnHasSameColorPiece)
            throw new IllegalArgumentException("You cannot rollback on a square occupied by an ally piece");
        if (chessBoard.at(param.positionToMoveOn()).getPiece().filter(Piece::isKing).isPresent()) {
            throw new CannotTakeKingException();
        }

    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.at(param.positionToMoveOn()).getPiece().ifPresent(chessBoard::removePieceFromTheBoard);
        chessBoard.move(param.piece(), param.positionToMoveOn());
    }
}
