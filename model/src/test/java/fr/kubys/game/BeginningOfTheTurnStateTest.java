package fr.kubys.game;

import fr.kubys.board.IllegalMoveException;
import fr.kubys.card.BombingCard;
import fr.kubys.card.Card;
import fr.kubys.card.LightweightSquadCard;
import fr.kubys.card.QuadrilleCard;
import fr.kubys.game.GameStateController;
import fr.kubys.game.StateEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import fr.kubys.core.Color;
import fr.kubys.piece.Pawn;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static fr.kubys.core.Position.*;

class BeginningOfTheTurnStateTest {

    private Card beforeMoveCard;
    private Card replaceMoveCard;
    private Card afterMoveCard;
    private GameStateController gameStateController;

    @BeforeEach
    void setUp() {
        gameStateController = new GameStateController();
        gameStateController.startGame();
        gameStateController.setCurrentState(StateEnum.BEGINNING_OF_THE_TURN);
        beforeMoveCard = new BombingCard();
        replaceMoveCard = new LightweightSquadCard();
        afterMoveCard = new QuadrilleCard();
        gameStateController.getCurrentPlayer().getCards().add(beforeMoveCard);
        gameStateController.getCurrentPlayer().getCards().add(replaceMoveCard);
        gameStateController.getCurrentPlayer().getCards().add(afterMoveCard);
    }

    @Test
    void should_be_able_to_play_valid_move() {
        assertDoesNotThrow(() -> gameStateController.tryToMove(e2, e4));

        assertEquals(StateEnum.MOVE_WITHOUT_CARD_PLAYED, gameStateController.getCurrentState());
    }

    @Test
    void should_not_be_able_to_play_invalid_move() {
        assertThrows(IllegalMoveException.class, () -> gameStateController.tryToMove(e2, e5));

        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, gameStateController.getCurrentState());
    }

    @Test
    void should_not_be_able_to_pass() {
        assertThrows(IllegalStateException.class, () -> gameStateController.tryToPass());

        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, gameStateController.getCurrentState());
    }

    @Nested
    class PlayCard {
        @Test
        void should_be_able_to_play_valid_before_move_card() {
            assertDoesNotThrow(() -> gameStateController.tryToPlayCard(beforeMoveCard, List.of(e4)));

            assertEquals(StateEnum.BEFORE_MOVE, gameStateController.getCurrentState());
        }

        @Test
        void should_be_able_to_play_valid_replace_move_card() {
            Pawn e2Pawn = (Pawn) gameStateController.getChessBoard().at(e2).getPiece().get();
            Pawn d2Pawn = (Pawn) gameStateController.getChessBoard().at(d2).getPiece().get();

            assertDoesNotThrow(() -> gameStateController.tryToPlayCard(replaceMoveCard, List.of(e2Pawn, d2Pawn)));

            assertEquals(StateEnum.END_OF_THE_TURN, gameStateController.getCurrentState());
        }

        @Test
        void should_not_be_able_to_play_after_move_card() {
            assertThrows(IllegalStateException.class, () -> gameStateController.tryToPlayCard(afterMoveCard, List.of(QuadrilleCard.Direction.CLOCKWISE)));

            assertEquals(StateEnum.BEGINNING_OF_THE_TURN, gameStateController.getCurrentState());
            // assert card effect has not been done
            assertEquals(Color.BLACK, gameStateController.getChessBoard().at(a8).getPiece().get().getColor());
            assertEquals(Color.BLACK, gameStateController.getChessBoard().at(h8).getPiece().get().getColor());
            assertEquals(Color.WHITE, gameStateController.getChessBoard().at(a1).getPiece().get().getColor());
            assertEquals(Color.WHITE, gameStateController.getChessBoard().at(h1).getPiece().get().getColor());
        }

        @Test
        void should_not_be_able_to_play_invalid_card() {
            assertThrows(IllegalArgumentException.class, () -> gameStateController.tryToPlayCard(beforeMoveCard, List.of(h8)));

            assertEquals(StateEnum.BEGINNING_OF_THE_TURN, gameStateController.getCurrentState());
        }
    }
}