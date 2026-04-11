package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.TwoPieceCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Rock;

public class BoxCard extends Card<TwoPieceCardParam> {

    public BoxCard() {
        super("Box",
                "Sur l'échiquier, permutez un Cavalier adverse avec une Tour adverse.",
                CardType.AFTER_TURN,
                TwoPieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, TwoPieceCardParam param) {
        if (param.piece1() == null || param.piece2() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (param.piece1().getColor() == chessBoard.getCurrentTurn())
            throw new CannotMoveThisColorException(param.piece1().getColor());
        if (param.piece2().getColor() == chessBoard.getCurrentTurn())
            throw new CannotMoveThisColorException(param.piece2().getColor());
        if (param.piece1().getPosition() == null || param.piece2().getPosition() == null)
            throw new IllegalStateException("Les deux pièces doivent être sur le plateau");
        boolean hasKnight = param.piece1() instanceof Knight || param.piece2() instanceof Knight;
        boolean hasRook = param.piece1() instanceof Rock || param.piece2() instanceof Rock;
        if (!hasKnight || !hasRook)
            throw new IllegalArgumentException("Vous devez sélectionner un Cavalier et une Tour");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, TwoPieceCardParam param) {
        Position pos1 = param.piece1().getPosition();
        Position pos2 = param.piece2().getPosition();
        chessBoard.fakeSquare(param.piece2(), pos1);
        chessBoard.fakeSquare(param.piece1(), pos2);
        boolean isKingUnderAttack = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeAllSquares();
        return !isKingUnderAttack;
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
