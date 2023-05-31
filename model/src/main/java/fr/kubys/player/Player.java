package fr.kubys.player;

import fr.kubys.card.Card;
import fr.kubys.card.params.CardParam;
import fr.kubys.core.Color;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final String name;
    private final Color color;
    private final List<Card<? extends CardParam>> cards;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.cards = new ArrayList<>();
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return this.name;
    }

    public Color getColor() {
        return this.color;
    }

    public List<Card<? extends CardParam>> getCards() {
        return this.cards;
    }
}
