package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.PieceRemoval;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

/**
 * Self Defense card (Autodéfense): played during the opponent's turn when they capture one of your pieces.
 * The captured piece defends itself — it is restored to its position and the attacking piece is eliminated.
 */
public class SelfDefenseCard extends Card<NoCardParam> {

    public SelfDefenseCard() {
        super("Autodéfense",
                "Votre adversaire veut prendre l'une de vos pièces, mais celle-ci se défend. La pièce attaquée reste en place, et c'est la pièce attaquante qui est éliminée.",
                CardType.ENEMY_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        findCapturedPieceRemoval(chessBoard);
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        PieceRemoval removal = findCapturedPieceRemoval(chessBoard);
        Piece capturedPiece = removal.piece();
        Position capturedPosition = removal.position();

        // The attacking piece is on capturedPosition — fake it as the restored captured piece
        chessBoard.fakeSquare(capturedPiece, capturedPosition);
        boolean isKingUnderAttack = chessBoard.isKingUnderAttack(capturedPiece.getColor());
        chessBoard.unfakeAllSquares();
        return !isKingUnderAttack;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        PieceRemoval removal = findCapturedPieceRemoval(chessBoard);
        Piece capturedPiece = removal.piece();
        Position capturedPosition = removal.position();
        Piece attackingPiece = chessBoard.at(capturedPosition).getPiece()
                .orElseThrow(() -> new IllegalStateException("No piece found on captured position"));

        chessBoard.removePieceFromTheBoard(attackingPiece);
        chessBoard.add(capturedPiece, capturedPosition);
    }

    private PieceRemoval findCapturedPieceRemoval(ChessBoard chessBoard) {
        Color cardPlayerColor = chessBoard.getCurrentTurn().opposite();
        return chessBoard.getPieceRemovals().stream()
                .filter(r -> r.turn() == chessBoard.getTurnNumber() && r.reason() == PieceRemoval.RemovalReason.CAPTURED)
                .filter(r -> r.piece().getColor() == cardPlayerColor)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No piece of yours was captured this turn"));
    }
}
