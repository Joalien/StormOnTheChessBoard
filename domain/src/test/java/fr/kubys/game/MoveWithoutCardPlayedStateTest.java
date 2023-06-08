package fr.kubys.game;

import fr.kubys.card.BlackHoleCard;
import fr.kubys.card.BombingCard;
import fr.kubys.card.LightweightSquadCard;
import fr.kubys.card.QuadrilleCard;
import fr.kubys.card.params.LightweightSquadCardParam;
import fr.kubys.card.params.PositionCardParam;
import fr.kubys.card.params.QuadrilleCardParam;
import fr.kubys.game.exception.AlreadyMovedException;
import fr.kubys.piece.Pawn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class MoveWithoutCardPlayedStateTest {

    private BombingCard beforeMoveCard;
    private LightweightSquadCard replaceMoveCard;
    private QuadrilleCard afterMoveCard;
    private GameStateController gameStateController;

    @BeforeEach
    void setUp() {
        gameStateController = new GameStateController();
        gameStateController.startGame(new Random().nextLong());
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
            assertDoesNotThrow(() -> gameStateController.tryToPlayCard(afterMoveCard, new QuadrilleCardParam(QuadrilleCard.Direction.CLOCKWISE)));

            assertEquals(StateEnum.END_OF_THE_TURN, gameStateController.getCurrentState());
        }

        @Test
        void should_not_be_able_to_play_replace_move_card() {
            assertThrows(IllegalStateException.class, () -> gameStateController.tryToPlayCard(replaceMoveCard, new LightweightSquadCardParam((Pawn) gameStateController.getChessBoard().at(e2).getPiece().get(), (Pawn) gameStateController.getChessBoard().at(d2).getPiece().get())));

            assertEquals(StateEnum.MOVE_WITHOUT_CARD_PLAYED, gameStateController.getCurrentState());
        }

        @Test
        void should_not_be_able_to_play_before_move_card() {
            assertThrows(IllegalStateException.class, () -> gameStateController.tryToPlayCard(beforeMoveCard, new PositionCardParam(e4)));

            assertEquals(StateEnum.MOVE_WITHOUT_CARD_PLAYED, gameStateController.getCurrentState());
        }

        @Test
        void should_not_be_able_to_play_invalid_card() {
            BlackHoleCard blackHoleCard = new BlackHoleCard();
            gameStateController.getCurrentPlayer().getCards().add(blackHoleCard);

            assertThrows(IllegalArgumentException.class, () -> gameStateController.tryToPlayCard(blackHoleCard, new PositionCardParam(h8)));

            assertEquals(StateEnum.MOVE_WITHOUT_CARD_PLAYED, gameStateController.getCurrentState());
        }
    }
}