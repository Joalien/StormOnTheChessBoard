package fr.kubys.board;

import fr.kubys.core.Color;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Square;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FakePieceDecoratorTest {
    @Test
    void should_apply_isPositionTheoreticallyReachable_on_fake_piece() {
        Knight knight = new Knight(Color.WHITE);
        knight.setPosition(a8);

        FakePieceDecorator fakePiece = new FakePieceDecorator(knight, h1);

        assertTrue(fakePiece.isPositionTheoreticallyReachable(f2));
    }

    @Test
    void should_not_move_original_piece() {
        Knight knight = new Knight(Color.WHITE);
        knight.setPosition(a8);

        new FakePieceDecorator(knight, h1);

        assertEquals(a8, knight.getPosition());
    }
}