package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.card.KangarooCard;
import fr.kubys.card.LightweightSquadCard;
import fr.kubys.card.params.KangarooCardParam;
import fr.kubys.card.params.LightweightSquadCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.Pawn;
import fr.kubys.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(4, gameStateController.getWhite().getCards().size());
        assertEquals(4, gameStateController.getBlack().getCards().size());
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

        assertDoesNotThrow(() -> gameStateController.dealCard(player));

        assertTrue(gameStateController.getDiscard().isEmpty());
        assertEquals(1, gameStateController.getStack().size());
        assertEquals(1, gameStateController.getCurrentPlayer().getCards().size());
    }

    private void add_2_cards_in_discard() {
        Card<LightweightSquadCardParam> card1 = new LightweightSquadCard();
        Card<KangarooCardParam> card2 = new KangarooCard();
        gameStateController.getDiscard().add(card1);
        gameStateController.getDiscard().add(card2);
    }
}