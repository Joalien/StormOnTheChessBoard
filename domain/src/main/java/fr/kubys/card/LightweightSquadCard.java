package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.LightweightSquadCardParam;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Square;

public class LightweightSquadCard extends Card<LightweightSquadCardParam> {

    public LightweightSquadCard() {
        super("Escouade légère", "Avancez deux de vos pions, chacun de deux cases", CardType.REPLACE_TURN, LightweightSquadCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, LightweightSquadCardParam param) {
        if (param.pawn1() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (param.pawn2() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (param.pawn1().equals(param.pawn2()))
            throw new IllegalArgumentException("Vous devez sélectionner deux pions différents");
        if (param.pawn1().getColor() != param.pawn2().getColor())
            throw new IllegalArgumentException("Vous devez déplacer des pions de la même couleur");
        if (param.pawn1().twoSquaresForward().isEmpty() || param.pawn2().twoSquaresForward().isEmpty()) {
            throw new IllegalArgumentException("Impossible d'avancer de deux cases un pion situé sur l'avant-dernière rangée");
        }

        chessBoard.fakeSquare(null, param.pawn2().getPosition());
        if (cannotMoveTwoSquaresForward(chessBoard, param.pawn1())) {
            chessBoard.unfakeSquare(param.pawn2().getPosition());
            throw new IllegalArgumentException("Impossible d'avancer %s de deux cases".formatted(param.pawn1()));
        }
        chessBoard.unfakeSquare(param.pawn2().getPosition());

        chessBoard.fakeSquare(null, param.pawn1().getPosition());
        if (cannotMoveTwoSquaresForward(chessBoard, param.pawn2())) {
            chessBoard.unfakeSquare(param.pawn1().getPosition());
            throw new IllegalArgumentException("Impossible d'avancer %s de deux cases".formatted(param.pawn2()));
        }
        chessBoard.unfakeSquare(param.pawn1().getPosition());
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, LightweightSquadCardParam param) {
        return true; // FIXME
    }

    @Override
    protected void doAction(ChessBoard chessBoard, LightweightSquadCardParam param) {
        chessBoard.move(param.pawn1(), param.pawn1().twoSquaresForward().get());
        chessBoard.move(param.pawn2(), param.pawn2().twoSquaresForward().get());
    }

    private boolean cannotMoveTwoSquaresForward(ChessBoard cb, Pawn pawn) {
        return pawn.oneSquareForward().map(cb::at).flatMap(Square::getPiece).isPresent() ||
                pawn.twoSquaresForward().map(cb::at).flatMap(Square::getPiece).isPresent();
    }
}
