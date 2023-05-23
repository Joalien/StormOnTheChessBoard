package card;

import board.ChessBoard;
import piece.Color;
import piece.Pawn;
import piece.Piece;
import piece.Square;

import java.util.*;
import java.util.stream.Collectors;

public class ChargeCard extends Card {

    private Set<Pawn> pawns;

    public ChargeCard() {
        super("Charge", "Avancez tous ceux de vos ppions que vous voulez, et qui le peuvent, d'une case", CardType.REPLACE_TURN);
    }

    @Override
    protected void setupParams(List<?> params) {
        this.pawns = params.stream().map(p -> (Pawn) p).collect(Collectors.toSet());
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (pawns == null) throw new IllegalStateException();
        if (pawns.isEmpty()) throw new IllegalArgumentException("You should select at least one pawn");
        if (pawns.stream().map(Piece::getColor).anyMatch(color -> color != isPlayedBy))
            throw new CannotMoveThisColorException(isPlayedBy.opposite());

        assert chessBoard.getNumberOfFakeSquares() == 0;
        pawns.stream()
                .peek(pawn -> fakeOtherPawns(chessBoard, pawn))
                .map(Pawn::oneSquareForward)
                .map(Optional::get)
                .map(chessBoard::at)
                .peek(pos -> chessBoard.unfakeAllSquares())
                .map(Square::getPiece)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(ChargeCard::throwsCannotMoveOneSquareForwardException);
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard) {
        pawns.forEach(p -> chessBoard.fakeSquare(null, p.getPosition()));
        pawns.stream()
                .filter(p -> p.oneSquareForward().isPresent())
                .forEach(p -> chessBoard.fakeSquare(p, p.oneSquareForward().get()));

        boolean kingIsNotUnderAttack = !chessBoard.isKingUnderAttack(isPlayedBy);

        chessBoard.unfakeAllSquares();
        return kingIsNotUnderAttack;
    }

    @Override
    protected boolean doAction(ChessBoard chessBoard) {
        Comparator<Pawn> startWithMoreAdvancedPawn = Map.of(
                Color.BLACK, Comparator.<Pawn>comparingInt(pawn -> pawn.getRow().getRowNumber()),
                Color.WHITE, Comparator.<Pawn>comparingInt(pawn -> pawn.getRow().getRowNumber()).reversed()
        ).get(this.isPlayedBy);
        pawns.stream()
                .sorted(startWithMoreAdvancedPawn)
                .forEach(p -> chessBoard.move(p, p.oneSquareForward().get()));
        return true;
    }

    private void fakeOtherPawns(ChessBoard cb, Pawn pawn) {
        pawns.stream()
                .filter(pawn1 -> !pawn1.equals(pawn))
                .forEach(p -> cb.fakeSquare(null, p.getPosition()));
    }

    private static void throwsCannotMoveOneSquareForwardException(Piece p) {
        throw new IllegalArgumentException(p + " cannot move one square forward!");
    }
}
