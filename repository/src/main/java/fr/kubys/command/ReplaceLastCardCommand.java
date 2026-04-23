package fr.kubys.command;

import fr.kubys.api.ChessBoardService;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public final class ReplaceLastCardCommand extends Command {

    String cardClassName;

    @Override
    public void execute(ChessBoardService chessBoardWriteService) {
        chessBoardWriteService.replaceLastCard(cardClassName);
    }

    @Override
    public String toString() {
        return "Replace last card with %s".formatted(cardClassName);
    }
}
