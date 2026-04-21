package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.KnightCardParam;
import fr.kubys.piece.Piece;

import java.util.List;

public class SuicideKnightCard extends Card<KnightCardParam> {

    public SuicideKnightCard() {
        super("Cavalier Suicidaire",
                "Jouez cette carte lorsque l'un de vos Cavaliers a la possibilité de prendre plusieurs pièces adverses. Vous le faites alors éclater en plusieurs éléments qui vont prendre chacun l'une des pièces adverses se trouvant à portée. Le Cavalier ainsi éclaté est retiré du jeu.",
                CardType.REPLACE_TURN,
                KnightCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, KnightCardParam param) {
        if (param.knight() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (param.knight().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.knight().getColor());
        if (chessBoard.getOutOfTheBoardPieces().contains(param.knight()))
            throw new IllegalArgumentException("Le Cavalier sélectionné doit être sur le plateau");

        List<Piece> capturable = ExplosionHelper.getCapturableEnemyPieces(chessBoard, param.knight());
        if (capturable.size() < 2)
            throw new IllegalArgumentException("Le Cavalier doit pouvoir prendre au moins 2 pièces adverses");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, KnightCardParam param) {
        List<Piece> capturable = ExplosionHelper.getCapturableEnemyPieces(chessBoard, param.knight());
        capturable.forEach(chessBoard::removePieceFromTheBoard);
        chessBoard.removePieceFromTheBoard(param.knight());
    }
}
