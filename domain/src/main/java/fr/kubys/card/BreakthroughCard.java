package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;

import java.util.Optional;

public class BreakthroughCard extends Card<PieceCardParam> {

    public BreakthroughCard() {
        super("Percée",
                "L'un de vos pions prend une pièce adverse en avançant droit devant lui.",
                CardType.REPLACE_TURN,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (!(param.piece() instanceof Pawn))
            throw new IllegalArgumentException("Vous ne pouvez utiliser qu'un Pion");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        Position target = oneForward(param.piece());
        if (target == null)
            throw new IllegalArgumentException("Le Pion ne peut pas avancer");
        Piece targetPiece = chessBoard.at(target).getPiece()
                .orElseThrow(() -> new IllegalArgumentException("Aucune pièce adverse devant le pion"));
        if (targetPiece.getColor() == chessBoard.getCurrentTurn())
            throw new IllegalArgumentException("Impossible de capturer votre propre pièce");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        Position target = oneForward(param.piece());
        chessBoard.at(target).getPiece().ifPresent(p -> chessBoard.removePieceFromTheBoard(p));
        chessBoard.at(param.piece().getPosition()).removePiece();
        chessBoard.add(param.piece(), target);
    }

    private Position oneForward(Piece piece) {
        Optional<Row> next = (piece.getColor() == Color.WHITE) ?
                piece.getRow().next() : piece.getRow().previous();
        return next.map(row -> Position.posToSquare(piece.getFile(), row)).orElse(null);
    }
}
