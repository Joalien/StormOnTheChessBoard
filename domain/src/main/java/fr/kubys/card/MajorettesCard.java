package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.TwoPieceCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Pawn;

public class MajorettesCard extends Card<TwoPieceCardParam> {

    public MajorettesCard() {
        super("Majorettes",
                "Déplacez deux de vos pions latéralement d'une case, sans prendre de pièce adverse.",
                CardType.AFTER_TURN,
                TwoPieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, TwoPieceCardParam param) {
        if (param.piece1() == null || param.piece2() == null) throw new IllegalStateException();
        if (param.piece1() == param.piece2()) throw new IllegalArgumentException("Must select two different pawns");
        if (!(param.piece1() instanceof Pawn) || !(param.piece2() instanceof Pawn))
            throw new IllegalArgumentException("Both pieces must be Pawns");
        if (param.piece1().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece1().getColor());
        if (param.piece2().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece2().getColor());
        // Check each pawn has a free lateral square
        if (lateralTarget(chessBoard, param.piece1()) == null)
            throw new IllegalArgumentException("%s has no free lateral square".formatted(param.piece1()));
        if (lateralTarget(chessBoard, param.piece2()) == null)
            throw new IllegalArgumentException("%s has no free lateral square".formatted(param.piece2()));
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, TwoPieceCardParam param) {
        // Simplified: this is AFTER_TURN so our king should be safe
        return true;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, TwoPieceCardParam param) {
        Position target1 = lateralTarget(chessBoard, param.piece1());
        Position target2 = lateralTarget(chessBoard, param.piece2());
        chessBoard.at(param.piece1().getPosition()).removePiece();
        chessBoard.at(param.piece2().getPosition()).removePiece();
        chessBoard.add(param.piece1(), target1);
        chessBoard.add(param.piece2(), target2);
    }

    private Position lateralTarget(ChessBoard chessBoard, fr.kubys.piece.Piece pawn) {
        // Try left then right
        int fileNum = pawn.getFile().getFileNumber();
        for (int delta : new int[]{-1, 1}) {
            int targetFile = fileNum + delta;
            if (targetFile >= 1 && targetFile <= 8) {
                fr.kubys.core.File f = java.util.Arrays.stream(fr.kubys.core.File.values())
                        .filter(file -> file.getFileNumber() == targetFile)
                        .findFirst().orElseThrow();
                Position target = Position.posToSquare(f, pawn.getRow());
                if (chessBoard.at(target).getPiece().isEmpty()) return target;
            }
        }
        return null;
    }
}
