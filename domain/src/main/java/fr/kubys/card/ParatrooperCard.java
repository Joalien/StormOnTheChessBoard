package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Pawn;

import java.util.Set;

public class ParatrooperCard extends Card<PieceToPositionCardParam> {

    private static final Set<Position> CENTER = Set.of(Position.d4, Position.e4, Position.d5, Position.e5);

    public ParatrooperCard() {
        super("Parachutiste",
                "Parachutez l'un des Pions que vous a pris votre adversaire sur l'une des quatre cases centrales de l'échiquier. Cette case doit être inoccupée.",
                CardType.REPLACE_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (!chessBoard.getOutOfTheBoardPieces().contains(param.piece()))
            throw new IllegalArgumentException("Le Pion doit avoir été capturé");
        if (!(param.piece() instanceof Pawn))
            throw new IllegalArgumentException("Doit être un Pion");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new IllegalArgumentException("Vous devez parachuter votre propre Pion capturé");
        if (!CENTER.contains(param.positionToMoveOn()))
            throw new IllegalArgumentException("La cible doit être une des quatre cases centrales");
        if (chessBoard.at(param.positionToMoveOn()).getPiece().isPresent())
            throw new IllegalArgumentException("La case cible doit être vide");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.add(param.piece(), param.positionToMoveOn());
    }
}
