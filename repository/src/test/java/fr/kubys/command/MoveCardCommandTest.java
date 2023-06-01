package fr.kubys.command;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.card.Card;
import fr.kubys.card.LightweightSquadCard;
import fr.kubys.card.params.LightweightSquadCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Pawn;
import fr.kubys.repository.ChessBoardRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.d2;
import static fr.kubys.core.Position.e2;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MoveCardCommandTest {

    ChessBoardRepositoryImpl chessBoardRepository;

    @BeforeEach
    void setUp() {
        chessBoardRepository = new ChessBoardRepositoryImpl();
    }

    @Test
    @Disabled
    void card_parameter_should_act_as_immutable() {
        Integer gameId = getGameWithLightweightSquadCardInTHeHandOfCurrentPlayer();
        ChessBoardReadService chessBoardService = chessBoardRepository.getChessBoardService(gameId);
        LightweightSquadCardParam lightweightSquadCardParam = new LightweightSquadCardParam(getPawnOn(chessBoardService, e2), getPawnOn(chessBoardService, d2));
        LightweightSquadCard lightweightSquadCard = new LightweightSquadCard();
        chessBoardService.getCurrentPlayer().getCards().add(lightweightSquadCard);
        PlayCardCommand<LightweightSquadCardParam> command = PlayCardCommand.<LightweightSquadCardParam>builder()
                .gameId(gameId)
                .card(lightweightSquadCard)
                .parameters(lightweightSquadCardParam)
                .build();

        assertDoesNotThrow(() -> chessBoardRepository.saveCommand(command));
        assertDoesNotThrow(() -> chessBoardRepository.getChessBoardService(gameId));
    }

    private Integer getGameWithLightweightSquadCardInTHeHandOfCurrentPlayer() { // FIXME
        Integer newGame;
        do {
            newGame = chessBoardRepository.createNewGame();
        } while (chessBoardRepository.getChessBoardService(newGame).getCurrentPlayer().getCards().stream()
                .map(Card::getClass)
                .noneMatch(aClass -> aClass == LightweightSquadCard.class));
            return newGame;
    }

    private static Pawn getPawnOn(ChessBoardReadService chessBoardService, Position position) {
        return chessBoardService.getPieces().stream()
                .filter(Pawn.class::isInstance)
                .map(Pawn.class::cast)
                .filter(piece -> piece.getPosition()
                .equals(position)).findAny().orElseThrow();
    }
}