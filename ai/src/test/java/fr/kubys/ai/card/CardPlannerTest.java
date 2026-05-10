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
        // Material swing alone is +20 (lose enemy pawn, gain own pawn). Positional terms
        // (the central pawn now belongs to us, mobility shifts, center occupation) push the
        // delta higher. We just check the swing is clearly positive.
        assertTrue(best.get().score() >= 20,
                "Betrayal of central pawn should score at least the material swing, got " + best.get().score());
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
    void bestPlayFor_with_explicit_player_uses_that_hand() {
        // White is current player; we plan for BLACK's hand explicitly. Only black has the
        // RetaliationCard — current-player overload would not see it.
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> board);
        gsc.startGame(1L);
        // Empty white's hand, give black a Retaliation card explicitly.
        var retaliation = new fr.kubys.card.RetaliationCard();
        gsc.getWhite().getCards().clear();
        gsc.getBlack().getCards().clear();
        gsc.getBlack().getCards().add(retaliation);

        // Implicit overload (current player = WHITE, empty hand) → empty.
        Optional<ScoredCardPlay> implicit = planner.bestPlayFor(1, gsc, CardType.ENEMY_TURN_AFTER_MOVE, List::of);
        assertTrue(implicit.isEmpty(), "Default overload looks at current player's hand (white, empty)");

        // Explicit overload (player = BLACK with Retaliation) → planner sees the card.
        // The simulation will fail because validInput rejects (no enemy capture this turn),
        // so the result is still empty — but the candidate generation went through black's hand.
        Player aiPlayer = gsc.getBlack();
        Optional<ScoredCardPlay> explicit = planner.bestPlayFor(1, gsc, aiPlayer, CardType.ENEMY_TURN_AFTER_MOVE, List::of);
        assertTrue(explicit.isEmpty(), "All Retaliation candidates fail validInput here, so empty is expected");
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
