package fr.kubys.card.params;

import fr.kubys.card.params.CardParam;
import fr.kubys.piece.Pawn;

import java.util.Set;

public record ChargeCardParam(Set<Pawn> pawns) implements CardParam {
}
