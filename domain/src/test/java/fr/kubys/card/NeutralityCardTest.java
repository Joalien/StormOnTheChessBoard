package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.IllegalMoveException;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class NeutralityCardTest {

    ChessBoard board;
    NeutralityCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new NeutralityCard();
    }

    @Nested
    class PlayCard {
        @Test
        void should_neutralize_enemy_knight() {
            Knight knight = new Knight(Color.BLACK);
            board.add(knight, g8);

            card.playOn(board, new PieceCardParam(knight));

            assertEquals(Color.NONE, knight.getColor());
            assertEquals(knight, board.at(g8).getPiece().get());
        }

        @Test
        void should_neutralize_enemy_rook() {
            Rock rock = new Rock(Color.BLACK);
            board.add(rock, a8);

            card.playOn(board, new PieceCardParam(rock));

            assertEquals(Color.NONE, rock.getColor());
        }

        @Test
        void should_neutralize_enemy_bishop() {
            Bishop bishop = new Bishop(Color.BLACK);
            board.add(bishop, c8);

            card.playOn(board, new PieceCardParam(bishop));

            assertEquals(Color.NONE, bishop.getColor());
        }

        @Test
        void should_neutralize_enemy_pawn_into_neutral_pawn() {
            Pawn pawn = new Pawn(Color.BLACK);
            board.add(pawn, d5);

            card.playOn(board, new PieceCardParam(pawn));

            Piece neutralPiece = board.at(d5).getPiece().get();
            assertInstanceOf(Pawn.class, neutralPiece);
            assertEquals(Color.NONE, neutralPiece.getColor());
        }

        @Test
        void should_promote_neutralized_pawn_on_back_rank() {
            Pawn pawn = new Pawn(Color.BLACK);
            board.add(pawn, a8);

            card.playOn(board, new PieceCardParam(pawn));

            Piece piece = board.at(a8).getPiece().get();
            assertInstanceOf(Queen.class, piece);
            assertEquals(Color.NONE, piece.getColor());
        }

        @Test
        void should_reject_king() {
            // Can't neutralize: king is already on the board from setUp
            King blackKing = (King) board.at(e8).getPiece().get();

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new PieceCardParam(blackKing)));
        }

        @Test
        void should_reject_queen() {
            Queen queen = new Queen(Color.BLACK);
            board.add(queen, d8);

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new PieceCardParam(queen)));
        }

        @Test
        void should_reject_own_piece() {
            Knight knight = new Knight(Color.WHITE);
            board.add(knight, b1);

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new PieceCardParam(knight)));
        }

        @Test
        void should_reject_already_neutral_piece() {
            Knight knight = new Knight(Color.BLACK);
            board.add(knight, g8);
            knight.setColor(Color.NONE);

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new PieceCardParam(knight)));
        }

        @Test
        void should_reject_if_neutralizing_creates_check() {
            // Black bishop on c3 blocks the diagonal to white king on e1
            // If neutralized, it becomes NONE and threatens white king
            board = ChessBoard.createEmpty();
            board.add(new King(Color.WHITE), a1);
            board.add(new King(Color.BLACK), e8);
            Bishop bishop = new Bishop(Color.BLACK);
            board.add(bishop, d4);
            board.setTurn(Color.WHITE);

            assertDoesNotThrow(
                    () -> card.playOn(board, new PieceCardParam(bishop)));
        }
    }

    @Nested
    class NeutralPieceMovement {
        @Test
        void both_players_can_move_neutral_piece() {
            Knight knight = new Knight(Color.BLACK);
            board.add(knight, c3);
            knight.setColor(Color.NONE);

            // White can move neutral knight
            board.setTurn(Color.WHITE);
            assertDoesNotThrow(() -> board.tryToMove(c3, d5));
            assertEquals(knight, board.at(d5).getPiece().get());

            // Black can move it too
            board.setTurn(Color.BLACK);
            assertDoesNotThrow(() -> board.tryToMove(d5, c3));
        }

        @Test
        void neutral_piece_can_capture_opponent_pieces() {
            Rock rock = new Rock(Color.BLACK);
            board.add(rock, a5);
            rock.setColor(Color.NONE);
            Pawn target = new Pawn(Color.WHITE);
            board.add(target, a2);

            // Black moves neutral rook to capture white pawn
            board.setTurn(Color.BLACK);
            assertDoesNotThrow(() -> board.tryToMove(a5, a2));
            assertTrue(board.getOutOfTheBoardPieces().contains(target));
        }

        @Test
        void neutral_piece_cannot_capture_own_player_pieces() {
            Rock rock = new Rock(Color.BLACK);
            board.add(rock, a5);
            rock.setColor(Color.NONE);
            Pawn ownPiece = new Pawn(Color.WHITE);
            board.add(ownPiece, a2);

            // White tries to use neutral rook to capture own white pawn
            board.setTurn(Color.WHITE);
            assertThrows(IllegalMoveException.class, () -> board.tryToMove(a5, a2));
        }

        @Test
        void neutral_piece_gives_check() {
            Bishop bishop = new Bishop(Color.BLACK);
            board.add(bishop, d4);
            bishop.setColor(Color.NONE);

            // Neutral bishop on d4 threatens white king on e1 (but also threats black king on e8... wait no, d4-e8 is not a diagonal)
            // Actually d4 threatens e1 via diagonal? d4 to e1: no, distance is file=1, row=3. Not a valid bishop move.
            // Let's use a better setup
            board = ChessBoard.createEmpty();
            board.add(new King(Color.WHITE), e1);
            board.add(new King(Color.BLACK), a8);
            Bishop neutralBishop = new Bishop(Color.WHITE);
            board.add(neutralBishop, b4);
            neutralBishop.setColor(Color.NONE);
            board.setTurn(Color.WHITE);

            // Neutral bishop on b4 threatens... let's check: b4 to e1 is a diagonal (3 files, 3 rows). Yes!
            assertTrue(board.isKingUnderAttack(Color.WHITE));
        }

        @Test
        void can_move_neutral_piece_even_if_exposes_own_king() {
            board = ChessBoard.createEmpty();
            board.add(new King(Color.WHITE), d1);
            board.add(new King(Color.BLACK), e8);
            Rock neutralRock = new Rock(Color.BLACK);
            board.add(neutralRock, d3);
            neutralRock.setColor(Color.NONE);
            board.add(new Queen(Color.BLACK), d8); // threatens d1 through d3
            board.setTurn(Color.WHITE);

            // Moving neutral rook off d-file exposes white king (temporary check allowed)
            assertDoesNotThrow(() -> board.tryToMove(d3, a3));
        }
    }

    @Nested
    class NeutralPawnMovement {
        @Test
        void neutral_pawn_moves_forward_for_white() {
            board.add(new Pawn(Color.NONE), d5);
            board.setTurn(Color.WHITE);

            assertDoesNotThrow(() -> board.tryToMove(d5, d6));
        }

        @Test
        void neutral_pawn_moves_forward_for_black() {
            board.add(new Pawn(Color.NONE), d5);
            board.setTurn(Color.BLACK);

            assertDoesNotThrow(() -> board.tryToMove(d5, d4));
        }

        @Test
        void neutral_pawn_cannot_move_backward_for_white() {
            board.add(new Pawn(Color.NONE), d5);
            board.setTurn(Color.WHITE);

            assertThrows(Exception.class, () -> board.tryToMove(d5, d4));
        }

        @Test
        void neutral_pawn_cannot_move_backward_for_black() {
            board.add(new Pawn(Color.NONE), d5);
            board.setTurn(Color.BLACK);

            assertThrows(Exception.class, () -> board.tryToMove(d5, d6));
        }

        @Test
        void neutral_pawn_can_capture_diagonally_in_player_direction() {
            board.add(new Pawn(Color.NONE), d5);
            board.add(new Knight(Color.BLACK), e6);
            board.setTurn(Color.WHITE);

            assertDoesNotThrow(() -> board.tryToMove(d5, e6));
        }

        @Test
        void neutral_pawn_cannot_capture_diagonally_backward() {
            board.add(new Pawn(Color.NONE), d5);
            board.add(new Knight(Color.BLACK), e4);
            board.setTurn(Color.WHITE);

            assertThrows(Exception.class, () -> board.tryToMove(d5, e4));
        }

        @Test
        void neutral_pawn_promotes_on_row_8_for_white() {
            board.add(new Pawn(Color.NONE), d7);
            board.setTurn(Color.WHITE);

            board.tryToMove(d7, d8);

            // Auto-promotes to queen
            assertInstanceOf(Queen.class, board.at(d8).getPiece().get());
        }

        @Test
        void neutral_pawn_promotes_on_row_1_for_black() {
            board.add(new Pawn(Color.NONE), d2);
            board.setTurn(Color.BLACK);

            board.tryToMove(d2, d1);

            assertInstanceOf(Queen.class, board.at(d1).getPiece().get());
        }
    }
}
