package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.PieceRemoval;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;

/**
 * Vampirism card: a piece that just captured an enemy piece transforms into the same type.
 * <p>
 * This includes Kings: a King that captures a Queen becomes a Queen. In this game,
 * the "piece to checkmate" is decoupled from the King piece type, so losing the King
 * piece type does not end the game.
 */
public class VampirismCard extends Card<PieceCardParam> {

    public VampirismCard() {
        super("Vampirisme", "L'une de vos pièces, qui vient de prendre une pièce adverse, se transforme en une pièce de même type que celle qu'elle vient de prendre. Jouez cette carte après votre coup.", CardType.AFTER_TURN, PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException();
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());

        Piece capturedPiece = findCapturedPieceThisTurn(chessBoard);
        if (capturedPiece instanceof King)
            throw new IllegalArgumentException("Cannot transform into a King");
        if (capturedPiece.getClass() == param.piece().getClass())
            throw new IllegalArgumentException("Piece is already the same type as the captured piece");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceCardParam param) {
        Piece capturedPiece = findCapturedPieceThisTurn(chessBoard);
        Piece newPiece = createPieceOfSameType(capturedPiece, param.piece().getColor());
        chessBoard.fakeSquare(newPiece, param.piece().getPosition());
        boolean isKingUnderAttack = chessBoard.isKingUnderAttack(param.piece().getColor());
        chessBoard.unfakeAllSquares();
        return !isKingUnderAttack;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        Piece capturedPiece = findCapturedPieceThisTurn(chessBoard);
        Piece newPiece = createPieceOfSameType(capturedPiece, param.piece().getColor());
        var position = param.piece().getPosition();
        chessBoard.removePieceFromTheBoard(param.piece());
        chessBoard.add(newPiece, position);
    }

    private Piece findCapturedPieceThisTurn(ChessBoard chessBoard) {
        return chessBoard.getPieceRemovals().stream()
                .filter(r -> r.turn() == chessBoard.getTurnNumber() && r.reason() == PieceRemoval.RemovalReason.CAPTURED)
                .map(PieceRemoval::piece)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No piece was captured this turn"));
    }

    private static Piece createPieceOfSameType(Piece model, Color color) {
        if (model instanceof Pawn) return color == Color.WHITE ? new WhitePawn() : new BlackPawn();
        if (model instanceof Knight) return new Knight(color);
        if (model instanceof Bishop) return new Bishop(color);
        if (model instanceof Rock) return new Rock(color);
        if (model instanceof Queen) return new Queen(color);
        throw new IllegalStateException("Unsupported piece type: " + model.getClass().getSimpleName());
    }
}
