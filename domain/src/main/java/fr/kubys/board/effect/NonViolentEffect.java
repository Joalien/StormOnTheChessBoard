package fr.kubys.board.effect;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.util.List;

public class NonViolentEffect extends Effect {

    private final Piece nonViolentPiece;

    public NonViolentEffect(Piece nonViolentPiece) {
        super("Non Violent");
        this.nonViolentPiece = nonViolentPiece;
    }

    public Piece getNonViolentPiece() {
        return nonViolentPiece;
    }

    @Override
    public List<Position> getPositions() {
        return nonViolentPiece.findPosition()
                .map(List::of)
                .orElse(List.of());
    }

    @Override
    public boolean blocksPosition(Position position) {
        return nonViolentPiece.findPosition()
                .map(position::equals)
                .orElse(false);
    }

    @Override
    public boolean blocksCapture(Piece attacker, Position target) {
        // Non-violent piece cannot capture enemy pieces
        return attacker == nonViolentPiece;
    }

    @Override
    public void afterRemovingPieceHook(ChessBoard chessBoard, Piece piece) {
        // If the non-violent piece is somehow removed, remove the effect
        if (piece == nonViolentPiece) {
            chessBoard.removeEffect(this);
        }
    }
}
