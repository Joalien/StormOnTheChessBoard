package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.PieceRemoval;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.util.Set;
import java.util.stream.Collectors;

public class NuclearBombCard extends Card<PieceCardParam> {

    public NuclearBombCard() {
        super("Bombe Atomique",
                "Si la pièce que vous venez de déplacer n'a pas pris une pièce adverse, vous pouvez la faire \"exploser\". Elle est alors retirée du jeu, ainsi que toutes les autres pièces, quelles que soient leur couleur, qui se trouvent sur les huit cases adjacentes. Les Rois ne sont pas affectés par l'explosion.",
                CardType.AFTER_TURN, PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException();
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new IllegalArgumentException("You can only explode your own pieces");
        if (param.piece().isKing())
            throw new IllegalArgumentException("You cannot explode a King");
        boolean capturedThisTurn = chessBoard.getPieceRemovals().stream()
                .anyMatch(r -> r.turn() == chessBoard.getTurnNumber() && r.reason() == PieceRemoval.RemovalReason.CAPTURED);
        if (capturedThisTurn)
            throw new IllegalArgumentException("You cannot explode a piece that just captured");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceCardParam param) {
        Set<Position> affectedPositions = getAffectedPositions(param.piece().getPosition());
        // Fake-remove the exploding piece and all non-king adjacent pieces
        chessBoard.fakeSquare(null, param.piece().getPosition());
        for (Position pos : affectedPositions) {
            chessBoard.at(pos).getPiece()
                    .filter(p -> !p.isKing())
                    .ifPresent(p -> chessBoard.fakeSquare(null, pos));
        }
        boolean isKingUnderAttack = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeAllSquares();
        return !isKingUnderAttack;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        Position center = param.piece().getPosition();
        Set<Position> affectedPositions = getAffectedPositions(center);

        // Remove adjacent pieces first (except kings)
        for (Position pos : affectedPositions) {
            chessBoard.at(pos).getPiece()
                    .filter(p -> !p.isKing())
                    .ifPresent(chessBoard::removePieceFromTheBoard);
        }
        // Remove the exploding piece
        chessBoard.removePieceFromTheBoard(param.piece());
    }

    private static Set<Position> getAffectedPositions(Position center) {
        return Position.generateAllPositions().stream()
                .filter(pos -> !pos.equals(center))
                .filter(pos -> pos.hasNoPositionBetween(center))
                .collect(Collectors.toSet());
    }
}
