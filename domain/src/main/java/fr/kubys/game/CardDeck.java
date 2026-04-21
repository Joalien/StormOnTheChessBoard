package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.card.CardRegistry;
import fr.kubys.card.params.CardParam;
import fr.kubys.player.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class CardDeck {

    private final List<Card<? extends CardParam>> stack;
    private final List<Card<? extends CardParam>> discard;

    CardDeck(long seed) {
        this.stack = new LinkedList<>(CardRegistry.createAllCards());
        this.discard = new LinkedList<>();
//        Collections.shuffle(stack, new Random(seed));
    }

    void dealCard(Player player) {
        if (stack.isEmpty()) {
            stack.addAll(discard);
            discard.clear();
            Collections.shuffle(stack);
        }
        player.getCards().add(stack.remove(0));
    }

    void discardAndDraw(Card<? extends CardParam> card, Player player) {
        discard.add(card);
        dealCard(player);
    }

    void shuffleAndRedeal(Player player, int numberOfCards) {
        discard.addAll(player.getCards());
        player.getCards().clear();
        stack.addAll(discard);
        discard.clear();
        Collections.shuffle(stack);
        IntStream.range(0, numberOfCards).forEach(i -> dealCard(player));
    }

    List<Card<? extends CardParam>> getStack() {
        return stack;
    }

    List<Card<? extends CardParam>> getDiscard() {
        return discard;
    }
}
