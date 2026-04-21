package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.BarricadeEffect;
import fr.kubys.card.params.BarricadeCardParam;

public class BarricadeCard extends Card<BarricadeCardParam> implements Effectable<BarricadeEffect> {

    public BarricadeCard() {
        super("Barricade",
                "Placez une barrière de deux cases de long, rectiligne ou à angle droit, entre n'importe quelles cases de l'échiquier. Cette barrière ne peut être franchie par aucune pièce, excepté les cavaliers ou toute pièce ayant acquis la capacité de sauter d'une manière similaire.",
                CardType.AFTER_TURN, BarricadeCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, BarricadeCardParam param) {
        if (param.from1() == null || param.to1() == null || param.from2() == null || param.to2() == null)
            throw new IllegalStateException("Paramètre de carte manquant");
        if (!BarricadeEffect.areOrthogonallyAdjacent(param.from1(), param.to1()))
            throw new IllegalArgumentException("Côté 1 : les positions doivent être orthogonalement adjacentes");
        if (!BarricadeEffect.areOrthogonallyAdjacent(param.from2(), param.to2()))
            throw new IllegalArgumentException("Côté 2 : les positions doivent être orthogonalement adjacentes");
        if (!BarricadeEffect.areEdgesConnected(param.from1(), param.to1(), param.from2(), param.to2()))
            throw new IllegalArgumentException("Les deux côtés doivent être connectés (partager un sommet)");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, BarricadeCardParam param) {
        chessBoard.addEffect(new BarricadeEffect(param.from1(), param.to1(), param.from2(), param.to2()));
    }
}
