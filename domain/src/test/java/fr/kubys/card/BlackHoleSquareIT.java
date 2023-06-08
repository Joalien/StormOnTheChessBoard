package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.QuadrilleCardParam;
import fr.kubys.piece.extra.BlackHoleSquare;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.a1;

class BlackHoleSquareIT {

    @Test
    void should_not_turn_if_black_hole_in_corner() {
        ChessBoard chessBoard = ChessBoard.createWithInitialState();
        chessBoard.removePieceFromTheBoard(chessBoard.at(a1).getPiece().get());
        chessBoard.setSquare(new BlackHoleSquare(a1));

        Assertions.assertThrows(BlackHoleSquare.BlackHoleException.class, () -> new QuadrilleCard().playOn(chessBoard, new QuadrilleCardParam(QuadrilleCard.Direction.CLOCKWISE)));
    }
}