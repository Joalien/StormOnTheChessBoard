package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.Bishop;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Queen;
import fr.kubys.piece.Rock;

import java.util.HashSet;
import java.util.Set;

public class CylinderCard extends Card<PieceToPositionCardParam> {

    public CylinderCard() {
        super("Cylindre",
                "Déplacez l'une de vos pièces en la faisant passer d'un côté à l'autre de l'échiquier, comme si ce dernier était cylindrique, et si les bords gauche et droit étaient contigus.",
                CardType.REPLACE_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null)
            throw new IllegalStateException("Paramètre de carte manquant");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        if (param.piece() instanceof Pawn)
            throw new IllegalArgumentException("Les pions ne peuvent pas se déplacer en cylindre");

        Position dest = param.positionToMoveOn();
        Piece piece = param.piece();

        // Destination must be empty or enemy
        chessBoard.at(dest).getPiece().ifPresent(target -> {
            if (target.getColor() == chessBoard.getCurrentTurn())
                throw new IllegalArgumentException("Impossible de capturer votre propre pièce");
            if (target.isKing())
                throw new IllegalArgumentException("Impossible de capturer le Roi");
        });

        if (!isCylindricallyReachable(chessBoard, piece, dest))
            throw new IllegalArgumentException("La pièce ne peut pas atteindre cette case en mode cylindrique");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.move(param.piece(), param.positionToMoveOn());
    }

    private boolean isCylindricallyReachable(ChessBoard chessBoard, Piece piece, Position dest) {
        Position from = piece.getPosition();

        if (piece instanceof Knight) {
            return isKnightCylindricallyReachable(from, dest);
        }

        // For Rock, Bishop, Queen: check if reachable via wrapping
        if (piece instanceof Rock) {
            return isLineCylindricallyReachable(chessBoard, from, dest, true, false);
        } else if (piece instanceof Bishop) {
            return isDiagonalCylindricallyReachable(chessBoard, from, dest);
        } else if (piece instanceof Queen) {
            return isLineCylindricallyReachable(chessBoard, from, dest, true, false)
                    || isDiagonalCylindricallyReachable(chessBoard, from, dest);
        }

        return false;
    }

    private boolean isKnightCylindricallyReachable(Position from, Position dest) {
        int fileDist = from.getFile().distanceTo(dest.getFile());
        int rowDist = from.getRow().distanceTo(dest.getRow());
        // Wrap file distance: min of direct and wrapped
        int wrappedFileDist = Math.min(fileDist, 8 - fileDist);
        return (wrappedFileDist + rowDist == 3) && wrappedFileDist > 0 && rowDist > 0;
    }

    private boolean isLineCylindricallyReachable(ChessBoard chessBoard, Position from, Position dest, boolean checkRow, boolean isDiagonal) {
        if (from.getRow() == dest.getRow()) {
            // Same row - check wrapping path
            return isRowPathClearWrapping(chessBoard, from, dest);
        }
        return false;
    }

    private boolean isDiagonalCylindricallyReachable(ChessBoard chessBoard, Position from, Position dest) {
        int fileDist = from.getFile().distanceTo(dest.getFile());
        int rowDist = from.getRow().distanceTo(dest.getRow());
        int wrappedFileDist = 8 - fileDist;

        // The diagonal wrapping: the wrapped file distance must equal the row distance
        if (wrappedFileDist != rowDist || wrappedFileDist == 0) return false;

        // Determine wrapping direction
        // If going right wraps to left side
        return isDiagonalPathClearWrapping(chessBoard, from, dest, wrappedFileDist);
    }

    private boolean isRowPathClearWrapping(ChessBoard chessBoard, Position from, Position dest) {
        int fromFile = from.getFile().getFileNumber();
        int destFile = dest.getFile().getFileNumber();
        int directDist = Math.abs(fromFile - destFile);
        int wrappedDist = 8 - directDist;

        // Only consider the wrapping path (non-wrapping is standard chess)
        if (wrappedDist >= directDist) return false; // Would use direct path normally

        // Check wrapping path: go in the opposite direction to direct
        int direction = (fromFile < destFile) ? -1 : 1; // opposite of direct direction
        int current = fromFile;
        for (int i = 0; i < wrappedDist - 1; i++) {
            current = wrapFile(current + direction);
            Position intermediate = Position.posToSquare(File.fromNumber(current), from.getRow());
            if (chessBoard.at(intermediate).getPiece().isPresent()) return false;
        }
        return true;
    }

    private boolean isDiagonalPathClearWrapping(ChessBoard chessBoard, Position from, Position dest, int distance) {
        int fromFile = from.getFile().getFileNumber();
        int fromRow = from.getRow().getRowNumber();
        int destFile = dest.getFile().getFileNumber();
        int destRow = dest.getRow().getRowNumber();

        // Determine the wrapping direction for file
        // Try going left (wrapping) if direct is right, and vice versa
        int rowDir = (destRow > fromRow) ? 1 : -1;

        // Try both file wrap directions, pick the one that results in wrapping
        for (int fileDir : new int[]{-1, 1}) {
            boolean valid = true;
            int curFile = fromFile;
            int curRow = fromRow;
            boolean wrapped = false;
            for (int i = 1; i <= distance; i++) {
                curFile = wrapFile(curFile + fileDir);
                curRow = curRow + rowDir;
                if (curRow < 1 || curRow > 8) { valid = false; break; }
                if (curFile != fromFile + fileDir * i || curFile < 1 || curFile > 8) wrapped = true;

                if (i == distance) {
                    if (curFile != destFile || curRow != destRow) { valid = false; break; }
                } else {
                    Position intermediate = Position.posToSquare(File.fromNumber(curFile), Row.fromNumber(curRow));
                    if (chessBoard.at(intermediate).getPiece().isPresent()) { valid = false; break; }
                }
            }
            if (valid && wrapped) return true;
        }
        return false;
    }

    private static int wrapFile(int file) {
        if (file < 1) return file + 8;
        if (file > 8) return file - 8;
        return file;
    }
}
