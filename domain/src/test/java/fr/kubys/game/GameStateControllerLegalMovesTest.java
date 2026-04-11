package fr.kubys.game;

import fr.kubys.core.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameStateControllerLegalMovesTest {

    private GameStateController controller;

    @BeforeEach
    void setUp() {
        controller = new GameStateController();
        controller.startGame(42);
    }

    @Test
    void pawn_on_e2_has_two_legal_moves_at_start() {
        Set<Position> moves = controller.getLegalMoves(e2);

        assertEquals(Set.of(e3, e4), moves);
    }

    @Test
    void knight_on_b1_has_two_legal_moves_at_start() {
        Set<Position> moves = controller.getLegalMoves(b1);

        assertEquals(Set.of(a3, c3), moves);
    }

    @Test
    void throws_when_no_piece_on_square() {
        assertThrows(IllegalArgumentException.class, () -> controller.getLegalMoves(e4));
    }
}
