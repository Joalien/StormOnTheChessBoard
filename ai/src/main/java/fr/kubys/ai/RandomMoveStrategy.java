package fr.kubys.ai;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.command.Command;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.core.Position;

import java.util.List;
import java.util.Random;

public class RandomMoveStrategy implements AiStrategy {

    private final Random random;

    public RandomMoveStrategy() {
        this.random = new Random();
    }

    public RandomMoveStrategy(Random random) {
        this.random = random;
    }

    @Override
    public List<Command> decideMove(Integer gameId, ChessBoardReadService boardState) {
        List<Move> allMoves = boardState.getPieces().stream()
                .filter(piece -> piece.getColor() == boardState.getCurrentPlayer().getColor())
                .flatMap(piece -> boardState.getLegalMoves(piece.getPosition()).stream()
                        .map(target -> new Move(piece.getPosition(), target)))
                .toList();

        if (allMoves.isEmpty()) {
            return List.of(EndTurnCommand.builder().gameId(gameId).build());
        }

        Move chosen = allMoves.get(random.nextInt(allMoves.size()));
        return List.of(
                PlayMoveCommand.builder().gameId(gameId).from(chosen.from()).to(chosen.to()).build(),
                EndTurnCommand.builder().gameId(gameId).build()
        );
    }

    private record Move(Position from, Position to) {
    }
}
