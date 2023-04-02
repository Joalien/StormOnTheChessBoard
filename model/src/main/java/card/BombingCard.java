package card;

import effet.BombingEffect;
import board.ChessBoard;
import piece.Color;
import piece.Piece;

public class BombingCard extends SCCard {

    private final String position;
    private final Color colorThatShouldExplode;


    public BombingCard(String position, Color colorThatShouldExplode) {
        super("Attentat",
                "En jouant cette carte, notez les coordonnées d'une case de l'échiquier, vide ou occupée par l'une de vos pices. Dès qu'une pièce adverse s'arrrête sur cette case, une bombe explose et la pièce est retirée du jeu. S'il s'agit du roi, la bombe explose mais le roi reste en place",
                SCType.BEFORE_TURN); // FIXME replace with AFTER_TURN once you'll code an other card BEFORE_TURN
        this.position = position;
        this.colorThatShouldExplode = colorThatShouldExplode;
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (position == null) throw new IllegalStateException();
        if (colorThatShouldExplode == null) throw new IllegalStateException();
        boolean thereIsEnemyPieceOnPosition = chessBoard.at(position).getPiece()
                .map(Piece::getColor)
                .map(c -> c == colorThatShouldExplode)
                .orElse(false);
        if (thereIsEnemyPieceOnPosition) throw new IllegalArgumentException("You cannot select a square occupied by an enemy piece");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard) {
        return true;
    }

    @Override
    protected boolean doAction(ChessBoard chessBoard) {
        chessBoard.addEffect(new BombingEffect(position, colorThatShouldExplode));
        return true;
    }
}
