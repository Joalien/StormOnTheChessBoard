package core;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static core.Position.*;

class PositionTest {

    @Test
    void should_generate_all_64_positions() {
        Set<Position> allPositions = Position.generateAllPositions();

        assertEquals(64, allPositions.size());
        assertTrue(allPositions.contains(a1));
        assertTrue(allPositions.contains(a8));
        assertTrue(allPositions.contains(h1));
        assertTrue(allPositions.contains(h8));
    }

    @Test
    void should_be_border() {
        Set<Position> a = Set.of(a1, a2, a3, a4, a5, a6, a7, a8);
        Set<Position> h = Set.of(h1, h2, h3, h4, h5, h6, h7, h8);
        Set<Position> one = Set.of(a1, b1, c1, d1, e1, f1, g1, h1);
        Set<Position> height = Set.of(a8, b8, c8, d8, e8, f8, g8, h8);
        Set<Position> borders = Stream.of(a.stream(), h.stream(), one.stream(), height.stream())
                .flatMap(x -> x)
                .collect(Collectors.toSet());

        assertTrue(Position.generateAllPositions().stream()
                .allMatch(pos -> pos.isBorder() == borders.contains(pos)));
    }

    @Test
    void should_not_have_position_between_c2() {
        Set<Position> noPositionBetween = Set.of(b1, b2, b3, c1, c2, c3, d1, d2, d3);
        assertTrue(Position.generateAllPositions().stream()
                .allMatch(s -> s.hasNoPositionBetween(c2) == noPositionBetween.contains(s)));
    }

    @Test
    void should_not_have_position_between_corner() {
        Set<Position> noPositionBetween = Set.of(g8, h8, g7, h7);
        assertTrue(Position.generateAllPositions().stream()
                .allMatch(s -> s.hasNoPositionBetween(h8) == noPositionBetween.contains(s)));
    }

    @Nested
    class MapPositionAndSquare {
        @Test
        void should_convert_position_to_square() {
            assertEquals(e4, Position.posToSquare(5, 4));
        }

        @Test
        void should_convert_position_to_square_bis() {
            assertEquals(a8, Position.posToSquare(1, 8));
        }

        @Test
        void should_convert_position_to_square_ter() {
            assertEquals(h1, Position.posToSquare(8, 1));
        }

        @ParameterizedTest
        @ValueSource(strings = {"e1", "e2", "e7", "e8"})
        public void should_get_file_number_from_square(Position square) {
            assertEquals(File.E, square.getFile());
        }

        @ParameterizedTest
        @ValueSource(strings = {"a8", "b8", "d8", "h8"})
        public void should_get_row_number_from_square(Position square) {
            assertEquals(Row.Height, square.getRow());
        }
    }

    @Nested
    class OutsideChessBoard {
        @Test
        void should_throw_when_position_is_before_chessboard() {
            assertThrows(IndexOutOfBoundsException.class, () -> Position.posToSquare(0, 1));
        }

        @Test
        void should_throw_when_position_is_before_chessboard_bis() {
            assertThrows(IndexOutOfBoundsException.class, () -> Position.posToSquare(1, 0));
        }

        @Test
        void should_throw_when_position_is_after_chessboard() {
            assertThrows(IndexOutOfBoundsException.class, () -> Position.posToSquare(5, 9));
        }

        @Test
        void should_throw_when_position_is_after_chessboard_bis() {
            assertThrows(IndexOutOfBoundsException.class, () -> Position.posToSquare(9, 1));
        }
    }
}