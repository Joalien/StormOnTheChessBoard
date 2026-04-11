package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.TwoPieceCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Bishop;
import fr.kubys.piece.Rock;

public class AsylumCard extends Card<TwoPieceCardParam> {

    public AsylumCard() {
        super("Asile",
                "Sur l'échiquier, permutez l'un de vos Fous avec l'une de vos Tours.",
                CardType.AFTER_TURN,
                TwoPieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, TwoPieceCardParam param) {
        if (param.piece1() == null || param.piece2() == null) throw new IllegalStateException("Missing required card parameter");
        if (param.piece1() == param.piece2()) throw new IllegalArgumentException("Pieces must be different");
        if (chessBoard.getOutOfTheBoardPieces().contains(param.piece1()))
            throw new IllegalArgumentException("%s should be on the board".formatted(param.piece1()));
        if (chessBoard.getOutOfTheBoardPieces().contains(param.piece2()))
            throw new IllegalArgumentException("%s should be on the board".formatted(param.piece2()));
        if (param.piece1().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece1().getColor());
        if (param.piece2().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece2().getColor());
        boolean hasBishop = param.piece1() instanceof Bishop || param.piece2() instanceof Bishop;
        boolean hasRock = param.piece1() instanceof Rock || param.piece2() instanceof Rock;
        if (!hasBishop || !hasRock)
            throw new IllegalArgumentException("You must select one Bishop and one Rook");
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
