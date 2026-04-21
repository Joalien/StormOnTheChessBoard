package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.Bishop;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Queen;
import fr.kubys.piece.Rock;

import java.util.ArrayList;
import java.util.List;

public class LaserCard extends Card<PieceToPositionCardParam> {

    public LaserCard() {
        super("Laser",
                "L'une de vos pièces (Tour, Fou ou Dame) envoie un rayon laser dans l'une de ses directions possibles de déplacement. Toutes les pièces, amies ou ennemies, qui se trouvent dans cette direction sont désintégrées. Les Rois ne sont pas affectés par le rayon laser, mais ne l'arrêtent pas.",
                CardType.AFTER_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null)
            throw new IllegalStateException("Paramètre de carte manquant");
        if (!(param.piece() instanceof Rock || param.piece() instanceof Bishop || param.piece() instanceof Queen))
            throw new IllegalArgumentException("Vous devez sélectionner une Tour, un Fou ou une Dame");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());

        // The direction indicator must be reachable by the piece type (ignoring obstacles)
        if (!param.piece().isPositionTheoreticallyReachable(param.positionToMoveOn()))
            throw new IllegalArgumentException("La direction indiquée n'est pas valide pour cette pièce");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        List<Piece> targets = getTargetsAlongRay(chessBoard, param.piece(), param.positionToMoveOn());
        for (Piece target : targets) {
            chessBoard.removePieceFromTheBoard(target);
        }
    }

    static List<Piece> getTargetsAlongRay(ChessBoard chessBoard, Piece source, Position directionIndicator) {
        List<Piece> targets = new ArrayList<>();
        Position from = source.getPosition();

        int fileDir = Integer.signum(directionIndicator.getFile().getFileNumber() - from.getFile().getFileNumber());
        int rowDir = Integer.signum(directionIndicator.getRow().getRowNumber() - from.getRow().getRowNumber());

        int currentFile = from.getFile().getFileNumber() + fileDir;
        int currentRow = from.getRow().getRowNumber() + rowDir;

        while (currentFile >= 1 && currentFile <= 8 && currentRow >= 1 && currentRow <= 8) {
            Position pos = Position.posToSquare(File.fromNumber(currentFile), Row.fromNumber(currentRow));
            chessBoard.at(pos).getPiece().ifPresent(piece -> {
                if (!piece.isKing()) {
                    targets.add(piece);
                }
            });
            currentFile += fileDir;
            currentRow += rowDir;
        }

        return targets;
    }
}
