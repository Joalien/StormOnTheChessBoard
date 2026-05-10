package fr.kubys.ai.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.ApartheidCard;
import fr.kubys.card.AsylumCard;
import fr.kubys.card.BarricadeCard;
import fr.kubys.card.BetrayalCard;
import fr.kubys.card.BombingCard;
import fr.kubys.card.ChargeCard;
import fr.kubys.card.params.*;
import fr.kubys.core.Color;
import fr.kubys.game.ChessBoardServiceFactory;
import fr.kubys.game.GameStateController;
import fr.kubys.piece.King;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Queen;
import fr.kubys.piece.Rock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class CardCandidateGeneratorsTest {

    private final CardCandidateGenerators generators = new CardCandidateGenerators();

    private static GameStateController controller(ChessBoard board) {
        var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> board);
        gsc.startGame(1L);
        return gsc;
    }

    @Test
    void no_card_param_yields_one_candidate() {
        ChessBoard board = standardBoard();
        var card = new ApartheidCard();

        List<CardParam> candidates = generators.candidatesFor(card, controller(board));

        assertEquals(1, candidates.size());
        assertInstanceOf(NoCardParam.class, candidates.get(0));
    }

    @Test
    void piece_param_yields_one_candidate_per_piece_on_board() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.add(new Pawn(Color.BLACK), d4);
        board.setTurn(Color.WHITE);

        List<CardParam> candidates = generators.candidatesFor(new BetrayalCard(), controller(board));

        assertEquals(3, candidates.size());
        assertTrue(candidates.stream().allMatch(PieceCardParam.class::isInstance));
    }

    @Test
    void position_param_yields_all_64_squares() {
        List<CardParam> candidates = generators.candidatesFor(new BombingCard(), controller(standardBoard()));

        assertEquals(64, candidates.size());
        assertTrue(candidates.stream().allMatch(PositionCardParam.class::isInstance));
    }

    @Test
    void two_piece_param_caps_at_top_K_squared() {
        // Top-K=8, K*(K-1) = 56 ordered pairs
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.add(new Queen(Color.WHITE), d1);
        board.add(new Queen(Color.BLACK), d8);
        board.add(new Rock(Color.WHITE), a1);
        board.add(new Rock(Color.BLACK), a8);
        board.add(new Rock(Color.WHITE), h1);
        board.add(new Rock(Color.BLACK), h8);
        board.add(new Pawn(Color.WHITE), e2);
        board.setTurn(Color.WHITE);

        List<CardParam> candidates = generators.candidatesFor(new AsylumCard(), controller(board));

        // 8 pieces × 7 = 56
        assertEquals(56, candidates.size());
        assertTrue(candidates.stream().allMatch(TwoPieceCardParam.class::isInstance));
    }

    @Test
    void barricade_param_only_includes_squares_near_own_king() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);

        List<CardParam> candidates = generators.candidatesFor(new BarricadeCard(), controller(board));

        assertFalse(candidates.isEmpty());
        assertTrue(candidates.stream().allMatch(BarricadeCardParam.class::isInstance));
        // Every endpoint should be within radius-2 of e1
        for (CardParam p : candidates) {
            BarricadeCardParam b = (BarricadeCardParam) p;
            for (var sq : List.of(b.from1(), b.to1(), b.from2(), b.to2())) {
                int fileDist = Math.abs(sq.getFile().getFileNumber() - 5);
                int rowDist = Math.abs(sq.getRow().getRowNumber() - 1);
                assertTrue(fileDist <= 2 && rowDist <= 2,
                        "Barricade endpoint %s should be near king e1, but is %d/%d away".formatted(sq, fileDist, rowDist));
            }
        }
    }

    @Test
    void charge_param_includes_empty_singletons_and_full_set() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.add(new Pawn(Color.WHITE), a2);
        board.add(new Pawn(Color.WHITE), b2);
        board.setTurn(Color.WHITE);

        List<CardParam> candidates = generators.candidatesFor(new ChargeCard(), controller(board));

        // empty + full + 2 singletons = 4
        assertEquals(4, candidates.size());
        assertTrue(candidates.stream().anyMatch(p -> ((ChargeCardParam) p).pawns().isEmpty()));
        assertTrue(candidates.stream().anyMatch(p -> ((ChargeCardParam) p).pawns().size() == 2));
        assertEquals(2, candidates.stream().filter(p -> ((ChargeCardParam) p).pawns().size() == 1).count());
    }

    private static ChessBoard standardBoard() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        return board;
    }
}
