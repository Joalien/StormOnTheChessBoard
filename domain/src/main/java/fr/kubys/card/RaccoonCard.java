package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.TwoPieceCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.*;

public class RaccoonCard extends Card<TwoPieceCardParam> {

    public RaccoonCard() {
        super("Raton laveur",
                "Sur l'échiquier, permutez l'un de vos Pions avec l'un de vos Fous, ou l'un de vos Cavaliers, ou l'une de vos Tours.",
                CardType.REPLACE_TURN,
                TwoPieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, TwoPieceCardParam param) {
        if (param.piece1() == null || param.piece2() == null) throw new IllegalStateException();
        if (param.piece1() == param.piece2()) throw new IllegalArgumentException("Pieces must be different");
        if (chessBoard.getOutOfTheBoardPieces().contains(param.piece1()))
            throw new IllegalArgumentException("%s should be on the board".formatted(param.piece1()));
        if (chessBoard.getOutOfTheBoardPieces().contains(param.piece2()))
            throw new IllegalArgumentException("%s should be on the board".formatted(param.piece2()));
        if (param.piece1().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece1().getColor());
        if (param.piece2().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece2().getColor());
        boolean hasPawn = param.piece1() instanceof Pawn || param.piece2() instanceof Pawn;
        boolean hasOfficer = param.piece1() instanceof Bishop || param.piece1() instanceof Knight || param.piece1() instanceof Rock
                || param.piece2() instanceof Bishop || param.piece2() instanceof Knight || param.piece2() instanceof Rock;
        if (!hasPawn || !hasOfficer)
            throw new IllegalArgumentException("You must select one Pawn and one Bishop, Knight or Rook");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, TwoPieceCardParam param) {
        chessBoard.fakeSquare(param.piece1(), param.piece2().getPosition());
        chessBoard.fakeSquare(param.piece2(), param.piece1().getPosition());
        boolean check = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeAllSquares();
        return !check;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, TwoPieceCardParam param) {
        Position pos1 = param.piece1().getPosition();
        Position pos2 = param.piece2().getPosition();
        chessBoard.removePieceFromTheBoard(param.piece1());
        chessBoard.removePieceFromTheBoard(param.piece2());
        chessBoard.add(param.piece1(), pos2);
        chessBoard.add(param.piece2(), pos1);
    }
}
