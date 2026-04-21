package fr.kubys.game;

import fr.kubys.core.Color;
import fr.kubys.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CardDeckTest {

    private CardDeck deck;
    private Player player;

    @BeforeEach
    void setUp() {
        deck = new CardDeck(new Random().nextLong());
        player = new Player("Test", Color.WHITE);
    }

    @Test
    void should_redeal_5_new_cards_after_shuffle() {
        // Deal 5 cards to player
        for (int i = 0; i < 5; i++) deck.dealCard(player);
        assertEquals(5, player.getCards().size());

        var oldCards = new ArrayList<>(player.getCards());
        int totalCards = deck.getStack().size() + deck.getDiscard().size() + player.getCards().size();

        deck.shuffleAndRedeal(player, 5);

        assertEquals(5, player.getCards().size());
        assertTrue(deck.getDiscard().isEmpty());
        assertEquals(totalCards - 5, deck.getStack().size());
    }

    @Test
    void should_put_old_hand_back_in_pool() {
        for (int i = 0; i < 5; i++) deck.dealCard(player);
        var oldCards = new ArrayList<>(player.getCards());
        int stackBefore = deck.getStack().size();

        deck.shuffleAndRedeal(player, 5);

        // Old hand (5) went to pool, then 5 drawn = net stack change is 0
        // But old cards should not be in hand anymore (with very high probability)
        // The total card count should be preserved
        int totalAfter = deck.getStack().size() + deck.getDiscard().size() + player.getCards().size();
        int totalBefore = stackBefore + 5; // stack + hand
        assertEquals(totalBefore, totalAfter);
    }

    @Test
    void should_work_with_empty_hand() {
        deck.shuffleAndRedeal(player, 5);

        assertEquals(5, player.getCards().size());
        assertTrue(deck.getDiscard().isEmpty());
    }

    @Test
    void should_include_discard_pile_in_shuffle() {
        for (int i = 0; i < 5; i++) deck.dealCard(player);
        // Simulate playing 3 cards (remove from hand, then discardAndDraw)
        for (int i = 0; i < 3; i++) {
            var card = player.getCards().getFirst();
            player.getCards().remove(card);
            deck.discardAndDraw(card, player);
        }
        assertEquals(5, player.getCards().size());
        assertEquals(3, deck.getDiscard().size());

        deck.shuffleAndRedeal(player, 5);

        assertTrue(deck.getDiscard().isEmpty());
        assertEquals(5, player.getCards().size());
    }

    @Test
    void should_redeal_deterministically_for_a_given_seed() {
        long seed = 42L;
        CardDeck deck1 = new CardDeck(seed);
        CardDeck deck2 = new CardDeck(seed);
        Player player1 = new Player("P1", Color.WHITE);
        Player player2 = new Player("P2", Color.WHITE);
        for (int i = 0; i < 5; i++) deck1.dealCard(player1);
        for (int i = 0; i < 5; i++) deck2.dealCard(player2);

        deck1.shuffleAndRedeal(player1, 5);
        deck2.shuffleAndRedeal(player2, 5);

        assertEquals(player1.getCards(), player2.getCards(),
                "Two decks with the same seed must redeal the same hand — required so that command replays are deterministic");
    }
}
