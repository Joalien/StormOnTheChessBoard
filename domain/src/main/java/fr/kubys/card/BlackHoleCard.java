package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PositionCardParam;
import fr.kubys.piece.extra.BlackHoleSquare;

public class BlackHoleCard extends Card<PositionCardParam> {

    public BlackHoleCard() {
        super("Trou noir", "Désignez une case vide qui est \"retirée\" de l'échiquier juqu'à la fin de la partie. Il sera impossible pendant la suite du jeu de s'y arrêter ou de la traverser.", CardType.AFTER_TURN, PositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PositionCardParam param) {
        if (param.position() == null) throw new IllegalStateException();
        if (chessBoard.at(param.position()).getPiece().isPresent())
            throw new IllegalArgumentException("You should select an empty square");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PositionCardParam param) {
        return true;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PositionCardParam param) {
        chessBoard.setSquare(new BlackHoleSquare(param.position()));
    }
}
