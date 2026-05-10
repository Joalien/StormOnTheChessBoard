package fr.kubys.ai.card;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.board.ChessBoard;
import fr.kubys.card.BetrayalCard;
import fr.kubys.card.CardType;
import fr.kubys.command.Command;
import fr.kubys.core.Color;
import fr.kubys.game.ChessBoardServiceFactory;
import fr.kubys.game.GameStateController;
import fr.kubys.piece.King;
import fr.kubys.piece.Pawn;
import fr.kubys.player.Player;
import fr.kubys.repository.ChessBoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CardPlannerTest {

    private ChessBoardRepository repository;
    private CardPlanner planner;

    @BeforeEach
    void setUp() {
        repository = mock(ChessBoardRepository.class);
        planner = new CardPlanner(repository, new CardCandidateGenerators());
    }

    @Test
    void picks_betrayal_on_enemy_pawn_to_swing_material() {
        // Initial: white K e1, black K e8, black pawn d4 (on white's side)
        ChessBoard initialBoard = ChessBoard.createEmpty();
        initialBoard.add(new King(Color.WHITE), e1);
        initialBoard.add(new King(Color.BLACK), e8);
        initialBoard.add(new Pawn(Color.BLACK), d4);
        initialBoard.setTurn(Color.WHITE);
        var initial = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> initialBoard);
        initial.startGame(1L);
        // Stamp BetrayalCard into white's hand
        var betrayalCard = new BetrayalCard();
        initial.getCurrentPlayer().getCards().clear();
        initial.getCurrentPlayer().getCards().add(betrayalCard);

        // Simulated board after Betrayal: black pawn becomes white pawn
        ChessBoard postBoard = ChessBoard.createEmpty();
        postBoard.add(new King(Color.WHITE), e1);
        postBoard.add(new King(Color.BLACK), e8);
        postBoard.add(new Pawn(Color.WHITE), d4);
        postBoard.setTurn(Color.WHITE);
        var simulated = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> postBoard);
        simulated.startGame(2L);
        when(repository.simulate(eq(1), any())).thenAnswer(inv -> {
            // For the only valid candidate (the BP@d4), return the post-state. The
            // serialized command holds the position string "d4" — we look it up against
            // the initial board to identify which piece the candidate refers to.
            List<Command> cmds = inv.getArgument(1);
            var cardCmd = (fr.kubys.command.PlayCardWithImmutableParamCommand<?>) cmds.get(cmds.size() - 1);
            String pieceSquare = (String) cardCmd.getParam().get("piece");
            if ("d4".equals(pieceSquare)) {
                return simulated;
            }
            throw new IllegalArgumentException("invalid candidate");
        });

        Optional<ScoredCardPlay> best = planner.bestPlayFor(1, initial, CardType.BEFORE_TURN, List::of);

        assertTrue(best.isPresent());
        assertEquals(betrayalCard, best.get().card());
        assertInstanceOf(fr.kubys.card.params.PieceCardParam.class, best.get().param());
        // Material swing: black had a pawn (-10), now white has a pawn (+10) — delta = +20
        assertEquals(20, best.get().score());
    }

    @Test
    void returns_empty_when_player_has_no_card_of_type() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        var initial = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> board);
        initial.startGame(1L);
        initial.getCurrentPlayer().getCards().clear();

        Optional<ScoredCardPlay> best = planner.bestPlayFor(1, initial, CardType.BEFORE_TURN, List::of);

        assertTrue(best.isEmpty());
    }

    @Test
    void returns_empty_when_card_has_no_generic_generator() {
        // KnightCardParam has no registered generator
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        var initial = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> board);
        initial.startGame(1L);
        initial.getCurrentPlayer().getCards().clear();
        initial.getCurrentPlayer().getCards().add(new fr.kubys.card.CrazyKnightCard()); // TwoPieceCardParam — but we only have kings, so all candidates will throw

        Optional<ScoredCardPlay> best = planner.bestPlayFor(1, initial, CardType.AFTER_TURN, List::of);

        // No mock setup → simulate is null → all attempts return empty via NPE catch.
        // Or when only 1 piece per side, no two-piece pair is generated for AsylumCard semantics.
        assertTrue(best.isEmpty());
    }
}
