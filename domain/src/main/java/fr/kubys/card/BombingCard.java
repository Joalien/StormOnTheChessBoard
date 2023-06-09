package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.BombingEffect;
import fr.kubys.card.params.PositionCardParam;
import fr.kubys.piece.Piece;

public class BombingCard extends Card<PositionCardParam> {

    public BombingCard() {
        super("Attentat",
                "En jouant cette carte, notez les coordonnées d'une case de l'échiquier, vide ou occupée par l'une de vos pices. Dès qu'une pièce adverse s'arrrête sur cette case, une bombe explose et la pièce est retirée du jeu. S'il s'agit du roi, la bombe explose mais le roi reste en place",
                CardType.BEFORE_TURN,
                PositionCardParam.class); // FIXME replace with AFTER_TURN once you'll code an other card BEFORE_TURN
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PositionCardParam param) {
        if (param.position() == null) throw new IllegalStateException();
        if (chessBoard.getCurrentTurn() == null) throw new IllegalStateException();
        boolean thereIsEnemyPieceOnPosition = chessBoard.at(param.position()).getPiece()
                .map(Piece::getColor)
                .map(c -> c != chessBoard.getCurrentTurn())
                .orElse(false);
        if (thereIsEnemyPieceOnPosition)
            throw new IllegalArgumentException("You cannot select a square occupied by an enemy piece");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PositionCardParam param) {
        return true;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PositionCardParam param) {
        chessBoard.addEffect(new BombingEffect(param.position(), chessBoard.getCurrentTurn()));
    }
}
