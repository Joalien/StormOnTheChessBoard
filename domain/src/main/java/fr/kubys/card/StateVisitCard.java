package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

public class StateVisitCard extends Card<NoCardParam> {

    public StateVisitCard() {
        super("Visites Officielles",
                "Permutez votre Roi avec celui de votre adversaire, à condition que cela ne mette aucun de vous deux en échec.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        // Kings always exist on the board
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        Piece ownKing = findKing(chessBoard, chessBoard.getCurrentTurn());
        Piece enemyKing = findKing(chessBoard, chessBoard.getCurrentTurn().opposite());
        Position pos1 = ownKing.getPosition();
        Position pos2 = enemyKing.getPosition();
        chessBoard.removePieceFromTheBoard(ownKing);
        chessBoard.removePieceFromTheBoard(enemyKing);
        chessBoard.add(ownKing, pos2);
        chessBoard.add(enemyKing, pos1);
    }

    private Piece findKing(ChessBoard chessBoard, Color color) {
        return chessBoard.allyPieces(color).stream()
                .filter(Piece::isKing)
                .findFirst()
                .orElseThrow();
    }
}
