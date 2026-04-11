package fr.kubys.card;

import fr.kubys.board.CheckException;
import fr.kubys.board.ChessBoard;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Rock;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NeutralityCardIT {

    @Test
    void should_not_allow_moving_neutral_piece_to_position_that_checks_own_king() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        Rock neutralRock = new Rock(Color.BLACK);
        board.add(neutralRock, a5);
        neutralRock.setColor(Color.NONE);
        board.setTurn(Color.WHITE);

        // White moves neutral rook from a5 to e5, which would check white king on e1 through the e-file
        assertThrows(CheckException.class, () -> board.tryToMove(a5, e5));
    }
}
