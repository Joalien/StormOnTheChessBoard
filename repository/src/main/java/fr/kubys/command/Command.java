package fr.kubys.command;

import fr.kubys.api.ChessBoardService;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@SuperBuilder
public abstract sealed class Command permits EndTurnCommand, PlayMoveCommand, StartGameCommand, PlayCardWithImmutableParamCommand {
    protected Integer gameId;
    @Builder.Default
    protected Instant instant = Instant.now();

    public abstract void execute(ChessBoardService chessBoardWriteService);
}
