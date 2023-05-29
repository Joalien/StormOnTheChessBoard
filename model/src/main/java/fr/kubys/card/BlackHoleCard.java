package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Position;
import fr.kubys.piece.extra.BlackHole;

public class BlackHoleCard extends Card<BlackHoleCard.BlackHoleCardParam> {

    public record BlackHoleCardParam(Position position) {}
    private BlackHoleCardParam param;

    public BlackHoleCard() {
        super("Trou noir", "Désignez une case vide qui est \"retirée\" de l'échiquier juqu'à la fin de la partie. Il sera impossible pendant la suite du jeu de s'y arrêter ou de la traverser.", CardType.AFTER_TURN);
    }

    @Override
    protected void setupParams(BlackHoleCardParam params) {
        this.param = params;
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (param.position == null) throw new IllegalStateException();
        if (chessBoard.at(param.position).getPiece().isPresent())
            throw new IllegalArgumentException("You should select an empty square");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard) {
        return true;
    }

    @Override
    protected void doAction(ChessBoard chessBoard) {
        BlackHole piece = new BlackHole();
        chessBoard.add(piece, param.position);
    }
}
