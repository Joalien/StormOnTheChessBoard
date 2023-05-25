package fr.kubys.game;

import fr.kubys.card.*;
import fr.kubys.game.GameStateController;
import fr.kubys.game.StateEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import fr.kubys.piece.Pawn;
import fr.kubys.game.exception.AlreadyMovedException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static fr.kubys.core.Position.*;

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
        gameStateController.getCurrentPlayer().getCards().add(beforeMoveCard);
        gameStateController.getCurrentPlayer().getCards().add(replaceMoveCard);
        gameStateController.getCurrentPlayer().getCards().add(afterMoveCard);

    }

    @Test
    void should_not_be_able_to_play_move() {
        assertThrows(AlreadyMovedException.class, () -> gameStateController.tryToMove(e2, e4));

        assertEquals(StateEnum.MOVE_WITHOUT_CARD_PLAYED, gameStateController.getCurrentState());
    }

    @Test
    void should_be_able_to_pass() {
        assertDoesNotThrow(() -> gameStateController.tryToPass());

        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, gameStateController.getCurrentState());
    }

    @Nested
    class PlayCard {
        @Test
        void should_be_able_to_play_valid_after_move_card() {
            assertDoesNotThrow(() -> gameStateController.tryToPlayCard(afterMoveCard, List.of(QuadrilleCard.Direction.CLOCKWISE)));

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
            BlackHoleCard blackHoleCard = new BlackHoleCard();
            gameStateController.getCurrentPlayer().getCards().add(blackHoleCard);

            assertThrows(IllegalArgumentException.class, () -> gameStateController.tryToPlayCard(blackHoleCard, List.of(h8)));

            assertEquals(StateEnum.MOVE_WITHOUT_CARD_PLAYED, gameStateController.getCurrentState());
        }
    }
}