package fr.kubys.card;

import fr.kubys.card.params.CardParam;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Rock;

public record StableCardParam(Rock rock, Knight knight) implements CardParam {
}
