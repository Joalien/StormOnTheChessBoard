package fr.kubys.command;

import fr.kubys.api.ChessBoardService;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Random;

@SuperBuilder
@Getter
public final class StartGameCommand extends Command {
    private final long seed = new Random().nextLong();

    @Override
    public void execute(ChessBoardService chessBoardWriteService) {
        chessBoardWriteService.startGame(seed);
    }

    @Override
    public String toString() {
        return "Start game";
    }
}
