package fr.kubys.card.params;

import fr.kubys.card.QuadrilleCard;
import fr.kubys.card.params.CardParam;

public record QuadrilleCardParam(QuadrilleCard.Direction direction) implements CardParam {
}
