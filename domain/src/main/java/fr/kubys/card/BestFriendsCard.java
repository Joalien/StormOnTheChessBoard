package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Queen;

public class BestFriendsCard extends Card<NoCardParam> {

    public BestFriendsCard() {
        super("Bonnes Copines",
                "Sur l'échiquier, permutez votre Dame avec celle de votre adversaire.",
                CardType.REPLACE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        if (findQueen(chessBoard, chessBoard.getCurrentTurn()) == null)
            throw new IllegalStateException("You have no Queen on the board");
        if (findQueen(chessBoard, chessBoard.getCurrentTurn().opposite()) == null)
            throw new IllegalStateException("Your opponent has no Queen on the board");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        Piece ownQueen = findQueen(chessBoard, chessBoard.getCurrentTurn());
        Piece enemyQueen = findQueen(chessBoard, chessBoard.getCurrentTurn().opposite());
        Position pos1 = ownQueen.getPosition();
        Position pos2 = enemyQueen.getPosition();
        chessBoard.removePieceFromTheBoard(ownQueen);
        chessBoard.removePieceFromTheBoard(enemyQueen);
        chessBoard.add(ownQueen, pos2);
        chessBoard.add(enemyQueen, pos1);
    }

    private Piece findQueen(ChessBoard chessBoard, fr.kubys.core.Color color) {
        return chessBoard.allyPieces(color).stream()
                .filter(p -> p instanceof Queen)
                .findFirst()
                .orElse(null);
    }
}
