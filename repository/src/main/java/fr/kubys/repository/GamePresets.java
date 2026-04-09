package fr.kubys.repository;

import fr.kubys.board.ChessBoard;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.King;
import fr.kubys.piece.WhitePawn;

import java.util.List;
import java.util.function.Supplier;

import static fr.kubys.core.Position.*;

public class GamePresets {

    public static final Supplier<ChessBoard> INITIAL_STATE = ChessBoard::createWithInitialState;

    public static Integer createKingsWithPawnsGame(ChessBoardRepository repository) {
        return repository.createCustomGame(() -> {
            ChessBoard board = ChessBoard.createEmpty();
            board.add(new King(Color.WHITE), e1);
            board.add(new King(Color.BLACK), e8);
            board.add(new WhitePawn(), a6);
            board.add(new WhitePawn(), b6);
            return board;
        });
    }

    public static Integer createSicilianGame(ChessBoardRepository repository) {
        Integer gameId = repository.createCustomGame(INITIAL_STATE);
        List.of(
                List.of(e2, e4), List.of(c7, c5),
                List.of(g1, f3), List.of(d7, d6),
                List.of(d2, d4), List.of(c5, d4),
                List.of(f3, d4), List.of(g8, f6),
                List.of(c1, e3), List.of(g7, g6),
                List.of(b1, c3), List.of(f8, g7),
                List.of(d1, d2), List.of(e8, g8),
                List.of(e1, c1), List.of(a7, a6)
        ).forEach(m -> saveMove(repository, gameId, m.get(0), m.get(1)));
        return gameId;
    }

    private static void saveMove(ChessBoardRepository repository, Integer gameId, Position from, Position to) {
        repository.saveCommand(PlayMoveCommand.builder().gameId(gameId).from(from).to(to).build());
        repository.saveCommand(EndTurnCommand.builder().gameId(gameId).build());
    }
}
