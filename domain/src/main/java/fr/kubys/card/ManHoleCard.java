package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.ManHoleEffect;
import fr.kubys.card.params.ManHoleCardParam;
import fr.kubys.piece.Piece;

import java.util.Optional;

public class ManHoleCard extends Card<ManHoleCardParam> {

    public ManHoleCard() {
        super("Bouche d'égout", "Placez des bouches d'égout sur deux cases de l'échiquier. Une pièce se trouvant sur une bouche d'égout peut désormais se rendre sur n'importe quelle autre bouche d'égout, occupée ou non", CardType.AFTER_TURN, ManHoleCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, ManHoleCardParam param) {
        if (param.position1() == null) throw new IllegalStateException();
        if (param.position2() == null) throw new IllegalStateException();
        if (param.position1().equals(param.position2()))
            throw new IllegalArgumentException("You should select two different positions");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, ManHoleCardParam param) {
        Optional<Piece> piece1 = chessBoard.at(param.position1()).getPiece();
        Optional<Piece> piece2 = chessBoard.at(param.position2()).getPiece();
        return piece1.isEmpty() || piece2.isEmpty() ||
                piece1.get().getColor() == piece2.get().getColor() ||
                !piece1.get().isKing() || !piece2.get().isKing();
    }

    @Override
    protected void doAction(ChessBoard chessBoard, ManHoleCardParam param) {
        chessBoard.addEffect(new ManHoleEffect(param.position1(), param.position2()));
    }
}
