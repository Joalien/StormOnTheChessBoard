package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class AntiGangCard extends Card<NoCardParam> {

    public AntiGangCard() {
        super("Anti-Gang",
                "Jouez cette carte lorsque votre adversaire vous annonce un échec, même mat. Il doit alors remettre la pièce qu'il vient de jouer là où elle était avant, et ne peut rien jouer d'autre.",
                CardType.ENEMY_TURN_AFTER_MOVE,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Anti-Gang is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Anti-Gang is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Anti-Gang is not yet implemented");
    }
}
