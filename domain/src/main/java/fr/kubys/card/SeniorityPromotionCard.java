package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Rock;

import java.util.List;

public class SeniorityPromotionCard extends Card<NoCardParam> {

    public SeniorityPromotionCard() {
        super("Promotion à l'ancienneté",
                "S'il ne vous reste plus qu'un ou deux pions sur l'échiquier, vous pouvez immédiatement les promouvoir en d'autres pièces de votre choix, Dame exceptée.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        long pawnCount = chessBoard.allyPieces(chessBoard.getCurrentTurn()).stream()
                .filter(p -> p instanceof Pawn)
                .count();
        if (pawnCount == 0)
            throw new IllegalArgumentException("Vous n'avez aucun pion à promouvoir");
        if (pawnCount > 2)
            throw new IllegalArgumentException("Vous ne pouvez utiliser cette carte que si vous avez 1 ou 2 pions restants");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        return true;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        Color color = chessBoard.getCurrentTurn();
        List<Piece> pawns = chessBoard.allyPieces(color).stream()
                .filter(p -> p instanceof Pawn)
                .toList();
        for (Piece pawn : pawns) {
            Position position = pawn.getPosition();
            chessBoard.removePieceFromTheBoard(pawn);
            chessBoard.add(new Rock(color), position);
        }
    }
}
