package fr.kubys.player;

import fr.kubys.card.Card;
import fr.kubys.core.Color;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Player {

    private final String name;
    private final Color color;
    private final List<Card> cards;

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

    public List<Card> getCards() {
        return this.cards;
    }
}
