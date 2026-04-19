package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.DoubleStrikeCardParam;
import fr.kubys.piece.King;
import fr.kubys.piece.Piece;

public class DoubleStrikeCard extends Card<DoubleStrikeCardParam> {

    public DoubleStrikeCard() {
        super("Double Frappe",
                "Déplacez simultanément deux pièces de même type (deux Fous, deux Cavaliers, deux Tours ou deux Pions).",
                CardType.REPLACE_TURN,
                DoubleStrikeCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, DoubleStrikeCardParam param) {
        if (param.piece1() == null || param.position1() == null || param.piece2() == null || param.position2() == null)
            throw new IllegalStateException("Paramètre de carte manquant");
        if (param.piece1() == param.piece2())
            throw new IllegalArgumentException("Vous devez sélectionner deux pièces différentes");
        if (param.piece1().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece1().getColor());
        if (param.piece2().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece2().getColor());
        if (param.piece1().getClass() != param.piece2().getClass())
            throw new IllegalArgumentException("Les deux pièces doivent être du même type");
        if (param.piece1() instanceof King || param.piece2() instanceof King)
            throw new IllegalArgumentException("Impossible de déplacer un Roi avec cette carte");

        // Validate each move individually
        if (!chessBoard.canAttack(param.piece1(), param.position1()))
            throw new IllegalArgumentException("%s ne peut pas atteindre %s".formatted(param.piece1(), param.position1()));
        if (!chessBoard.canAttack(param.piece2(), param.position2()))
            throw new IllegalArgumentException("%s ne peut pas atteindre %s".formatted(param.piece2(), param.position2()));

        // Cannot capture kings
        chessBoard.at(param.position1()).getPiece().ifPresent(p -> {
            if (p.isKing()) throw new IllegalArgumentException("Impossible de capturer le Roi");
        });
        chessBoard.at(param.position2()).getPiece().ifPresent(p -> {
            if (p.isKing()) throw new IllegalArgumentException("Impossible de capturer le Roi");
        });

        // Destinations must not be the same
        if (param.position1().equals(param.position2()))
            throw new IllegalArgumentException("Les deux destinations doivent être différentes");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, DoubleStrikeCardParam param) {
        chessBoard.fakeSquare(null, param.piece1().getPosition());
        chessBoard.fakeSquare(null, param.piece2().getPosition());
        chessBoard.fakeSquare(param.piece1(), param.position1());
        chessBoard.fakeSquare(param.piece2(), param.position2());
        boolean check = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeAllSquares();
        return !check;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, DoubleStrikeCardParam param) {
        chessBoard.move(param.piece1(), param.position1());
        chessBoard.move(param.piece2(), param.position2());
    }
}
