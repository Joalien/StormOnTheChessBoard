import api.ChessBoardReadService;
import api.ChessBoardService;
import api.ChessBoardServiceFactory;
import api.ChessBoardWriteService;
import command.Command;

import java.util.*;

public class ChessBoardRepositoryImpl implements ChessBoardRepository {
    private final Map<Integer, List<Command>> store = new HashMap<>();

    @Override
    public boolean saveCommand(Integer gameId, Command cbs) {
        ChessBoardWriteService chessBoardWriteService = computeChessBoard(gameId);

        if (cbs.execute(chessBoardWriteService)) {
            store.putIfAbsent(gameId, new LinkedList<>());
            return store.get(gameId).add(cbs);
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