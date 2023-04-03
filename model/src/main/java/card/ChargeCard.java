package card;

import board.ChessBoard;
import piece.Color;
import piece.Pawn;
import piece.Piece;
import piece.Square;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ChargeCard extends Card {

    private final Set<Pawn> pawns;

    public ChargeCard(Set<Pawn> pawns) {
        super("Charge", "Avancez tous ceux de vos ppions que vous voulez, et qui le peuvent, d'une case", SCType.REPLACE_TURN);
        this.pawns = pawns;
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (pawns == null) throw new IllegalStateException();
        if (pawns.isEmpty()) throw new IllegalArgumentException("You should select at least one pawn");
        if (pawns.stream().map(Piece::getColor).anyMatch(color1 -> color1 != isPlayedBy))
            throw new IllegalArgumentException("You should move pawns of your color");

        assert chessBoard.getNumberOfFakeSquares() == 0;
        pawns.stream()
                .peek(pawn -> fakeOtherPawns(chessBoard, pawn))
                .map(Pawn::oneSquareForward)
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
        pawns.forEach(p -> chessBoard.fakeSquare(p, p.oneSquareForward()));

        boolean kingIsNotUnderAttack = !chessBoard.isKingUnderAttack(isPlayedBy);

        chessBoard.unfakeAllSquares();
        return kingIsNotUnderAttack;
    }

    @Override
    protected boolean doAction(ChessBoard chessBoard) {
        Comparator<Pawn> startWithMoreAdvancedPawn = Map.of(
                Color.BLACK, Comparator.comparingInt(Pawn::getY),
                Color.WHITE, Comparator.comparingInt(Pawn::getY).reversed()
        ).get(this.isPlayedBy);
        pawns.stream()
                .sorted(startWithMoreAdvancedPawn)
                .forEach(p -> chessBoard.move(p, p.oneSquareForward()));
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
