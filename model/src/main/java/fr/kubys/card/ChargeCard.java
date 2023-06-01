package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.ChargeCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Square;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public class ChargeCard extends Card<ChargeCardParam> {

    public ChargeCard() {
        super("Charge", "Avancez tous ceux de vos pions que vous voulez, et qui le peuvent, d'une case", CardType.REPLACE_TURN, ChargeCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, ChargeCardParam param) {
        if (param.pawns() == null) throw new IllegalStateException();
        if (param.pawns().isEmpty()) throw new IllegalArgumentException("You should select at least one pawn");
        if (param.pawns().stream().map(Piece::getColor).anyMatch(color -> color != chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(chessBoard.getCurrentTurn().opposite());

        assert chessBoard.getNumberOfFakeSquares() == 0;
        param.pawns().stream()
                .peek(pawn -> fakeOtherPawns(chessBoard, pawn, param))
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
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, ChargeCardParam param) {
        param.pawns().forEach(p -> chessBoard.fakeSquare(null, p.getPosition()));
        param.pawns().stream()
                .filter(p -> p.oneSquareForward().isPresent())
                .forEach(p -> chessBoard.fakeSquare(p, p.oneSquareForward().get()));

        boolean kingIsNotUnderAttack = !chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());

        chessBoard.unfakeAllSquares();
        return kingIsNotUnderAttack;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, ChargeCardParam param) {
        Comparator<Pawn> startWithMoreAdvancedPawn = Map.of(
                Color.BLACK, Comparator.<Pawn>comparingInt(pawn -> pawn.getRow().getRowNumber()),
                Color.WHITE, Comparator.<Pawn>comparingInt(pawn -> pawn.getRow().getRowNumber()).reversed()
        ).get(chessBoard.getCurrentTurn());
        param.pawns().stream()
                .sorted(startWithMoreAdvancedPawn)
                .forEach(p -> chessBoard.move(p, p.oneSquareForward().get()));
    }

    private void fakeOtherPawns(ChessBoard cb, Pawn pawn, ChargeCardParam param) {
        param.pawns().stream()
                .filter(pawn1 -> !pawn1.equals(pawn))
                .forEach(p -> cb.fakeSquare(null, p.getPosition()));
    }

    private static void throwsCannotMoveOneSquareForwardException(Piece p) {
        throw new IllegalArgumentException(p + " cannot move one square forward!");
    }
}
