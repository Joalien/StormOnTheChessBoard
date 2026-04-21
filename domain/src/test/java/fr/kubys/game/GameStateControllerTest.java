package fr.kubys.game;

import fr.kubys.board.CheckException;
import fr.kubys.card.Card;
import fr.kubys.card.KangarooCard;
import fr.kubys.card.LightweightSquadCard;
import fr.kubys.card.params.KnightCardParam;
import fr.kubys.card.params.LightweightSquadCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.Pawn;
import fr.kubys.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

// Note: tests access getStack()/getDiscard() through the public API which delegates to CardDeck

class GameStateControllerTest {

    private GameStateController gameStateController;

    @BeforeEach
    void setUp() {
        gameStateController = new GameStateController();
        gameStateController.startGame(new Random().nextLong());
    }

    @Test
    void should_set_up_correctly() {
        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, gameStateController.getCurrentState());
        assertEquals(gameStateController.getWhite(), gameStateController.getCurrentPlayer());
        assertEquals(5, gameStateController.getWhite().getCards().size());
        assertEquals(5, gameStateController.getBlack().getCards().size());
        assertFalse(gameStateController.getStack().isEmpty());
        assertEquals(Color.WHITE, gameStateController.getWhite().getColor());
        assertEquals(Color.BLACK, gameStateController.getBlack().getColor());
    }

    @Test
    void should_discard_card_and_pick_new_one() {
        LightweightSquadCard card = new LightweightSquadCard();
        gameStateController.getCurrentPlayer().getCards().clear();
        gameStateController.getCurrentPlayer().getCards().add(card);
        gameStateController.getStack().remove(card);
        assertEquals(1, gameStateController.getCurrentPlayer().getCards().size());
        assertTrue(gameStateController.getCurrentPlayer().getCards().contains(card));

        assertDoesNotThrow(() -> gameStateController.tryToPlayCard(card, new LightweightSquadCardParam((Pawn) gameStateController.getChessBoard().at(e2).getPiece().get(), (Pawn) gameStateController.getChessBoard().at(d2).getPiece().get())));

        assertEquals(1, gameStateController.getCurrentPlayer().getCards().size());
        assertFalse(gameStateController.getCurrentPlayer().getCards().contains(card));
    }

    @Test
    void should_not_be_able_to_start_an_already_started_game() {
        gameStateController = new GameStateController();
        assertDoesNotThrow(() -> gameStateController.startGame(new Random().nextLong()));
        assertThrows(IllegalStateException.class, () -> gameStateController.startGame(new Random().nextLong()));
    }

    @Test
    void should_not_be_able_to_move_from_empty_square() {
        assertThrows(IllegalArgumentException.class, () -> gameStateController.tryToMove(e4, e5));
    }

    @Test
    void should_not_be_able_to_play_enemy_piece() {
        assertDoesNotThrow(() -> gameStateController.tryToMove(e2, e4));
        gameStateController.tryToPass();
        assertThrows(IllegalStateException.class, () -> gameStateController.tryToMove(d2, d4));
    }

    @Test
    void should_reshuffle_discard_when_deck_is_empty() {
        gameStateController.getStack().clear();
        add_2_cards_in_discard();
        Player player = gameStateController.getCurrentPlayer();
        player.getCards().clear();

        assertDoesNotThrow(() -> gameStateController.getDeck().dealCard(player));

        assertTrue(gameStateController.getDiscard().isEmpty());
        assertEquals(1, gameStateController.getStack().size());
        assertEquals(1, gameStateController.getCurrentPlayer().getCards().size());
    }

    @Test
    void should_shuffle_hand_at_beginning_of_turn() {
        var oldCards = new java.util.ArrayList<>(gameStateController.getCurrentPlayer().getCards());
        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, gameStateController.getCurrentState());

        assertDoesNotThrow(() -> gameStateController.shuffleHand());

        assertEquals(5, gameStateController.getCurrentPlayer().getCards().size());
        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, gameStateController.getCurrentState());
    }

    @Test
    void should_shuffle_hand_after_move() {
        gameStateController.tryToMove(e2, e4);
        assertEquals(StateEnum.MOVE_WITHOUT_CARD_PLAYED, gameStateController.getCurrentState());

        assertDoesNotThrow(() -> gameStateController.shuffleHand());

        assertEquals(5, gameStateController.getCurrentPlayer().getCards().size());
        assertEquals(StateEnum.MOVE_WITHOUT_CARD_PLAYED, gameStateController.getCurrentState());
    }

    @Test
    void should_allow_move_into_check() {
        // Move king's pawn to expose king — this was forbidden before, now allowed
        assertDoesNotThrow(() -> gameStateController.tryToMove(f2, f3));
    }

    @Test
    void should_not_pass_turn_while_in_check() {
        // Move pawn to allow diagonal attack on king
        gameStateController.tryToMove(f2, f3);
        gameStateController.tryToPass(); // end white turn
        gameStateController.tryToMove(e7, e5); // black moves
        gameStateController.tryToPass(); // end black turn
        gameStateController.tryToMove(g2, g4); // white plays fool's mate setup
        gameStateController.tryToPass(); // end white turn
        // Black queen delivers check
        gameStateController.tryToMove(d8, h4);
        // Black cannot pass while white is... wait, it's black's turn and black just checked white
        // Actually after black moves, black tries to pass — but white's king is checked, not black's
        // Let me reconsider: tryToPass checks getCurrentPlayer's king
        // After black moves Qh4, it's still black's turn. Black's king is not in check.
        // So black can pass. Then it's white's turn, white is in check.
        // White should not be able to pass.
        gameStateController.tryToPass(); // end black turn — OK, black is not in check
        // Now it's white's turn, white king is in check from Qh4
        assertTrue(gameStateController.isCurrentPlayerInCheck());
        assertThrows(CheckException.class, () -> gameStateController.tryToPass());
    }

    @Test
    void should_not_shuffle_if_game_not_started() {
        var controller = new GameStateController();
        assertThrows(IllegalStateException.class, controller::shuffleHand);
    }

    private void add_2_cards_in_discard() {
        Card<LightweightSquadCardParam> card1 = new LightweightSquadCard();
        Card<KnightCardParam> card2 = new KangarooCard();
        gameStateController.getDiscard().add(card1);
        gameStateController.getDiscard().add(card2);
    }
}