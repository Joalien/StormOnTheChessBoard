package fr.kubys.piece.extra;

import fr.kubys.core.Position;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Square;

import java.util.Optional;

public class BlackHoleSquare extends Square {
    public BlackHoleSquare(Position position) {
        super(position);
    }

    public Optional<Piece> getPiece() {
        throw new BlackHoleException("You cannot move a black hole!");
    }

    public void setPiece(Piece piece) {
        throw new BlackHoleException("You cannot move a black hole!");
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
