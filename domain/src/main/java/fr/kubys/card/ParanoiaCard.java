package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.piece.Bishop;
import fr.kubys.piece.King;
import fr.kubys.piece.Piece;

public class ParanoiaCard extends Card<PieceToPositionCardParam> {

    public ParanoiaCard() {
        super("Paranoïa",
                "Au lieu de déplacer l'une de vos pièces, déplacez un Fou de votre adversaire de manière à prendre l'une de ses propres pièces.",
                CardType.REPLACE_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null)
            throw new IllegalStateException("Paramètre de carte manquant");
        if (!(param.piece() instanceof Bishop))
            throw new IllegalArgumentException("La pièce doit être un Fou");
        if (param.piece().getColor() == chessBoard.getCurrentTurn())
            throw new IllegalArgumentException("Le Fou doit appartenir à l'adversaire ou être neutre");
        if (!param.piece().isPositionTheoreticallyReachable(param.positionToMoveOn()))
            throw new IllegalArgumentException("Le Fou ne peut pas atteindre cette position");
        if (!chessBoard.emptyPath(param.piece(), param.positionToMoveOn()))
            throw new IllegalArgumentException("Le chemin est bloqué");
        Piece target = chessBoard.at(param.positionToMoveOn()).getPiece()
                .orElseThrow(() -> new IllegalArgumentException("La case de destination est vide"));
        if (target.getColor() != param.piece().getColor())
            throw new IllegalArgumentException("La cible doit être de la même couleur que le Fou");
        if (target instanceof King)
            throw new IllegalArgumentException("Impossible de capturer un Roi");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        Piece target = chessBoard.at(param.positionToMoveOn()).getPiece().orElseThrow();
        chessBoard.removePieceFromTheBoard(target);
        chessBoard.move(param.piece(), param.positionToMoveOn());
    }
}
