package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;

public class PuppetCard extends Card<PieceCardParam> {

    public PuppetCard() {
        super("Marionnette",
                "Jouez cette carte lorsque vous êtes mis en échec, même mat. Vous permutez alors sur l'échiquier votre Roi avec l'un de vos Pions.",
                CardType.BEFORE_TURN,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException("Missing required card parameter");
        if (!(param.piece() instanceof Pawn))
            throw new IllegalArgumentException("You must select a Pawn");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        if (chessBoard.getOutOfTheBoardPieces().contains(param.piece()))
            throw new IllegalArgumentException("Pawn is not on the board");
        if (!chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn()))
            throw new IllegalStateException("Your king must be under attack to play this card");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        Piece king = chessBoard.allyPieces(chessBoard.getCurrentTurn()).stream()
                .filter(Piece::isKing)
                .findFirst()
                .orElseThrow();
        Position kingPos = king.getPosition();
        Position pawnPos = param.piece().getPosition();

        chessBoard.removePieceFromTheBoard(king);
        chessBoard.removePieceFromTheBoard(param.piece());
        chessBoard.add(king, pawnPos);
        chessBoard.add(param.piece(), kingPos);
    }
}
