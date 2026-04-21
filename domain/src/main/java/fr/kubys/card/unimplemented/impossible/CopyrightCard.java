package fr.kubys.card.unimplemented.impossible;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class CopyrightCard extends Card<NoCardParam> {

    public CopyrightCard() {
        super("Droits d'Auteur",
                "À partir du moment où cette carte est jouée, tout joueur faisant une remarque désobligeante au sujet du jeu Tempête sur l'échiquier perd immédiatement un pion, choisi par son adversaire.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Droits d'Auteur is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Droits d'Auteur is not yet implemented");
    }
}
