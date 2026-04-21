package fr.kubys.card.unimplemented.impossible;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class InterferenceCard extends Card<NoCardParam> {

    public InterferenceCard() {
        super("Ingérence",
                "Demandez à la tierce personne la plus proche de l'échiquier de déplacer deux pièces de son choix, sauf les Rois, une de chaque joueur.",
                CardType.BEFORE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Ingérence is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Ingérence is not yet implemented");
    }
}
