package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.PieceRemoval;
import fr.kubys.card.params.LeapfrogCardParam;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.Pawn;

public class LeapfrogCard extends Card<LeapfrogCardParam> {

    public LeapfrogCard() {
        super("Saute-mouton",
                "Déplacez l'un de vos pions comme au jeu de Dames, en sautant sur les diagonales par dessus vos pièces ou celles de l'adversaire. Toutes les pièces sautées sont prises et rejoignent les pièces capturées.",
                CardType.REPLACE_TURN,
                LeapfrogCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, LeapfrogCardParam param) {
        if (!(param.pawn() instanceof Pawn))
            throw new IllegalArgumentException("La pièce sélectionnée doit être un Pion");
        if (param.pawn().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.pawn().getColor());
        if (chessBoard.getOutOfTheBoardPieces().contains(param.pawn()))
            throw new IllegalArgumentException("Le Pion n'est pas sur l'échiquier");
        if (param.positions() == null || param.positions().isEmpty())
            throw new IllegalArgumentException("La liste de cases ne peut pas être vide");

        Position currentPos = param.pawn().getPosition();
        for (Position landing : param.positions()) {
            validateSingleJump(chessBoard, currentPos, landing, param.pawn().getPosition());
            currentPos = landing;
        }
    }

    private void validateSingleJump(ChessBoard chessBoard, Position from, Position landing, Position pawnStart) {
        int fileDiff = landing.getFile().getFileNumber() - from.getFile().getFileNumber();
        int rowDiff = landing.getRow().getRowNumber() - from.getRow().getRowNumber();

        if (Math.abs(fileDiff) != 2 || Math.abs(rowDiff) != 2)
            throw new IllegalArgumentException("%s n'est pas un saut diagonal valide depuis %s".formatted(landing, from));

        Position midPos = midpoint(from, landing);
        boolean midHasPiece = chessBoard.at(midPos).getPiece().isPresent() && !midPos.equals(pawnStart);
        if (!midHasPiece)
            throw new IllegalArgumentException("Pas de pièce à sauter sur %s".formatted(midPos));

        boolean landEmpty = chessBoard.at(landing).getPiece().isEmpty() || landing.equals(pawnStart);
        if (!landEmpty)
            throw new IllegalArgumentException("La case d'arrivée %s n'est pas vide".formatted(landing));
    }

    @Override
    protected void doAction(ChessBoard chessBoard, LeapfrogCardParam param) {
        Position currentPos = param.pawn().getPosition();
        for (Position landing : param.positions()) {
            Position midPos = midpoint(currentPos, landing);
            chessBoard.at(midPos).getPiece()
                    .ifPresent(jumped -> chessBoard.removePieceFromTheBoard(jumped, PieceRemoval.RemovalReason.CAPTURED));
            currentPos = landing;
        }
        chessBoard.move(param.pawn(), param.positions().get(param.positions().size() - 1));
    }

    private static Position midpoint(Position from, Position to) {
        int fileDiff = to.getFile().getFileNumber() - from.getFile().getFileNumber();
        int rowDiff = to.getRow().getRowNumber() - from.getRow().getRowNumber();
        return Position.posToSquare(
                File.fromNumber(from.getFile().getFileNumber() + fileDiff / 2),
                Row.fromNumber(from.getRow().getRowNumber() + rowDiff / 2));
    }
}
