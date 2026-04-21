package fr.kubys.ai;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.command.Command;
import fr.kubys.core.Color;
import fr.kubys.repository.ChessBoardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AiGameService {

    private static final Logger log = LoggerFactory.getLogger(AiGameService.class);
    private final Map<Integer, AiGameConfig> aiGames = new ConcurrentHashMap<>();
    private final ChessBoardRepository chessBoardRepository;

    public AiGameService(ChessBoardRepository chessBoardRepository) {
        this.chessBoardRepository = chessBoardRepository;
    }

    public void registerAiGame(Integer gameId, Color aiColor, AiStrategy strategy) {
        aiGames.put(gameId, new AiGameConfig(aiColor, strategy));
    }

    public boolean isAiGame(Integer gameId) {
        return aiGames.containsKey(gameId);
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
