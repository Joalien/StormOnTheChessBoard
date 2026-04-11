package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.KnightCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.extra.Kangaroo;

public class KangarooCard extends Card<KnightCardParam> {

    public KangarooCard() {
        super("Kangourou", "Transformez définitivement l'un de vos cavaliers, ou un cavalier adverse en kangourou. Le kangourou se déplace en faisant deux sauts de cavalier consécutifs.", CardType.AFTER_TURN, KnightCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, KnightCardParam param) {
        if (param.knight() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (chessBoard.getOutOfTheBoardPieces().contains(param.knight()))
            throw new IllegalArgumentException("%s devrait être sur le plateau".formatted(param.knight()));
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, KnightCardParam param) {
        return true; // FIXME
    }

    @Override
    protected void doAction(ChessBoard chessBoard, KnightCardParam param) {
        Position knightPosition = param.knight().getPosition();
        chessBoard.removePieceFromTheBoard(param.knight());
        chessBoard.add(new Kangaroo(param.knight().getColor()), knightPosition);
    }
}
