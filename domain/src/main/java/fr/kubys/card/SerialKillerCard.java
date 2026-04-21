package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.SerialKillerCardParam;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.King;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;

import java.util.List;

public class SerialKillerCard extends Card<SerialKillerCardParam> {

    public SerialKillerCard() {
        super("Serial Killer",
                "L'un de vos Pions, qui vient de prendre une pièce adverse, est pris d'une frénésie destructrice. Continuez à déplacer, en diagonale, le même pion en prenant à chaque déplacement une pièce adverse, tant que cela est possible.",
                CardType.AFTER_TURN,
                SerialKillerCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, SerialKillerCardParam param) {
        if (!(param.pawn() instanceof Pawn))
            throw new IllegalArgumentException("La pièce sélectionnée doit être un Pion");
        if (param.pawn().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.pawn().getColor());
        if (param.positions() == null || param.positions().isEmpty())
            throw new IllegalArgumentException("La liste de positions ne peut pas être vide");

        Piece pawn = param.pawn();
        Position currentPos = pawn.getPosition();
        Color pawnColor = pawn.getColor();

        for (Position targetPos : param.positions()) {
            if (!isDiagonalForward(currentPos, targetPos, pawnColor))
                throw new IllegalArgumentException("%s n'est pas une diagonale avant depuis %s".formatted(targetPos, currentPos));

            Piece target = chessBoard.at(targetPos).getPiece()
                    .orElseThrow(() -> new IllegalArgumentException("Pas de pièce à capturer sur %s".formatted(targetPos)));
            if (target.getColor() == pawnColor)
                throw new IllegalArgumentException("Impossible de capturer sa propre pièce sur %s".formatted(targetPos));
            if (target instanceof King)
                throw new IllegalArgumentException("Impossible de capturer le Roi");

            currentPos = targetPos;
        }
    }

    @Override
    protected void doAction(ChessBoard chessBoard, SerialKillerCardParam param) {
        Piece pawn = param.pawn();
        for (Position pos : param.positions()) {
            chessBoard.move(pawn, pos);
        }
    }

    private boolean isDiagonalForward(Position from, Position to, Color pawnColor) {
        int fileDiff = Math.abs(to.getFile().getFileNumber() - from.getFile().getFileNumber());
        int rowDiff = to.getRow().getRowNumber() - from.getRow().getRowNumber();
        int expectedRowDiff = (pawnColor == Color.WHITE) ? 1 : -1;
        return fileDiff == 1 && rowDiff == expectedRowDiff;
    }
}
