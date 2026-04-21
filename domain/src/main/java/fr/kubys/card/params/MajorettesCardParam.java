package fr.kubys.card.params;

import fr.kubys.card.MajorettesCard;
import fr.kubys.piece.Piece;

public record MajorettesCardParam(
        Piece piece1,
        MajorettesCard.Direction direction1,
        Piece piece2,
        MajorettesCard.Direction direction2
) implements CardParam {
}
