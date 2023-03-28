import java.util.Optional;

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

    public void removePiece() {
        this.piece = null;
    }

    public String getPosition() {
        return position;
    }
}
