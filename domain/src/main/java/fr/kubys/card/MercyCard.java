package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.PieceRemoval;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.*;

/**
 * Mercy card (Pitié): when a major piece (Rook, Bishop, Queen) captures one of your pawns,
 * the pawn begs for mercy. The attacker, moved, stops on the square just before the pawn
 * on its path. The pawn is restored.
 */
public class MercyCard extends Card<NoCardParam> {

    public MercyCard() {
        super("Pitié",
                "Lorsqu'une pièce adverse (Tour, Fou ou Dame) se rue sauvagement sur l'un de vos pions, ce dernier l'implore à genoux. La pièce adverse, émue, s'immobilise alors sur la case précédente, juste avant votre pion.",
                CardType.ENEMY_TURN_AFTER_MOVE,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        PieceRemoval removal = findCapturedPawnRemoval(chessBoard);
        Position capturedPosition = removal.position();

        Piece attackingPiece = chessBoard.at(capturedPosition).getPiece()
                .orElseThrow(() -> new IllegalStateException("Aucune pièce trouvée sur la position capturée"));

        if (!(attackingPiece instanceof Rock || attackingPiece instanceof Bishop || attackingPiece instanceof Queen))
            throw new IllegalArgumentException("Seule une Tour, un Fou ou une Dame peut déclencher Grâce");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        PieceRemoval removal = findCapturedPawnRemoval(chessBoard);
        Piece capturedPawn = removal.piece();
        Position capturedPosition = removal.position();
        Position mercySquare = computeMercySquare(capturedPosition, chessBoard.getLastMoveFrom());
        Piece attacker = chessBoard.at(capturedPosition).getPiece().orElseThrow();

        chessBoard.fakeSquare(capturedPawn, capturedPosition);
        if (!mercySquare.equals(capturedPosition)) {
            chessBoard.fakeSquare(attacker, mercySquare);
        }
        boolean isKingUnderAttack = chessBoard.isKingUnderAttack(capturedPawn.getColor());
        chessBoard.unfakeAllSquares();
        return !isKingUnderAttack;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        PieceRemoval removal = findCapturedPawnRemoval(chessBoard);
        Piece capturedPawn = removal.piece();
        Position capturedPosition = removal.position();
        Position mercySquare = computeMercySquare(capturedPosition, chessBoard.getLastMoveFrom());
        Piece attackingPiece = chessBoard.at(capturedPosition).getPiece()
                .orElseThrow(() -> new IllegalStateException("Aucune pièce trouvée sur la position capturée"));

        chessBoard.removePieceFromTheBoard(attackingPiece);
        chessBoard.add(capturedPawn, capturedPosition);
        if (!mercySquare.equals(capturedPosition)) {
            chessBoard.add(attackingPiece, mercySquare);
        }
    }

    private PieceRemoval findCapturedPawnRemoval(ChessBoard chessBoard) {
        Color cardPlayerColor = chessBoard.getCurrentTurn().opposite();
        return chessBoard.getPieceRemovals().stream()
                .filter(r -> r.turn() == chessBoard.getTurnNumber() && r.reason() == PieceRemoval.RemovalReason.CAPTURED)
                .filter(r -> r.piece().getColor() == cardPlayerColor)
                .filter(r -> r.piece() instanceof Pawn)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aucun de vos pions n'a été capturé ce tour"));
    }

    static Position computeMercySquare(Position pawnPosition, Position attackerOrigin) {
        int fileDirection = Integer.signum(attackerOrigin.getFile().getFileNumber() - pawnPosition.getFile().getFileNumber());
        int rowDirection = Integer.signum(attackerOrigin.getRow().getRowNumber() - pawnPosition.getRow().getRowNumber());
        return Position.posToSquare(
                pawnPosition.getFile().getFileNumber() + fileDirection,
                pawnPosition.getRow().getRowNumber() + rowDirection
        );
    }
}
