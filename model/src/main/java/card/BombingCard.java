package card;

import board.BombingEffect;
import board.ChessBoard;
import board.Effect;
import piece.BlackHole;
import piece.Color;
import piece.Piece;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BombingCard extends SCCard {

    private final String position;
    private final Color color;


    public BombingCard(String position, Color color) {
        super("Attentat", "En jouant cette carte, notez les coordonnées d'une case de l'échiquier, vide ou occupée par l'une de vos pices. Dès qu'une pièce adverse s'arrrête sur cette case, une bombe explose et la pièce est retirée du jeu. S'il s'agit du roi, la bombe explose mais le roi reste en place", SCType.AFTER_TURN);
        this.position = position;
        this.color = color;
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (position == null) throw new IllegalStateException();
        if (color == null) throw new IllegalStateException();
        boolean thereIsEnemyPieceOnPosition = chessBoard.at(position).getPiece()
                .map(Piece::getColor)
                .map(c -> c != color)
                .orElse(false);
        if (thereIsEnemyPieceOnPosition) throw new IllegalArgumentException("You cannot select a square occupied by an enemy piece");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard) {
        return true;
    }

    @Override
    protected boolean doAction(ChessBoard chessBoard) {
        chessBoard.addEffect(new BombingEffect(position, color));
        return true;
    }
}
