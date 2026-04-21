package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.Pawn;

import java.util.Optional;

public class BanzaiCard extends Card<PieceCardParam> {

    public BanzaiCard() {
        super("Banzaï",
                "Avancez l'un de vos pions de trois cases. Aucune prise en passant ne sera possible.",
                CardType.REPLACE_TURN,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (!(param.piece() instanceof Pawn))
            throw new IllegalArgumentException("Vous ne pouvez avancer qu'un Pion");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        Position target = threeSquaresForward(param.piece());
        if (target == null)
            throw new IllegalArgumentException("Le Pion ne peut pas avancer de 3 cases (sortirait du plateau)");
        // Check all 3 squares are empty
        Position pos = param.piece().getPosition();
        for (int i = 0; i < 3; i++) {
            pos = oneForward(pos, param.piece().getColor());
            if (pos == null) throw new IllegalArgumentException("Le Pion ne peut pas avancer de 3 cases");
            if (chessBoard.at(pos).getPiece().isPresent())
                throw new IllegalArgumentException("La case %s n'est pas vide".formatted(pos));
        }
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        Position target = threeSquaresForward(param.piece());
        chessBoard.at(param.piece().getPosition()).removePiece();
        chessBoard.add(param.piece(), target);
    }

    private Position threeSquaresForward(fr.kubys.piece.Piece piece) {
        Position pos = piece.getPosition();
        for (int i = 0; i < 3; i++) {
            pos = oneForward(pos, piece.getColor());
            if (pos == null) return null;
        }
        return pos;
    }

    private Position oneForward(Position pos, Color color) {
        Optional<Row> next = (color == Color.WHITE) ? pos.getRow().next() : pos.getRow().previous();
        return next.map(row -> Position.posToSquare(pos.getFile(), row)).orElse(null);
    }
}
