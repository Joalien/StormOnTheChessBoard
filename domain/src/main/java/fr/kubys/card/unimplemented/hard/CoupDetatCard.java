package fr.kubys.card.unimplemented.hard;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.NoCardParam;

public class CoupDetatCard extends Card<NoCardParam> {

    public CoupDetatCard() {
        super("Coup d'État",
                "Votre Roi devient un simple Prince. Il continue à se déplacer comme auparavant, mais n'est désormais qu'une pièce ordinaire, pouvant être prise. Une autre de vos pièces (Pion, Fou ou Cavalier), qui conserve également toutes ses capacités de développement, s'empare du trône. C'est cet usurpateur que votre adversaire devra mater.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Coup d'État is not yet implemented");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Coup d'État is not yet implemented");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Coup d'État is not yet implemented");
    }
}
