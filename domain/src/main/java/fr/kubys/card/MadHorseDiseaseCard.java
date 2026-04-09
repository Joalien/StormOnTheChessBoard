package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.KnightCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Bishop;

public class MadHorseDiseaseCard extends Card<KnightCardParam> {

    public MadHorseDiseaseCard() {
        super("Encéphalopathie Spongiforme Équine",
                "L'un de vos Cavaliers, ou un Cavalier adverse, devient Fou. Il se déplacera désormais uniquement comme un Fou.",
                CardType.AFTER_TURN,
                KnightCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, KnightCardParam param) {
        if (param.knight() == null) throw new IllegalStateException();
        if (chessBoard.getOutOfTheBoardPieces().contains(param.knight()))
            throw new IllegalArgumentException("%s should be on the board".formatted(param.knight()));
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, KnightCardParam param) {
        Position knightPosition = param.knight().getPosition();
        Bishop fakeBishop = new Bishop(param.knight().getColor());
        chessBoard.fakeSquare(fakeBishop, knightPosition);
        boolean check = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeAllSquares();
        return !check;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, KnightCardParam param) {
        Position knightPosition = param.knight().getPosition();
        chessBoard.removePieceFromTheBoard(param.knight());
        chessBoard.add(new Bishop(param.knight().getColor()), knightPosition);
    }
}
