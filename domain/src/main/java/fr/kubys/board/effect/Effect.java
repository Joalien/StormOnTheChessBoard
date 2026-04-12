package fr.kubys.board.effect;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class Effect {

    private final String name;
    private final List<Position> positions = new ArrayList<>();

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

    public boolean blocksPosition(Position position) {
        return false;
    }

    public boolean blocksPath(Position from, Position to, Set<Position> intermediates) {
        return intermediates.stream().anyMatch(this::blocksPosition);
    }

    public String getName() {
        return this.name;
    }

    public List<Position> getPositions() {
        return positions;
    }
}
