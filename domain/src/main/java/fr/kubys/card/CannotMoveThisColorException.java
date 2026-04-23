package fr.kubys.card;

import fr.kubys.core.Color;

public class CannotMoveThisColorException extends IllegalStateException {
    public CannotMoveThisColorException(Color color) {
        super("Vous ne pouvez pas déplacer une pièce %s".formatted(switch (color) {
            case WHITE -> "blanche";
            case BLACK -> "noire";
            case NONE -> "neutre";
        }));
    }
}
