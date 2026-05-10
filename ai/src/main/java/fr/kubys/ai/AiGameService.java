package fr.kubys.ai;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.api.GameResult;
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
    private final GameCommandExecutor commandExecutor;
    private final Executor aiExecutor;

    @Autowired
    public AiGameService(ChessBoardRepository chessBoardRepository) {
        this(chessBoardRepository, chessBoardRepository::saveCommand, defaultExecutor());
    }

    AiGameService(ChessBoardRepository chessBoardRepository, GameCommandExecutor commandExecutor, Executor aiExecutor) {
        this.chessBoardRepository = chessBoardRepository;
        this.commandExecutor = commandExecutor;
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
        if (boardState.getGameResult() != GameResult.ONGOING) {
            log.info("[AI Game {} turn {}] Game is over ({}), AI will not play",
                    gameId, boardState.getTurnNumber(), boardState.getGameResult());
            return false;
        }

        if (boardState.getCurrentPlayer().getColor() == config.aiColor()) {
            return playOwnTurn(gameId, config, boardState);
        }
        return playEnemyReaction(gameId, config, boardState);
    }

    private boolean playOwnTurn(Integer gameId, AiGameConfig config, ChessBoardReadService boardState) {
        int turn = boardState.getTurnNumber();
        log.info("[AI Game {} turn {}] AI ({}) is thinking...", gameId, turn, config.aiColor());
        List<Command> commands = config.strategy().decideMove(gameId, boardState);

        // Re-check after the (potentially long) think: if the user undid the end-turn
        // in the meantime, the state is no longer AI's and we must discard the move.
        ChessBoardReadService postState = chessBoardRepository.getChessBoardService(gameId);
        if (postState.getCurrentPlayer().getColor() != config.aiColor()) {
            log.info("[AI Game {} turn {}] Turn changed during computation, discarding {} command(s)",
                    gameId, turn, commands.size());
            return false;
        }

        for (Command command : commands) {
            commandExecutor.execute(command);
        }
        log.info("[AI Game {} turn {}] AI played {} command(s)", gameId, turn, commands.size());
        return true;
    }

    /**
     * Asks the strategy whether any ENEMY_TURN card in the AI's hand should be played in
     * reaction to the human's last move/card. Re-checks the state after the (potentially
     * long) thinking phase to avoid committing into a stale window — if the human ended
     * their turn meanwhile, the AI's regular {@link #playOwnTurn} path will run on the
     * next scheduled tick.
     */
    private boolean playEnemyReaction(Integer gameId, AiGameConfig config, ChessBoardReadService boardState) {
        int turn = boardState.getTurnNumber();
        List<Command> commands = config.strategy().decideEnemyReaction(gameId, boardState, config.aiColor());
        if (commands.isEmpty()) return false;

        ChessBoardReadService postState = chessBoardRepository.getChessBoardService(gameId);
        if (postState.getCurrentPlayer().getColor() == config.aiColor()) {
            log.info("[AI Game {} turn {}] Turn switched to AI before enemy reaction could commit, discarding {} command(s)",
                    gameId, turn, commands.size());
            return false;
        }
        if (!postState.getCurrentStateName().equals(boardState.getCurrentStateName())) {
            log.info("[AI Game {} turn {}] State changed ({} → {}) before enemy reaction could commit, discarding",
                    gameId, turn, boardState.getCurrentStateName(), postState.getCurrentStateName());
            return false;
        }

        for (Command command : commands) {
            commandExecutor.execute(command);
        }
        log.info("[AI Game {} turn {}] AI played {} enemy-reaction command(s)", gameId, turn, commands.size());
        return true;
    }

    public Optional<Color> getAiColor(Integer gameId) {
        return Optional.ofNullable(aiGames.get(gameId)).map(AiGameConfig::aiColor);
    }
}
