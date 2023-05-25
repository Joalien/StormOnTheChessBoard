package fr.kubys.repository;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.api.ChessBoardService;
import fr.kubys.command.Command;
import fr.kubys.game.ChessBoardServiceFactory;
import fr.kubys.api.ChessBoardWriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class ChessBoardRepositoryImpl implements ChessBoardRepository {
    private final Map<Integer, List<Command>> store = new HashMap<>();

    @Override
    public boolean saveCommand(Command cbs) {
        ChessBoardWriteService chessBoardWriteService = computeChessBoard(cbs.getGameId());

        if (cbs.execute(chessBoardWriteService)) {
            store.putIfAbsent(cbs.getGameId(), new LinkedList<>());
            log.info("{} has been played on chessboard {}", cbs, cbs.getGameId());
            return store.get(cbs.getGameId()).add(cbs);
        } else return false;
    }

    @Override
    public ChessBoardReadService getChessBoardService(Integer gameId) {
        if (!store.containsKey(gameId)) throw new IllegalArgumentException("game %s not found".formatted(gameId));

        return computeChessBoard(gameId);
    }

    @Override
    public boolean undoLastCommand(Integer gameId) {
        throw new UnsupportedOperationException("Not implemented yet but easy to do ;)");
    }

    private ChessBoardService computeChessBoard(Integer gameId) {
        ChessBoardService gameStateController = ChessBoardServiceFactory.newChessBoardService();
        store.getOrDefault(gameId, Collections.emptyList())
                .forEach(command -> command.execute(gameStateController));
        return gameStateController;
    }
}