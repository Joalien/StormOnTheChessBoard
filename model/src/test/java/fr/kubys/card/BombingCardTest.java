package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.BombingCard;
import fr.kubys.card.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import fr.kubys.core.Color;
import fr.kubys.piece.Queen;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static fr.kubys.core.Position.e4;

class BombingCardTest {

    private ChessBoard chessBoard;
    private Card bombing;

    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createEmpty();
        bombing = new BombingCard();
        bombing.setIsPlayedBy(Color.WHITE);
    }

    @Nested
    class Success {
        @Test
        void should_work_if_empty_square() {
            assertTrue(chessBoard.getEffects().isEmpty());

            assertDoesNotThrow(() -> bombing.playOn(chessBoard, Collections.singletonList(e4)));

            assertEquals(1, chessBoard.getEffects().size());
        }

        @Test
        void should_work_if_played_on_ally_piece() {
            chessBoard.add(new Queen(Color.WHITE), e4);

            assertDoesNotThrow(() -> bombing.playOn(chessBoard, Collections.singletonList(e4)));
        }
    }

    @Nested
    class Failure {
        @Test
        void should_throw_if_position_occupied_by_enemy_piece() {
            Queen queen = new Queen(Color.BLACK);
            chessBoard.add(queen, e4);

            assertThrows(IllegalArgumentException.class, () -> bombing.playOn(chessBoard, Collections.singletonList(e4)));

            assertEquals(queen, chessBoard.at(e4).getPiece().get());
        }
    }
}