package board;

import org.junit.jupiter.api.Test;
import core.Color;
import piece.Knight;
import piece.Square;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static core.Position.*;

class FakePieceDecoratorTest {
    @Test
    void should_apply_isPositionTheoreticallyReachable_on_fake_piece() {
        Knight knight = new Knight(Color.WHITE);
        knight.setSquare(new Square(a8));

        FakePieceDecorator fakePiece = new FakePieceDecorator(knight, new Square(h1));

        assertTrue(fakePiece.isPositionTheoreticallyReachable(f2));
    }

    @Test
    void should_not_move_original_piece() {
        Knight knight = new Knight(Color.WHITE);
        knight.setSquare(new Square(a8));

        new FakePieceDecorator(knight, new Square(h1));

        assertEquals(a8, knight.getPosition());
    }
}