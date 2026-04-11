package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.piece.King;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Queen;

public class OhDarlingCard extends Card<PieceToPositionCardParam> {

    public OhDarlingCard() {
        super("Oh, Darling !",
                "Amenez directement votre Roi sur une case libre adjacente à votre Dame, ou votre Dame sur une case libre adjacente à votre Roi.",
                CardType.REPLACE_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null) throw new IllegalStateException("Missing required card parameter");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        if (!(param.piece() instanceof King) && !(param.piece() instanceof Queen))
            throw new IllegalArgumentException("You must move your King or your Queen");
        if (chessBoard.at(param.positionToMoveOn()).getPiece().isPresent())
            throw new IllegalArgumentException("Target square must be empty");

        Piece partner = findPartner(chessBoard, param.piece());
        if (partner == null)
            throw new IllegalStateException("Cannot find the partner piece on the board");

        boolean adjacent = partner.getPosition().getFile().distanceTo(param.positionToMoveOn().getFile()) <= 1
                && partner.getPosition().getRow().distanceTo(param.positionToMoveOn().getRow()) <= 1;
        if (!adjacent)
            throw new IllegalArgumentException("Target must be adjacent to the partner");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.fakeSquare(param.piece(), param.positionToMoveOn());
        chessBoard.fakeSquare(null, param.piece().getPosition());
        boolean check = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeAllSquares();
        return !check;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.at(param.piece().getPosition()).removePiece();
        chessBoard.add(param.piece(), param.positionToMoveOn());
    }

    private Piece findPartner(ChessBoard chessBoard, Piece piece) {
        Class<?> partnerType = (piece instanceof King) ? Queen.class : King.class;
        return chessBoard.allyPieces(chessBoard.getCurrentTurn()).stream()
                .filter(partnerType::isInstance)
                .findFirst()
                .orElse(null);
    }
}
