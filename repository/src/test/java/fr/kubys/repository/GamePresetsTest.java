package fr.kubys.repository;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.board.effect.Effect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class GamePresetsTest {

    ChessBoardRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ChessBoardRepositoryImpl();
    }

    @Test
    void effect_showcase_game_should_have_all_effects_active() {
        Integer gameId = GamePresets.createEffectShowcaseGame(repository);

        ChessBoardReadService game = repository.getChessBoardService(gameId);
        Set<String> effectNames = game.getEffects().stream()
                .map(Effect::getName)
                .collect(Collectors.toSet());

        assertTrue(effectNames.contains("Barricade"), "Missing Barricade effect");
        assertTrue(effectNames.contains("Trou noir"), "Missing Trou noir effect");
        assertTrue(effectNames.contains("Attentat"), "Missing Attentat effect");
        assertTrue(effectNames.contains("Bouche d'égout"), "Missing Bouche d'égout effect");
        assertTrue(effectNames.contains("Magnétisme"), "Missing Magnétisme effect");
        assertTrue(game.getEffects().size() >= 5, "Should have at least 5 effects");
    }

    @Test
    void effect_showcase_game_should_have_kangaroo() {
        Integer gameId = GamePresets.createEffectShowcaseGame(repository);

        ChessBoardReadService game = repository.getChessBoardService(gameId);
        boolean hasKangaroo = game.getPieces().stream()
                .anyMatch(p -> p.getClass().getSimpleName().equals("Kangaroo"));

        assertTrue(hasKangaroo, "Should have a Kangaroo piece on the board");
    }

    @Test
    void effect_showcase_game_should_have_crab() {
        Integer gameId = GamePresets.createEffectShowcaseGame(repository);

        ChessBoardReadService game = repository.getChessBoardService(gameId);
        boolean hasCrab = game.getPieces().stream()
                .anyMatch(p -> p.getClass().getSimpleName().equals("Crab"));

        assertTrue(hasCrab, "Should have a Crab piece on the board");
    }

    @Test
    void effect_showcase_game_should_have_neutral_piece() {
        Integer gameId = GamePresets.createEffectShowcaseGame(repository);

        ChessBoardReadService game = repository.getChessBoardService(gameId);
        boolean hasNeutral = game.getPieces().stream()
                .anyMatch(p -> p.getColor() == fr.kubys.core.Color.NONE);

        assertTrue(hasNeutral, "Should have a neutral piece on the board");
    }

    @Test
    void effect_showcase_game_should_be_playable() {
        Integer gameId = GamePresets.createEffectShowcaseGame(repository);

        ChessBoardReadService game = repository.getChessBoardService(gameId);
        assertNotNull(game.getCurrentPlayer());
        assertNotNull(game.getCurrentStateName());
        assertFalse(game.getPieces().isEmpty());
    }
}
