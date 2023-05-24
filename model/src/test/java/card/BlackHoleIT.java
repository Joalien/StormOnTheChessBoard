package card;

import board.ChessBoard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import piece.extra.BlackHole;

import java.util.List;

import static core.Position.a1;

class BlackHoleIT {

    @Test
    void should_not_turn_if_black_hole_in_corner() {
        ChessBoard chessBoard = ChessBoard.createWithInitialState();
        chessBoard.removePieceFromTheBoard(chessBoard.at(a1).getPiece().get());
        chessBoard.add(new BlackHole(), a1);

        Assertions.assertThrows(BlackHole.BlackHoleException.class, () -> new QuadrilleCard().playOn(chessBoard, List.of(QuadrilleCard.Direction.CLOCKWISE)));
    }
}