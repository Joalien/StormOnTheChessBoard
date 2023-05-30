package fr.kubys.card.params;

import fr.kubys.card.params.CardParam;
import fr.kubys.piece.Pawn;

public record LightweightSquadCardParam(Pawn pawn1, Pawn pawn2) implements CardParam {
}
