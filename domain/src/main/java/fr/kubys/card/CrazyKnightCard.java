package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.TwoPieceCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Bishop;
import fr.kubys.piece.Knight;

public class CrazyKnightCard extends Card<TwoPieceCardParam> {

    public CrazyKnightCard() {
        super("Cheval Fou",
                "Sur l'échiquier, permutez l'un de vos Fous avec l'un de vos Cavaliers.",
                CardType.AFTER_TURN,
                TwoPieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, TwoPieceCardParam param) {
        if (param.piece1() == null || param.piece2() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (param.piece1() == param.piece2()) throw new IllegalArgumentException("Les pièces doivent être différentes");
        if (chessBoard.getOutOfTheBoardPieces().contains(param.piece1()))
            throw new IllegalArgumentException("%s devrait être sur le plateau".formatted(param.piece1()));
        if (chessBoard.getOutOfTheBoardPieces().contains(param.piece2()))
            throw new IllegalArgumentException("%s devrait être sur le plateau".formatted(param.piece2()));
        if (param.piece1().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece1().getColor());
        if (param.piece2().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece2().getColor());
        boolean hasBishop = param.piece1() instanceof Bishop || param.piece2() instanceof Bishop;
        boolean hasKnight = param.piece1() instanceof Knight || param.piece2() instanceof Knight;
        if (!hasBishop || !hasKnight)
            throw new IllegalArgumentException("Vous devez sélectionner un Fou et un Cavalier");
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
