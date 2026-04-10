package fr.kubys.card.params;

import fr.kubys.core.Position;

public record BarricadeCardParam(Position from1, Position to1, Position from2, Position to2) implements CardParam {
}
