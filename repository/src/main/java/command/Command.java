package command;

import api.ChessBoardWriteService;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@SuperBuilder
public abstract class Command {
    protected Integer gameId;
    @Builder.Default
    protected Instant instant = Instant.now();

    public abstract boolean execute(ChessBoardWriteService chessBoardWriteService);
}
