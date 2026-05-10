package fr.kubys.repository;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.api.ChessBoardService;
import fr.kubys.board.ChessBoard;
import fr.kubys.command.Command;
import fr.kubys.command.StartGameCommand;
import fr.kubys.game.ChessBoardServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Supplier;

@Slf4j
@Repository
public class ChessBoardRepositoryImpl implements ChessBoardRepository {
    private final Map<Integer, List<Command>> store = new HashMap<>();
    private final Map<Integer, Supplier<ChessBoard>> boardFactories = new HashMap<>();

    @Override
    public Integer createNewGame() {
        return createCustomGame(GamePresets.INITIAL_STATE);
    }

    @Override
    public Integer createCustomGame(Supplier<ChessBoard> boardFactory) {
        Integer nextGameId = store.keySet().stream()
                .mapToInt(value -> value)
                .max()
                .orElse(0) + 1;
        store.put(nextGameId, new LinkedList<>());
        boardFactories.put(nextGameId, boardFactory);
        saveCommand(StartGameCommand.builder().gameId(nextGameId).build());
        return nextGameId;
    }

    @Override
    public void saveCommand(Command command) {
        ChessBoardService chessBoardWriteService = computeChessBoard(command.getGameId());

        command.execute(chessBoardWriteService);
        log.info("[Chessboard {}] {}", command.getGameId(), command);
        store.get(command.getGameId()).add(command);
    }

    @Override
    public ChessBoardReadService getChessBoardService(Integer gameId) {
        return computeChessBoard(gameId);
    }

    @Override
    public void undoLastCommand(Integer gameId) {
        int lastActionIndex = store.get(gameId).size() - 1;
        store.get(gameId).remove(lastActionIndex);
    }

    @Override
    public ChessBoardReadService simulate(Integer gameId, List<Command> hypothetical) {
        if (!store.containsKey(gameId)) throw new GameNotFoundException(gameId);
        Supplier<ChessBoard> factory = boardFactories.getOrDefault(gameId, GamePresets.INITIAL_STATE);
        ChessBoardService gameStateController = ChessBoardServiceFactory.newChessBoardService(factory);
        List.copyOf(store.get(gameId)).forEach(command -> command.execute(gameStateController));
        hypothetical.forEach(command -> command.execute(gameStateController));
        return gameStateController;
    }

    @Override
    public boolean gameExists(Integer gameId) {
        return store.containsKey(gameId);
    }

    @Override
    public Set<Integer> getGameIds() {
        return Set.copyOf(store.keySet());
    }

    private ChessBoardService computeChessBoard(Integer gameId) {
        if (!store.containsKey(gameId)) throw new GameNotFoundException(gameId);
        Supplier<ChessBoard> factory = boardFactories.getOrDefault(gameId, GamePresets.INITIAL_STATE);
        ChessBoardService gameStateController = ChessBoardServiceFactory.newChessBoardService(factory);
        List.copyOf(store.get(gameId)).forEach(command -> command.execute(gameStateController));
        return gameStateController;
    }
}