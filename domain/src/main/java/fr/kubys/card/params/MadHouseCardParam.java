package fr.kubys.card.params;

import fr.kubys.piece.Bishop;
import fr.kubys.piece.Rock;

public record MadHouseCardParam(Rock rock, Bishop bishop) implements CardParam {
}
