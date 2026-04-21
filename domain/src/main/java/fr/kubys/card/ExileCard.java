package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.piece.King;
import fr.kubys.piece.Piece;

import java.util.Set;
import java.util.stream.Collectors;

public class ExileCard extends Card<PieceToPositionCardParam> {

    public ExileCard() {
        super("Exil",
                "Replacez sur la case, ou sur l'une des cases, où elle pouvait se trouver en début de partie, une pièce adverse de votre choix. La case d'arrivée doit être libre.",
                CardType.AFTER_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null) throw new IllegalStateException("Missing required card parameter");
        if (!param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new IllegalArgumentException("You must target an enemy piece");
        if (param.piece() instanceof King)
            throw new IllegalArgumentException("Cannot exile a King");
        if (chessBoard.at(param.positionToMoveOn()).getPiece().isPresent())
            throw new IllegalArgumentException("Target square must be empty");
        Set<fr.kubys.core.Position> startingPositions = ChessBoard.createWithInitialState()
                .allyPieces(param.piece().getColor()).stream()
                .filter(p -> p.getClass() == param.piece().getClass())
                .map(Piece::getPosition)
                .collect(Collectors.toSet());
        if (!startingPositions.contains(param.positionToMoveOn()))
            throw new IllegalArgumentException("Target must be a starting position for this piece type");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.at(param.piece().getPosition()).removePiece();
        chessBoard.add(param.piece(), param.positionToMoveOn());
    }
}
