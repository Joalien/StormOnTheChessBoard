package fr.kubys.command;

import fr.kubys.api.ChessBoardService;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public final class ShuffleCommand extends Command {

    @Override
    public void execute(ChessBoardService chessBoardWriteService) {
        chessBoardWriteService.shuffleHand();
    }

    @Override
    public String toString() {
        return "Shuffle hand";
    }
}
