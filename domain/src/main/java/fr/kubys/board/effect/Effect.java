package fr.kubys.board.effect;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.util.HashSet;
import java.util.Set;

public abstract class Effect {

    private final String name;
    private final Set<Position> positions = new HashSet<>();

    public Effect(String name) {
        this.name = name;
    }

    public void afterMoveHook(ChessBoard chessBoard, Piece piece) {
    }

    public void beforeMoveHook(ChessBoard chessBoard, Piece piece) {
    }

    public void afterRemovingPieceHook(ChessBoard chessBoard, Piece piece) {
    }

    public boolean allowToMove(Piece piece, Position positionToMoveOn) {
        return false;
    }

    public String getName() {
        return this.name;
    }

    public Set<Position> getPositions() {
        return positions;
    }
}
