package fr.kubys.piece.extra;

import fr.kubys.core.Position;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Square;

import java.util.Optional;

public class BlackHoleSquare extends Square {
    private final BlackHolePiece blackHolePiece = new BlackHolePiece();
    public BlackHoleSquare(Position position) {
        super(position);
        blackHolePiece.setPosition(position);
    }

    public Optional<Piece> getPiece() {
        return Optional.of(blackHolePiece);
    }

    public void setPiece(Piece piece) {
        throw new BlackHoleException("You cannot move a piece on a black hole!");
    }

    public Piece removePiece() {
        throw new BlackHoleException("You cannot move a black hole!");
    }

    public Position getPosition() {
        return super.getPosition();
    }
    public static class BlackHoleException extends RuntimeException {
        public BlackHoleException(String message) {
            super(message);
        }
    }
}
