package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Rock;

import java.util.List;

public class BombardCard extends Card<PieceToPositionCardParam> {

    public BombardCard() {
        super("Bombarde",
                "L'une de vos Tours peut sauter par dessus une pièce pour prendre la pièce adverse qui se trouve immédiatement au-delà.",
                CardType.AFTER_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (param.positionToMoveOn() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (!(param.piece() instanceof Rock))
            throw new IllegalArgumentException("Vous devez sélectionner une Tour");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        if (chessBoard.getOutOfTheBoardPieces().contains(param.piece()))
            throw new IllegalArgumentException("La Tour sélectionnée doit être sur le plateau");

        Position from = param.piece().getPosition();
        Position to = param.positionToMoveOn();

        // Must be on same rank or file
        if (from.getFile() != to.getFile() && from.getRow() != to.getRow())
            throw new IllegalArgumentException("La Tour doit se déplacer en ligne droite");

        // Exactly one piece on the path (the piece to jump over)
        List<Position> piecesOnPath = param.piece().squaresOnThePath(to).stream()
                .filter(pos -> chessBoard.at(pos).getPiece().isPresent())
                .toList();
        if (piecesOnPath.size() != 1)
            throw new IllegalArgumentException("Il doit y avoir exactement une pièce à sauter");

        // Target must be immediately after the piece to jump over
        Position pieceToJumpOver = piecesOnPath.get(0);
        if (!isAdjacent(pieceToJumpOver, to))
            throw new IllegalArgumentException("La cible doit être juste après la pièce sautée");

        // Target must have an enemy piece
        Piece target = chessBoard.at(to).getPiece()
                .orElseThrow(() -> new IllegalArgumentException("La case cible doit contenir une pièce adverse"));
        if (target.getColor() == param.piece().getColor())
            throw new IllegalArgumentException("Impossible de capturer votre propre pièce");
        if (target.isKing())
            throw new IllegalArgumentException("Impossible de capturer le Roi");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.fakeSquare(null, param.piece().getPosition());
        chessBoard.fakeSquare(param.piece(), param.positionToMoveOn());
        boolean check = chessBoard.isKingUnderAttack(param.piece().getColor());
        chessBoard.unfakeAllSquares();
        return !check;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.move(param.piece(), param.positionToMoveOn());
    }

    private static boolean isAdjacent(Position a, Position b) {
        int fileDiff = Math.abs(a.getFile().getFileNumber() - b.getFile().getFileNumber());
        int rowDiff = Math.abs(a.getRow().getRowNumber() - b.getRow().getRowNumber());
        return (fileDiff == 0 && rowDiff == 1) || (fileDiff == 1 && rowDiff == 0);
    }
}
