import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoardUtilTest {

    @Nested
    class MapPositionAndSquare {
        @Test
        void should_convert_position_to_square() {
            assertEquals("e4", BoardUtil.posToSquare(5, 4));
        }

        @Test
        void should_convert_position_to_square_bis() {
            assertEquals("a8", BoardUtil.posToSquare(1, 8));
        }

        @Test
        void should_convert_position_to_square_ter() {
            assertEquals("h1", BoardUtil.posToSquare(8, 1));
        }

        @ParameterizedTest
        @ValueSource(strings = {"e1", "e2", "e7", "e8"})
        public void should_get_column_number_from_square(String square) {
            assertEquals(5, BoardUtil.getX(square));
        }

        @ParameterizedTest
        @ValueSource(strings = {"a8", "b8", "d8", "H8"})
        public void should_get_row_number_from_square(String square) {
            assertEquals(8, BoardUtil.getY(square));
        }
    }

    @Nested
    class OutsideChessBoard {
        @Test
        void should_throw_when_position_is_before_chessboard() {
            assertThrows(IndexOutOfBoundsException.class, () -> BoardUtil.posToSquare(0, 1));
        }

        @Test
        void should_throw_when_position_is_before_chessboard_bis() {
            assertThrows(IndexOutOfBoundsException.class, () -> BoardUtil.posToSquare(1, 0));
        }

        @Test
        void should_throw_when_position_is_after_chessboard() {
            assertThrows(IndexOutOfBoundsException.class, () -> BoardUtil.posToSquare(5, 9));
        }

        @Test
        void should_throw_when_position_is_after_chessboard_bis() {
            assertThrows(IndexOutOfBoundsException.class, () -> BoardUtil.posToSquare(9, 1));
        }
    }
}