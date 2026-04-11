package fr.kubys.game;

import fr.kubys.card.Card;
import fr.kubys.card.CardRegistry;
import fr.kubys.card.params.CardParam;
import fr.kubys.player.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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

    List<Card<? extends CardParam>> getStack() {
        return stack;
    }

    List<Card<? extends CardParam>> getDiscard() {
        return discard;
    }
}
