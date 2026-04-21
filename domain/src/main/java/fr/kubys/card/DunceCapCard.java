package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.DunceCapEffect;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.King;

import java.util.Set;

public class DunceCapCard extends Card<PieceToPositionCardParam> implements Effectable<DunceCapEffect> {

    private static final Set<Position> CORNERS = Set.of(Position.a1, Position.a8, Position.h1, Position.h8);

    public DunceCapCard() {
        super("Bonnet d'Âne",
                "Désignez une pièce adverse (sauf le Roi), que vous envoyez au piquet sur une case libre située dans l'un des quatre coins de l'échiquier. Votre adversaire ne pourra pas déplacer cette pièce à son prochain tour.",
                CardType.AFTER_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null)
            throw new IllegalStateException("Paramètre de carte manquant");
        if (param.piece().getColor() == chessBoard.getCurrentTurn())
            throw new IllegalArgumentException("Vous devez cibler une pièce adverse");
        if (param.piece() instanceof King)
            throw new IllegalArgumentException("Impossible de cibler un Roi");
        if (!CORNERS.contains(param.positionToMoveOn()))
            throw new IllegalArgumentException("La destination doit être l'un des quatre coins de l'échiquier");
        if (chessBoard.at(param.positionToMoveOn()).getPiece().isPresent())
            throw new IllegalArgumentException("La case de destination doit être libre");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.at(param.piece().getPosition()).removePiece();
        chessBoard.add(param.piece(), param.positionToMoveOn());
        // Only add freeze effect if the piece survived placement (e.g. not destroyed by a bombing)
        if (param.piece().getPosition() != null) {
            chessBoard.addEffect(new DunceCapEffect(param.piece(), chessBoard.getTurnNumber()));
        }
    }
}
