package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Position;

import java.util.List;
import java.util.stream.Stream;

public class FourCornersCard extends Card<PieceCardParam> {

    private static final List<Position> CORNERS = List.of(Position.a1, Position.h1, Position.a8, Position.h8);

    public FourCornersCard() {
        super("Quatre Coins",
                "Si trois des quatre coins de l'échiquier sont occupés, placez l'une de vos pièces, de votre choix, dans le quatrième coin.",
                CardType.REPLACE_TURN,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException();
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        long occupied = CORNERS.stream()
                .filter(pos -> chessBoard.at(pos).getPiece().isPresent())
                .count();
        if (occupied != 3)
            throw new IllegalArgumentException("Exactly 3 corners must be occupied (currently %d)".formatted(occupied));
        Position freeCorner = CORNERS.stream()
                .filter(pos -> chessBoard.at(pos).getPiece().isEmpty())
                .findFirst().orElseThrow();
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceCardParam param) {
        Position freeCorner = CORNERS.stream()
                .filter(pos -> chessBoard.at(pos).getPiece().isEmpty())
                .findFirst().orElseThrow();
        chessBoard.fakeSquare(param.piece(), freeCorner);
        chessBoard.fakeSquare(null, param.piece().getPosition());
        boolean check = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeAllSquares();
        return !check;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        Position freeCorner = CORNERS.stream()
                .filter(pos -> chessBoard.at(pos).getPiece().isEmpty())
                .findFirst().orElseThrow();
        chessBoard.at(param.piece().getPosition()).removePiece();
        chessBoard.add(param.piece(), freeCorner);
    }
}
