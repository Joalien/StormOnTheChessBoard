package game;

import card.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import piece.Pawn;
import game.exception.AlreadyMovedException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static core.Position.*;

class MoveWithoutCardPlayedStateTest {

    private Card beforeMoveCard;
    private Card replaceMoveCard;
    private Card afterMoveCard;
    private GameStateController gameStateController;

    @BeforeEach
    void setUp() {
        gameStateController = new GameStateController();
        gameStateController.startGame();
        gameStateController.setCurrentState(StateEnum.MOVE_WITHOUT_CARD_PLAYED);
        beforeMoveCard = new BombingCard();
        replaceMoveCard = new LightweightSquadCard();
        afterMoveCard = new QuadrilleCard();
    }

    @Test
    void should_not_be_able_to_play_move() {
        assertThrows(AlreadyMovedException.class, () -> gameStateController.tryToMove(e2, e4));

        assertEquals(StateEnum.MOVE_WITHOUT_CARD_PLAYED, gameStateController.getCurrentState());
    }

    @Test
    void should_be_able_to_pass() {
        assertTrue(gameStateController.tryToPass());

        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, gameStateController.getCurrentState());
    }

    @Nested
    class PlayCard {
        @Test
        void should_be_able_to_play_valid_after_move_card() {
            assertTrue(gameStateController.tryToPlayCard(afterMoveCard, List.of(QuadrilleCard.Direction.CLOCKWISE)));

            assertEquals(StateEnum.END_OF_THE_TURN, gameStateController.getCurrentState());
        }

        @Test
        void should_not_be_able_to_play_replace_move_card() {
            assertThrows(IllegalStateException.class, () -> gameStateController.tryToPlayCard(replaceMoveCard, List.of((Pawn) gameStateController.getChessBoard().at(e2).getPiece().get(), (Pawn) gameStateController.getChessBoard().at(d2).getPiece().get())));

            assertEquals(StateEnum.MOVE_WITHOUT_CARD_PLAYED, gameStateController.getCurrentState());
        }

        @Test
        void should_not_be_able_to_play_before_move_card() {
            assertThrows(IllegalStateException.class, () -> gameStateController.tryToPlayCard(beforeMoveCard, List.of(e4)));

            assertEquals(StateEnum.MOVE_WITHOUT_CARD_PLAYED, gameStateController.getCurrentState());
        }

        @Test
        void should_not_be_able_to_play_invalid_card() {
            assertThrows(IllegalArgumentException.class, () -> gameStateController.tryToPlayCard(new BlackHoleCard(), List.of(h8)));

            assertEquals(StateEnum.MOVE_WITHOUT_CARD_PLAYED, gameStateController.getCurrentState());
        }
    }
}