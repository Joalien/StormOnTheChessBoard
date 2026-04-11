package fr.kubys.game;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.QuadrilleCard;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.card.params.QuadrilleCardParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnemyReactionStateTest {

    private GameStateController gameStateController;
    private Card<NoCardParam> enemyTurnCard;

    @BeforeEach
    void setUp() {
        gameStateController = new GameStateController();
        gameStateController.startGame(new Random().nextLong());

        enemyTurnCard = new Card<>("TestEnemyCard", "test", CardType.ENEMY_TURN, NoCardParam.class) {
            @Override
            protected void validInput(ChessBoard chessBoard, NoCardParam param) {}
            @Override
            protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) { return true; }
            @Override
            protected void doAction(ChessBoard chessBoard, NoCardParam param) {}
        };
    }

    @Test
    void should_enter_enemy_reaction_after_move() {
        gameStateController.tryToMove(e2, e4);

        assertEquals(StateEnum.ENEMY_REACTION, gameStateController.getCurrentState());
        // Current player is still white (no swap)
        assertEquals(gameStateController.getWhite(), gameStateController.getCurrentPlayer());
    }

    @Test
    void should_auto_resolve_on_pass() {
        gameStateController.tryToMove(e2, e4);
        assertEquals(StateEnum.ENEMY_REACTION, gameStateController.getCurrentState());

        // Active player passes — enemy reaction is auto-resolved, turn ends
        gameStateController.tryToPass();

        assertEquals(StateEnum.BEGINNING_OF_THE_TURN, gameStateController.getCurrentState());
        assertEquals(gameStateController.getBlack(), gameStateController.getCurrentPlayer());
    }

    @Test
    void should_auto_resolve_when_active_player_plays_card() {
        gameStateController.tryToMove(e2, e4);
        assertEquals(StateEnum.ENEMY_REACTION, gameStateController.getCurrentState());

        // Active player plays an AFTER_TURN card — enemy reaction is auto-resolved
        QuadrilleCard afterMoveCard = new QuadrilleCard();
        gameStateController.getCurrentPlayer().getCards().add(afterMoveCard);
        gameStateController.tryToPlayCard(afterMoveCard, new QuadrilleCardParam(QuadrilleCard.Direction.CLOCKWISE));

        // Enters a new ENEMY_REACTION after the card play
        assertEquals(StateEnum.ENEMY_REACTION, gameStateController.getCurrentState());
        assertEquals(gameStateController.getWhite(), gameStateController.getCurrentPlayer());
    }

    @Test
    void should_allow_opponent_to_play_enemy_turn_card() {
        gameStateController.tryToMove(e2, e4);
        assertEquals(StateEnum.ENEMY_REACTION, gameStateController.getCurrentState());

        // Opponent (black) plays an ENEMY_TURN card
        gameStateController.getBlack().getCards().add(enemyTurnCard);
        gameStateController.tryToPlayCard(enemyTurnCard, new NoCardParam());

        // Reaction resolved, back to target state, active player unchanged
        assertEquals(StateEnum.MOVE_WITHOUT_CARD_PLAYED, gameStateController.getCurrentState());
        assertEquals(gameStateController.getWhite(), gameStateController.getCurrentPlayer());
    }

    @Test
    void should_not_allow_active_player_to_play_enemy_turn_card() {
        gameStateController.tryToMove(e2, e4);
        assertEquals(StateEnum.ENEMY_REACTION, gameStateController.getCurrentState());

        // Active player (white) tries to play ENEMY_TURN — not in their hand via getOpponent()
        gameStateController.getWhite().getCards().add(enemyTurnCard);
        // The card is in white's hand but getOpponent() returns black, so it should fail
        assertThrows(Exception.class, () -> gameStateController.tryToPlayCard(enemyTurnCard, new NoCardParam()));
    }

    @Test
    void should_skip_enemy_reaction_if_already_played_this_turn() {
        // First action: move triggers enemy reaction
        gameStateController.tryToMove(e2, e4);
        assertEquals(StateEnum.ENEMY_REACTION, gameStateController.getCurrentState());

        // Opponent plays enemy card
        gameStateController.getBlack().getCards().add(enemyTurnCard);
        gameStateController.tryToPlayCard(enemyTurnCard, new NoCardParam());
        assertEquals(StateEnum.MOVE_WITHOUT_CARD_PLAYED, gameStateController.getCurrentState());

        // Second action: play after-turn card should NOT trigger enemy reaction again
        QuadrilleCard afterMoveCard = new QuadrilleCard();
        gameStateController.getCurrentPlayer().getCards().add(afterMoveCard);
        gameStateController.tryToPlayCard(afterMoveCard, new QuadrilleCardParam(QuadrilleCard.Direction.CLOCKWISE));

        assertEquals(StateEnum.END_OF_THE_TURN, gameStateController.getCurrentState());
    }

    @Test
    void should_reset_enemy_card_flag_on_new_turn() {
        // White moves, passes (auto-resolves enemy reaction + ends turn)
        gameStateController.tryToMove(e2, e4);
        gameStateController.tryToPass();

        // Now it's black's turn — move should trigger enemy reaction again
        gameStateController.tryToMove(e7, e5);
        assertEquals(StateEnum.ENEMY_REACTION, gameStateController.getCurrentState());
        assertEquals(gameStateController.getBlack(), gameStateController.getCurrentPlayer());
    }
}
