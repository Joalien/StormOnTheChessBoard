package position;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PositionUtilTest {

    @Test
    void should_generate_all_64_positions() {
        List<String> allPositions = PositionUtil.generateAllPositions();

        assertEquals(64, allPositions.size());
        assertTrue(allPositions.contains("a1"));
        assertTrue(allPositions.contains("a8"));
        assertTrue(allPositions.contains("h1"));
        assertTrue(allPositions.contains("h8"));

        assertFalse(allPositions.contains("a0"));
        assertFalse(allPositions.contains("a9"));
        assertFalse(allPositions.contains("i1"));
        assertFalse(allPositions.contains("z1"));
    }

    @Nested
    class MapPositionAndSquare {
        @Test
        void should_convert_position_to_square() {
            assertEquals("e4", PositionUtil.posToSquare(5, 4));
        }

        @Test
        void should_convert_position_to_square_bis() {
            assertEquals("a8", PositionUtil.posToSquare(1, 8));
        }

        @Test
        void should_convert_position_to_square_ter() {
            assertEquals("h1", PositionUtil.posToSquare(8, 1));
        }

        @ParameterizedTest
        @ValueSource(strings = {"e1", "e2", "e7", "e8"})
        public void should_get_file_number_from_square(String square) {
            assertEquals(5, PositionUtil.getX(square));
        }

        @ParameterizedTest
        @ValueSource(strings = {"a8", "b8", "d8", "H8"})
        public void should_get_row_number_from_square(String square) {
            assertEquals(8, PositionUtil.getY(square));
        }
    }

    @Nested
    class OutsideChessBoard {
        @Test
        void should_throw_when_position_is_before_chessboard() {
            assertThrows(IndexOutOfBoundsException.class, () -> PositionUtil.posToSquare(0, 1));
        }

        @Test
        void should_throw_when_position_is_before_chessboard_bis() {
            assertThrows(IndexOutOfBoundsException.class, () -> PositionUtil.posToSquare(1, 0));
        }

        @Test
        void should_throw_when_position_is_after_chessboard() {
            assertThrows(IndexOutOfBoundsException.class, () -> PositionUtil.posToSquare(5, 9));
        }

        @Test
        void should_throw_when_position_is_after_chessboard_bis() {
            assertThrows(IndexOutOfBoundsException.class, () -> PositionUtil.posToSquare(9, 1));
        }

        @Test
        void should_throw_if_outside_of_board_position() {
            assertThrows(IndexOutOfBoundsException.class, () -> PositionUtil.getX("i2"));
        }
    }
}