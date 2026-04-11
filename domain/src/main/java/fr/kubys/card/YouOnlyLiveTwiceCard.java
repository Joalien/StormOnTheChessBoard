package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.piece.*;

import java.util.Set;
import java.util.stream.Collectors;

public class YouOnlyLiveTwiceCard extends Card<PieceToPositionCardParam> {

    public YouOnlyLiveTwiceCard() {
        super("On ne vit que deux fois",
                "Récupérez l'une des pièces (sauf la Dame) que vous a prises votre adversaire, et placez-la sur une case libre où elle pouvait se trouver en début de partie.",
                CardType.AFTER_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null) throw new IllegalStateException("Missing required card parameter");
        if (!chessBoard.getOutOfTheBoardPieces().contains(param.piece()))
            throw new IllegalArgumentException("Piece must have been captured");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new IllegalArgumentException("Must resurrect one of your own pieces");
        if (param.piece() instanceof Queen)
            throw new IllegalArgumentException("Cannot resurrect a Queen");
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
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.fakeSquare(param.piece(), param.positionToMoveOn());
        boolean check = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeSquare(param.positionToMoveOn());
        return !check;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.add(param.piece(), param.positionToMoveOn());
    }
}
