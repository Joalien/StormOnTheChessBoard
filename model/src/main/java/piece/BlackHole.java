package piece;

import lombok.extern.slf4j.Slf4j;

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
    public boolean isPositionTheoricallyReachable(int x, int y, Optional<Color> color) {
        return false;
    }

    @Override
    public Set<String> squaresOnThePath(String squareToMoveOn) {
        throw new BlackHoleException("You cannot move a black hole!");
    }

    public static class BlackHoleException extends RuntimeException {
        public BlackHoleException(String message) {
            super(message);
        }
    }
}
