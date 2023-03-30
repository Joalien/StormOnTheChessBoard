package piece;

import lombok.ToString;

import java.util.Optional;

@ToString
public class Square {

    private final String position;
    private Piece piece;

    public Square(String position) {
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

    public String getPosition() {
        return position;
    }

}
