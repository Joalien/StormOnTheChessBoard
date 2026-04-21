package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;

public class ApartheidCard extends Card<NoCardParam> {
    public ApartheidCard() {
        super("Apartheid", "Retirer immédiatement de l'échiquier tous les pions noirs qui se trouvent sur des cases blanches, et tous les pions blancs qui se trouvent sur des cases noires.", CardType.AFTER_TURN, NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        // keep empty, no param
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        chessBoard.getPieces().stream()
                .filter(piece -> whitePawnOnBlackSquare(piece) || blackPawnOnWhiteSquare(piece))
                .forEach(chessBoard::removePieceFromTheBoard);
    }

    private static boolean whitePawnOnBlackSquare(Piece piece) {
        return piece instanceof Pawn && piece.getColor() == Color.WHITE && !piece.getPosition().isWhiteSquare();
    }

    private static boolean blackPawnOnWhiteSquare(Piece piece) {
        return piece instanceof Pawn && piece.getColor() == Color.BLACK && piece.getPosition().isWhiteSquare();
    }
}
