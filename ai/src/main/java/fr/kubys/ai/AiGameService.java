package fr.kubys.ai;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.command.Command;
import fr.kubys.core.Color;
import fr.kubys.repository.ChessBoardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class AiGameService {

    private static final Logger log = LoggerFactory.getLogger(AiGameService.class);
    private final Map<Integer, AiGameConfig> aiGames = new ConcurrentHashMap<>();
    private final ChessBoardRepository chessBoardRepository;
    private final Executor aiExecutor;

    @Autowired
    public AiGameService(ChessBoardRepository chessBoardRepository) {
        this(chessBoardRepository, defaultExecutor());
    }

    AiGameService(ChessBoardRepository chessBoardRepository, Executor aiExecutor) {
        this.chessBoardRepository = chessBoardRepository;
        this.aiExecutor = aiExecutor;
    }

    private static Executor defaultExecutor() {
        return Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "ai-game-worker");
            t.setDaemon(true);
            return t;
        });
    }

    public void registerAiGame(Integer gameId, Color aiColor, AiStrategy strategy) {
        aiGames.put(gameId, new AiGameConfig(aiColor, strategy));
    }

    public boolean isAiGame(Integer gameId) {
        return aiGames.containsKey(gameId);
    }

    public void schedulePlayIfAiTurn(Integer gameId, Runnable afterCommit) {
        aiExecutor.execute(() -> {
            try {
                if (playIfAiTurn(gameId)) afterCommit.run();
            } catch (Exception e) {
                log.error("[AI Game {}] async AI move failed: {}", gameId, e.getMessage(), e);
            }
        });
    }

    public boolean playIfAiTurn(Integer gameId) {
        AiGameConfig config = aiGames.get(gameId);
        if (config == null) return false;

        ChessBoardReadService boardState = chessBoardRepository.getChessBoardService(gameId);
        if (boardState.getCurrentPlayer().getColor() != config.aiColor()) return false;

        log.info("[AI Game {}] AI ({}) is thinking...", gameId, config.aiColor());
        List<Command> commands = config.strategy().decideMove(gameId, boardState);

        for (Command command : commands) {
            chessBoardRepository.saveCommand(command);
        }
        log.info("[AI Game {}] AI played {} command(s)", gameId, commands.size());
        return true;
    }

    public Optional<Color> getAiColor(Integer gameId) {
        return Optional.ofNullable(aiGames.get(gameId)).map(AiGameConfig::aiColor);
    }
}
