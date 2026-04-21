package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.CrabEffect;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.extra.Crab;

public class CrabCard extends Card<PieceCardParam> implements Effectable<CrabEffect> {

    public CrabCard() {
        super("Crabe",
                "Transformez l'un de vos Pions en Crabe, et ce définitivement. Le Crabe se déplace en diagonale, comme un Fou, mais d'une case seulement, en avançant ou en reculant. Parvenu sur la dernière rangée, il est promu comme un pion ordinaire.",
                CardType.AFTER_TURN,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException("Missing required card parameter");
        if (!(param.piece() instanceof Pawn))
            throw new IllegalArgumentException("You can only transform a Pawn into a Crab");
        if (param.piece() instanceof Crab)
            throw new IllegalArgumentException("This piece is already a Crab");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        var position = param.piece().getPosition();
        var color = param.piece().getColor();
        chessBoard.removePieceFromTheBoard(param.piece());
        Crab crab = new Crab(color);
        chessBoard.add(crab, position);
        chessBoard.addEffect(new CrabEffect(crab));
    }
}
