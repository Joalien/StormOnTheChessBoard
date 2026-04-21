package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.MajorettesCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;

import java.util.Arrays;

public class MajorettesCard extends Card<MajorettesCardParam> {

    public MajorettesCard() {
        super("Majorettes",
                "Déplacez deux de vos pions latéralement d'une case, sans prendre de pièce adverse.",
                CardType.AFTER_TURN,
                MajorettesCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, MajorettesCardParam param) {
        if (param.piece1() == null || param.piece2() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (param.direction1() == null || param.direction2() == null) throw new IllegalStateException("Direction manquante");
        if (param.piece1() == param.piece2()) throw new IllegalArgumentException("Vous devez sélectionner deux pions différents");
        if (!(param.piece1() instanceof Pawn) || !(param.piece2() instanceof Pawn))
            throw new IllegalArgumentException("Les deux pièces doivent être des Pions");
        if (param.piece1().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece1().getColor());
        if (param.piece2().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece2().getColor());
        if (lateralTarget(chessBoard, param.piece1(), param.direction1()) == null)
            throw new IllegalArgumentException("%s ne peut pas aller à %s".formatted(param.piece1(), param.direction1().label));
        if (lateralTarget(chessBoard, param.piece2(), param.direction2()) == null)
            throw new IllegalArgumentException("%s ne peut pas aller à %s".formatted(param.piece2(), param.direction2().label));
    }

    @Override
    protected void doAction(ChessBoard chessBoard, MajorettesCardParam param) {
        Position target1 = lateralTarget(chessBoard, param.piece1(), param.direction1());
        Position target2 = lateralTarget(chessBoard, param.piece2(), param.direction2());
        chessBoard.at(param.piece1().getPosition()).removePiece();
        chessBoard.at(param.piece2().getPosition()).removePiece();
        chessBoard.add(param.piece1(), target1);
        chessBoard.add(param.piece2(), target2);
    }

    private Position lateralTarget(ChessBoard chessBoard, Piece pawn, Direction direction) {
        int targetFile = pawn.getFile().getFileNumber() + direction.fileDelta;
        if (targetFile < 1 || targetFile > 8) return null;
        fr.kubys.core.File f = Arrays.stream(fr.kubys.core.File.values())
                .filter(file -> file.getFileNumber() == targetFile)
                .findFirst().orElseThrow();
        Position target = Position.posToSquare(f, pawn.getRow());
        if (chessBoard.at(target).getPiece().isEmpty()) return target;
        return null;
    }

    public enum Direction {
        LEFT(-1, "gauche"),
        RIGHT(1, "droite");

        final int fileDelta;
        final String label;

        Direction(int fileDelta, String label) {
            this.fileDelta = fileDelta;
            this.label = label;
        }
    }
}
