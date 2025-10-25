package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.piece.BlackPawn;
import fr.kubys.piece.Piece;
import fr.kubys.piece.WhitePawn;

public class ApartheidCard extends Card<NoCardParam> {
    public ApartheidCard() {
        super("Apartheid", "Retirer immédiatement de l'échiquier tous les pions noirs qui se trouvent sur des cases blanches, et tous les pions blancs qui se trouvent sur des cases noires.", CardType.AFTER_TURN, NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        // keep empty, no param
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        chessBoard.getPieces().stream()
                .filter(piece -> whitePawnOnBlackSquare(piece) || blackPawnOnWhiteSquare(piece))
                .forEach(piece -> chessBoard.fakeSquare(null, piece.getPosition()));

        boolean isKingUnderAttack = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeAllSquares();
        return !isKingUnderAttack;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        chessBoard.getPieces().stream()
                .filter(piece -> whitePawnOnBlackSquare(piece) || blackPawnOnWhiteSquare(piece))
                .forEach(chessBoard::removePieceFromTheBoard);
    }

    private static boolean whitePawnOnBlackSquare(Piece piece) {
        return piece instanceof WhitePawn && !piece.getPosition().isWhiteSquare();
    }

    private static boolean blackPawnOnWhiteSquare(Piece piece) {
        return piece instanceof BlackPawn && piece.getPosition().isWhiteSquare();
    }
}
