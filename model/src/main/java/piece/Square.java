package piece;

import lombok.ToString;
import core.Position;

import java.util.Optional;

@ToString
public class Square {

    private final Position position;
    private Piece piece;

    public Square(Position position) {
        this.position = position;
    }

    public Optional<Piece> getPiece() {
        return Optional.ofNullable(piece);
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Piece removePiece() {
        if (this.piece == null)
            throw new IllegalStateException("You can not remove piece from a square that does not contain piece");
        Piece p = this.piece;
        p.setSquare(null);
        this.piece = null;
        return p;
    }

    public Position getPosition() {
        return position;
    }

}
