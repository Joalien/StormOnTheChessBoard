package fr.kubys.piece;

import fr.kubys.board.FakePieceDecorator;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PieceCloneTest {

    @Test
    void default_clone_deep_copies_standard_pieces() {
        Stream.<Piece>of(
                new Bishop(Color.WHITE),
                new Queen(Color.BLACK),
                new Rock(Color.WHITE),
                new Knight(Color.BLACK),
                new Pawn(Color.WHITE)
        ).forEach(original -> {
            original.setPosition(Position.d4);
            Piece copy = original.clone();

            assertNotSame(original, copy, original.getClass().getSimpleName());
            assertEquals(original.getClass(), copy.getClass());
            assertEquals(original.getColor(), copy.getColor());
            assertEquals(Position.d4, copy.getPosition());
        });
    }

    @Test
    void default_clone_preserves_castle_flag() {
        King king = new King(Color.WHITE);
        king.setPosition(Position.e1);
        assertTrue(king.canCastle(), "king should be able to castle initially");

        king.cannotCastleAnymore();
        Piece copy = king.clone();

        assertFalse(copy.canCastle());
        assertTrue(copy.isKing());
    }

    @Test
    void default_clone_throws_clear_error_when_no_color_constructor() {
        // A synthetic subclass whose only constructor doesn't match (Color) —
        // exercises the error path without depending on a production class.
        Piece oddball = new OddballPiece("anything");

        IllegalStateException ex = assertThrows(IllegalStateException.class, oddball::clone);
        assertTrue(ex.getMessage().contains("must override clone()"), ex.getMessage());
        assertTrue(ex.getMessage().contains("OddballPiece"), ex.getMessage());
    }

    @Test
    void fake_piece_decorator_refuses_to_be_cloned() {
        FakePieceDecorator fake = new FakePieceDecorator(new Knight(Color.WHITE), Position.e4);
        assertThrows(UnsupportedOperationException.class, fake::clone);
    }

    // Helper class used by the "no Color constructor" test.
    static final class OddballPiece extends Piece {
        public OddballPiece(String ignored) {
            super(Color.WHITE);
        }

        @Override
        public boolean isPositionTheoreticallyReachable(fr.kubys.core.File file, fr.kubys.core.Row row, Color color) {
            return false;
        }

        @Override
        public java.util.Set<Position> squaresOnThePath(Position squareToMoveOn) {
            return java.util.Set.of();
        }
    }
}
