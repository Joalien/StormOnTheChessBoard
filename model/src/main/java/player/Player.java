package player;

import card.SCCard;
import lombok.Getter;
import piece.Color;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Player {

    private String name;
    private Color color;
    private Set<Class<? extends SCCard>> cards;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.cards = new HashSet<>();
    }
}
