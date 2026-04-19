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

import java.util.List;
import java.util.stream.Stream;

public class SnowplowCard extends Card<PieceToPositionCardParam> {

    public SnowplowCard() {
        super("Chasse-Neige",
                "Déplacez une Tour, une Dame ou un Fou sur l'une de ses lignes de déplacement en prenant toutes les pièces adverses qui se trouvent sur son chemin. Votre pièce ne s'arrête que lorsqu'elle rencontre une pièce amie, le Roi adverse, ou le bord de l'échiquier.",
                CardType.REPLACE_TURN,
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

        Position from = param.piece().getPosition();
        Position to = param.positionToMoveOn();

        // Must be along a valid movement line for the piece
        if (!param.piece().isPositionTheoreticallyReachable(to))
            throw new IllegalArgumentException("La destination n'est pas sur une ligne de déplacement valide");

        // Validate: destination is the edge or just before an ally piece
        Position expectedDest = computeSnowplowDestination(chessBoard, param.piece(), to);
        if (!to.equals(expectedDest))
            throw new IllegalArgumentException("La pièce doit aller jusqu'au bord ou juste avant une pièce amie (destination attendue: %s)".formatted(expectedDest));

        // Cannot capture a king
        List<Piece> enemiesOnPath = getEnemiesOnPath(chessBoard, param.piece(), to);
        if (enemiesOnPath.stream().anyMatch(Piece::isKing))
            throw new IllegalArgumentException("Impossible de capturer le Roi");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceToPositionCardParam param) {
        List<Piece> enemies = getEnemiesOnPath(chessBoard, param.piece(), param.positionToMoveOn());
        chessBoard.fakeSquare(null, param.piece().getPosition());
        chessBoard.fakeSquare(param.piece(), param.positionToMoveOn());
        for (Piece enemy : enemies) {
            chessBoard.fakeSquare(null, enemy.getPosition());
        }
        boolean check = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeAllSquares();
        return !check;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        List<Piece> enemies = getEnemiesOnPath(chessBoard, param.piece(), param.positionToMoveOn());
        // Remove enemy pieces first
        for (Piece enemy : enemies) {
            chessBoard.removePieceFromTheBoard(enemy);
        }
        // Move the piece to destination
        chessBoard.at(param.piece().getPosition()).removePiece();
        param.piece().setPosition(null);
        chessBoard.add(param.piece(), param.positionToMoveOn());
    }

    static Position computeSnowplowDestination(ChessBoard chessBoard, Piece piece, Position directionIndicator) {
        Position from = piece.getPosition();
        int fileDir = Integer.signum(directionIndicator.getFile().getFileNumber() - from.getFile().getFileNumber());
        int rowDir = Integer.signum(directionIndicator.getRow().getRowNumber() - from.getRow().getRowNumber());

        int currentFile = from.getFile().getFileNumber() + fileDir;
        int currentRow = from.getRow().getRowNumber() + rowDir;
        Position lastValid = from;

        while (currentFile >= 1 && currentFile <= 8 && currentRow >= 1 && currentRow <= 8) {
            Position pos = Position.posToSquare(File.fromNumber(currentFile), Row.fromNumber(currentRow));
            Piece occupant = chessBoard.at(pos).getPiece().orElse(null);
            if (occupant != null && (occupant.getColor() == piece.getColor() || occupant.isKing())) {
                // Stop before ally piece or enemy king
                break;
            }
            lastValid = pos;
            currentFile += fileDir;
            currentRow += rowDir;
        }
        return lastValid;
    }

    static List<Piece> getEnemiesOnPath(ChessBoard chessBoard, Piece piece, Position dest) {
        return Stream.concat(piece.squaresOnThePath(dest).stream(), Stream.of(dest))
                .flatMap(pos -> chessBoard.at(pos).getPiece().stream())
                .filter(p -> p.getColor() != piece.getColor())
                .toList();
    }
}
