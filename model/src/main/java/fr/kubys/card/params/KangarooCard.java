package fr.kubys.card.params;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.KangarooCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.extra.Kangaroo;

public class KangarooCard extends Card<KangarooCardParam> {

    private KangarooCardParam param;

    public KangarooCard() {
        super("Kangaroo", "Transformez définitivement l'un de vos cavaliers, ou un cavalier adverse en kangourou. Le kangourou se déplace en faisant deux sauts de cavalier consécutifs.", CardType.AFTER_TURN);
    }

    @Override
    protected void setupParams(KangarooCardParam params) {
        this.param = params;
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (param.knight() == null) throw new IllegalStateException();
        if (chessBoard.getOutOfTheBoardPieces().contains(param.knight()))
            throw new IllegalArgumentException("%s should be on the board".formatted(param.knight()));
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard) {
        return true; // FIXME
    }

    @Override
    protected void doAction(ChessBoard chessBoard) {
        Position knightPosition = param.knight().getPosition();
        chessBoard.removePieceFromTheBoard(param.knight());
        chessBoard.add(new Kangaroo(param.knight().getColor()), knightPosition);
    }
}
