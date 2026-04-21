package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.ManHoleEffect;
import fr.kubys.card.params.ManHoleCardParam;
import fr.kubys.piece.Piece;

import java.util.Optional;

public class ManHoleCard extends Card<ManHoleCardParam> implements Effectable<ManHoleEffect> {

    public ManHoleCard() {
        super("Bouche d'égout", "Placez des bouches d'égout sur deux cases de l'échiquier. Une pièce se trouvant sur une bouche d'égout peut désormais se rendre sur n'importe quelle autre bouche d'égout, occupée ou non", CardType.AFTER_TURN, ManHoleCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, ManHoleCardParam param) {
        if (param.position1() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (param.position2() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (param.position1().equals(param.position2()))
            throw new IllegalArgumentException("Vous devez sélectionner deux positions différentes");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, ManHoleCardParam param) {
        chessBoard.addEffect(new ManHoleEffect(param.position1(), param.position2()));
    }
}
