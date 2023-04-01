package player;

import card.SCCard;
import piece.Color;

import java.util.Set;

public class Player {

    private String name;
    private Color color;
    private Set<SCCard> cards;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }
}
