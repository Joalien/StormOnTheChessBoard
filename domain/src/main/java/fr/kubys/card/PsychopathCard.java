package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.piece.Bishop;
import fr.kubys.piece.Piece;

import java.util.List;

public class PsychopathCard extends Card<PieceCardParam> {

    public PsychopathCard() {
        super("Psychopathe",
                "Jouez cette carte lorsque l'un de vos Fous a la possibilité de prendre plusieurs pièces adverses. Vous le faites alors éclater en plusieurs éléments qui vont prendre chacun l'une des pièces adverses se trouvant à portée. Le Fou ainsi éclaté est retiré du jeu.",
                CardType.REPLACE_TURN,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (!(param.piece() instanceof Bishop))
            throw new IllegalArgumentException("Vous devez sélectionner un Fou");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        if (chessBoard.getOutOfTheBoardPieces().contains(param.piece()))
            throw new IllegalArgumentException("Le Fou sélectionné doit être sur le plateau");

        List<Piece> capturable = ExplosionHelper.getCapturableEnemyPieces(chessBoard, param.piece());
        if (capturable.size() < 2)
            throw new IllegalArgumentException("Le Fou doit pouvoir prendre au moins 2 pièces adverses");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        List<Piece> capturable = ExplosionHelper.getCapturableEnemyPieces(chessBoard, param.piece());
        capturable.forEach(chessBoard::removePieceFromTheBoard);
        chessBoard.removePieceFromTheBoard(param.piece());
    }
}
