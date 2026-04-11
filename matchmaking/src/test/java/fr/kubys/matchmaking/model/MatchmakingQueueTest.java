package fr.kubys.matchmaking.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MatchmakingQueueTest {

    private MatchmakingQueue queue;

    @BeforeEach
    void setUp() {
        queue = new MatchmakingQueue();
    }

    @Test
    void first_player_should_wait() {
        String token = queue.join();

        assertTrue(queue.isWaiting(token));
        assertTrue(queue.getMatch(token).isEmpty());
    }

    @Test
    void second_player_should_create_match() {
        String token1 = queue.join();
        String token2 = queue.join();

        assertFalse(queue.isWaiting(token1));
        assertFalse(queue.isWaiting(token2));

        Optional<MatchResult> match1 = queue.getMatch(token1);
        Optional<MatchResult> match2 = queue.getMatch(token2);

        assertTrue(match1.isPresent());
        assertTrue(match2.isPresent());
        assertSame(match1.get(), match2.get());

        assertEquals("white", match1.get().getColorForToken(token1));
        assertEquals("black", match1.get().getColorForToken(token2));
    }

    @Test
    void third_player_should_start_new_waiting_round() {
        queue.join();
        queue.join();
        String token3 = queue.join();

        assertTrue(queue.isWaiting(token3));
        assertTrue(queue.getMatch(token3).isEmpty());
    }

    @Test
    void cancel_should_remove_from_queue() {
        String token = queue.join();
        assertTrue(queue.isWaiting(token));

        queue.cancel(token);
        assertFalse(queue.isWaiting(token));
    }

    @Test
    void getMatch_unknown_token_should_return_empty() {
        assertTrue(queue.getMatch("unknown").isEmpty());
    }

    @Test
    void getColorForToken_unknown_token_should_throw() {
        String token1 = queue.join();
        String token2 = queue.join();
        MatchResult match = queue.getMatch(token1).orElseThrow();

        assertThrows(IllegalArgumentException.class, () -> match.getColorForToken("unknown"));
    }

    @Test
    void gameId_should_be_null_initially_and_settable() {
        String token1 = queue.join();
        queue.join();
        MatchResult match = queue.getMatch(token1).orElseThrow();

        assertNull(match.getGameId());
        match.setGameId(42);
        assertEquals(42, match.getGameId());
    }
}
