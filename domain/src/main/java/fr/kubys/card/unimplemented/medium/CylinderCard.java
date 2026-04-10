package fr.kubys.card.unimplemented.medium;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class CylinderCard extends Card<NoCardParam> {

    public CylinderCard() {
        super("Cylindre",
                "Déplacez l'une de vos pièces en la faisant passer d'un côté à l'autre de l'échiquier, comme si ce dernier était cylindrique, et si les bords gauche et droit étaient contigus.",
                CardType.REPLACE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Cylindre is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Cylindre is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Cylindre is not yet implemented");
    }
}
