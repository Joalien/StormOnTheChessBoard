package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.CeasefireEffect;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.Bishop;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Queen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CeasefireCardIT {

    ChessBoard board;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a1);
        board.add(new King(Color.BLACK), e8);
    }

    @Test
    void ceasefire_ends_when_a_card_destroys_a_pinned_piece_and_exposes_check() {
        // Black bishop on e5 is pinned against the black king on e8 by the white queen on e2.
        // The queen is far enough from the blast radius around d5 to survive.
        // White knight on d5 sits adjacent to the pinned bishop.
        Queen whiteQueen = new Queen(Color.WHITE);
        board.add(whiteQueen, e2);
        Bishop blackBishop = new Bishop(Color.BLACK);
        board.add(blackBishop, e5);
        Knight whiteKnight = new Knight(Color.WHITE);
        board.add(whiteKnight, d5);

        board.setTurn(Color.WHITE);
        new CeasefireCard().playOn(board, new NoCardParam());
        board.setTurn(Color.WHITE);
        assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof CeasefireEffect),
                "Sanity check: ceasefire is active before the explosion");

        // White plays a NuclearBomb on its own knight — the blast takes adjacent
        // pieces with it, including the pinned black bishop on e5.
        new NuclearBombCard().playOn(board, new PieceCardParam(whiteKnight));

        assertTrue(board.getOutOfTheBoardPieces().contains(blackBishop),
                "Sanity check: the pinned bishop was destroyed by the blast");
        assertTrue(board.isKingUnderAttack(Color.BLACK),
                "Sanity check: removing the pinned bishop exposes the black king to the white queen on e2");
        assertFalse(board.getEffects().stream().anyMatch(e -> e instanceof CeasefireEffect),
                "Ceasefire must end as soon as a king ends up in check, even when the check is caused by a card-triggered removal rather than a move");
    }
}
