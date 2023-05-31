package fr.kubys.card.params;

import fr.kubys.piece.Knight;
import fr.kubys.piece.Rock;

public record StableCardParam(Rock rock, Knight knight) implements CardParam {
}
