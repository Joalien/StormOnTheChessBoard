package card;

import board.ChessBoard;
import effet.BombingEffect;
import piece.Piece;
import position.Position;

import java.util.List;

public class BombingCard extends Card {

    private Position position;


    public BombingCard() {
        super("Attentat",
                "En jouant cette carte, notez les coordonnées d'une case de l'échiquier, vide ou occupée par l'une de vos pices. Dès qu'une pièce adverse s'arrrête sur cette case, une bombe explose et la pièce est retirée du jeu. S'il s'agit du roi, la bombe explose mais le roi reste en place",
                CardType.BEFORE_TURN); // FIXME replace with AFTER_TURN once you'll code an other card BEFORE_TURN
    }

    @Override
    protected void setupParams(List<?> params) {
        this.position = (Position) params.get(0);
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (position == null) throw new IllegalStateException();
        if (isPlayedBy == null) throw new IllegalStateException();
        boolean thereIsEnemyPieceOnPosition = chessBoard.at(position).getPiece()
                .map(Piece::getColor)
                .map(c -> c != isPlayedBy)
                .orElse(false);
        if (thereIsEnemyPieceOnPosition)
            throw new IllegalArgumentException("You cannot select a square occupied by an enemy piece");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard) {
        return true;
    }

    @Override
    protected boolean doAction(ChessBoard chessBoard) {
        chessBoard.addEffect(new BombingEffect(position, isPlayedBy));
        return true;
    }
}
