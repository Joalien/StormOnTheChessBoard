package board;

import lombok.extern.slf4j.Slf4j;
import piece.Color;
import piece.Piece;
import piece.Square;

import java.util.Optional;
import java.util.Set;

@Slf4j
public class BlackHole extends Piece {
    public BlackHole() {
        super(null, 'H');
    }

    @Override
    public void setSquare(Square square) {
        if (this.getSquare().isPresent()) log.warn("You are updating the position of a black hole!");
        super.setSquare(square);
    }

    @Override
    public boolean reachableSquares(int x, int y, Optional<Color> color) {
        return false;
    }

    @Override
    public Set<String> squaresOnThePath(String squareToMoveOn) {
        throw new BlackHoleException();
    }
}
