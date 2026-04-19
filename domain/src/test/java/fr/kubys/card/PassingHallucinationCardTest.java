package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.BombingEffect;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class PassingHallucinationCardTest {

    ChessBoard board;
    PassingHallucinationCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new PassingHallucinationCard();
    }

    @Test
    void should_move_rock_like_bishop() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a1);

        card.playOn(board, new PieceToPositionCardParam(rock, d4));

        assertEquals(rock, board.at(d4).getPiece().get());
    }

    @Test
    void should_reject_straight_move() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(rock, a6)));
    }

    @Test
    void should_reject_capture() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, c3);
        Pawn enemy = new Pawn(Color.BLACK);
        board.add(enemy, f6);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, f6)));
    }

    @Test
    void should_promote_pawn_when_reaching_last_rank() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, a3);

        card.playOn(board, new PieceToPositionCardParam(pawn, f8));

        // Pawn moved diagonally like a bishop and reached last rank → auto-promoted
        assertInstanceOf(Queen.class, board.at(f8).getPiece().get());
        assertEquals(Color.WHITE, board.at(f8).getPiece().get().getColor());
    }

    @Test
    void should_trigger_bombing_effect_on_move() {
        Rock rock = new Rock(Color.WHITE);
        board.add(rock, a1);
        board.addEffect(new BombingEffect(d4, Color.BLACK));

        card.playOn(board, new PieceToPositionCardParam(rock, d4));

        // Rock moved to bombed square and got destroyed by afterMoveHook
        assertTrue(board.at(d4).getPiece().isEmpty());
        assertTrue(board.getOutOfTheBoardPieces().contains(rock));
    }
}
