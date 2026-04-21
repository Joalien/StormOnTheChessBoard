package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.Piece;

public class YouTooBrutusCard extends Card<PieceCardParam> {

    public YouTooBrutusCard() {
        super("Toi aussi Brutus",
                "Jouez cette carte lorsque votre adversaire vous annonce un échec, même mat. La pièce qui vous met en échec change alors de couleur et passe dans votre camp.",
                CardType.ENEMY_TURN_AFTER_MOVE,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        Color cardPlayerColor = chessBoard.getCurrentTurn().opposite();
        if (!chessBoard.isKingUnderAttack(cardPlayerColor))
            throw new IllegalStateException("Your king must be under attack to play this card");
        if (param.piece() == null) throw new IllegalStateException("Missing required card parameter");
        Piece king = chessBoard.allyPieces(cardPlayerColor).stream()
                .filter(Piece::isKing)
                .findFirst()
                .orElseThrow();
        if (!chessBoard.canAttack(param.piece(), king.getPosition()))
            throw new IllegalArgumentException("Selected piece is not checking the king");
        if (param.piece().getColor() == cardPlayerColor)
            throw new IllegalArgumentException("Selected piece must be an enemy piece");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceCardParam param) {
        return true;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        Color cardPlayerColor = chessBoard.getCurrentTurn().opposite();
        param.piece().setColor(cardPlayerColor);
    }
}
