package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.KangarooCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.extra.Kangaroo;

public class KangarooCard extends Card<KangarooCardParam> {

    public KangarooCard() {
        super("Kangourou", "Transformez définitivement l'un de vos cavaliers, ou un cavalier adverse en kangourou. Le kangourou se déplace en faisant deux sauts de cavalier consécutifs.", CardType.AFTER_TURN, KangarooCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, KangarooCardParam param) {
        if (param.knight() == null) throw new IllegalStateException();
        if (chessBoard.getOutOfTheBoardPieces().contains(param.knight()))
            throw new IllegalArgumentException("%s should be on the board".formatted(param.knight()));
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, KangarooCardParam param) {
        return true; // FIXME
    }

    @Override
    protected void doAction(ChessBoard chessBoard, KangarooCardParam param) {
        Position knightPosition = param.knight().getPosition();
        chessBoard.removePieceFromTheBoard(param.knight());
        chessBoard.add(new Kangaroo(param.knight().getColor()), knightPosition);
    }
}
