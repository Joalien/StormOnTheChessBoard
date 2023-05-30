package fr.kubys.card.params;

import fr.kubys.card.params.CardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Bishop;

public record ReflectedBishopCardParam(Bishop bishop, Position positionToMoveOn) implements CardParam {
}
