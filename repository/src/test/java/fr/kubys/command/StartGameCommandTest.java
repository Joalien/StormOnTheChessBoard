package fr.kubys.command;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.repository.ChessBoardRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class StartGameCommandTest {
    ChessBoardRepositoryImpl chessBoardRepository;

    @BeforeEach
    void setUp() {
        chessBoardRepository = new ChessBoardRepositoryImpl();
    }

    @Test
    @Disabled("Do not shuffle to make tests easier")
    void should_have_different_card_order() {
        Integer gameId1 = chessBoardRepository.createNewGame();
        ChessBoardReadService game1 = chessBoardRepository.getChessBoardService(gameId1);
        List<List<?>> cards1 = List.of(game1.getWhite().getCards(), game1.getBlack().getCards(), game1.getStack());

        Integer gameId2 = chessBoardRepository.createNewGame();
        ChessBoardReadService game2 = chessBoardRepository.getChessBoardService(gameId2);
        List<List<?>> cards2 = List.of(game2.getWhite().getCards(), game2.getBlack().getCards(), game2.getStack());

        System.out.println(cards1);
        System.out.println(cards2);
        assertNotEquals(cards1, cards2);
    }

    @Test
    void should_have_same_cards_each_time_we_reconstruct_the_game() {
        Integer gameId1 = chessBoardRepository.createNewGame();
        ChessBoardReadService game1 = chessBoardRepository.getChessBoardService(gameId1);

        ChessBoardReadService stillGame1 = chessBoardRepository.getChessBoardService(gameId1);

        assertEquals(game1.getWhite().getCards(), stillGame1.getWhite().getCards());
        assertEquals(game1.getBlack().getCards(), stillGame1.getBlack().getCards());
        assertEquals(game1.getStack(), stillGame1.getStack());
    }

}