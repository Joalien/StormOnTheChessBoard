package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.TwoPieceCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Pawn;

public class InfiltrationCard extends Card<TwoPieceCardParam> {

    public InfiltrationCard() {
        super("Infiltration",
                "Sur l'échiquier, permutez un de vos pions avec un pion adverse.",
                CardType.REPLACE_TURN,
                TwoPieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, TwoPieceCardParam param) {
        if (param.piece1() == null || param.piece2() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (!(param.piece1() instanceof Pawn) || !(param.piece2() instanceof Pawn))
            throw new IllegalArgumentException("Les deux pièces doivent être des Pions");
        boolean isFirstPawnAlly = !param.piece1().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn());
        boolean isSecondPawnAlly = !param.piece2().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn());
        if (isFirstPawnAlly == isSecondPawnAlly)
            throw new IllegalArgumentException("Vous devez sélectionner un de vos pions et un pion adverse");
        chessBoard.getEffects().forEach(effect -> effect.beforeMoveHook(chessBoard, param.piece1()));
        chessBoard.getEffects().forEach(effect -> effect.beforeMoveHook(chessBoard, param.piece2()));
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
