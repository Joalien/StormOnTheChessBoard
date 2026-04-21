package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.TwoPieceCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Rock;

public class UrbanPlanningCard extends Card<TwoPieceCardParam> {

    public UrbanPlanningCard() {
        super("Urbanisme",
                "Sur l'échiquier, permutez l'une de vos Tours avec une Tour adverse.",
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
        boolean piece1IsOwn = !param.piece1().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn());
        boolean piece2IsOwn = !param.piece2().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn());
        if (piece1IsOwn == piece2IsOwn)
            throw new IllegalArgumentException("Vous devez sélectionner une de vos pièces et une pièce adverse");
        if (!(param.piece1() instanceof Rock) || !(param.piece2() instanceof Rock))
            throw new IllegalArgumentException("Les deux pièces doivent être des Tours");
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
