package fr.kubys.ai;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.command.Command;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.core.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class StockfishStrategy implements AiStrategy {

    private static final Logger log = LoggerFactory.getLogger(StockfishStrategy.class);
    private static final long MOVE_TIME_MS = 1000;
    private static final long PROCESS_TIMEOUT_MS = 5000;

    private final String binaryPath;
    private final AiStrategy fallback;

    public StockfishStrategy(String binaryPath, AiStrategy fallback) {
        this.binaryPath = binaryPath;
        this.fallback = fallback;
    }

    @Override
    public List<Command> decideMove(Integer gameId, ChessBoardReadService boardState) {
        // We always try Stockfish, even when card effects or non-standard pieces are on the
        // board: an approximate Stockfish suggestion is far better than the dumb fallback,
        // and the post-call legality check catches illegal suggestions cleanly. Custom
        // pieces are mapped to their nearest standard equivalent in FenConverter (Kangaroo
        // and Crab → knight, FusedPiece → queen) so the FEN stays valid.
        int turn = boardState.getTurnNumber();
        try {
            String fen = FenConverter.toFen(boardState);
            log.info("[AI Game {} turn {}] Asking Fairy-Stockfish for position: {}", gameId, turn, fen);
            String bestMove = queryStockfish(fen);

            if (bestMove == null) {
                log.warn("[AI Game {} turn {}] Stockfish returned no move, using fallback", gameId, turn);
                return fallback.decideMove(gameId, boardState);
            }

            Position from = Position.valueOf(bestMove.substring(0, 2));
            Position to = Position.valueOf(bestMove.substring(2, 4));

            // Validate that the move is legal in our engine. Effects (Barricade, Bombing…)
            // and non-standard pieces can make Stockfish's suggestion illegal here; in that
            // case we fall back rather than committing an invalid command.
            Set<Position> legalMoves = boardState.getLegalMoves(from);
            if (!legalMoves.contains(to)) {
                log.info("[AI Game {} turn {}] Stockfish move {}→{} not legal in our engine, using fallback", gameId, turn, from, to);
                return fallback.decideMove(gameId, boardState);
            }

            log.info("[AI Game {} turn {}] Fairy-Stockfish plays {}→{}", gameId, turn, from, to);
            return List.of(
                    PlayMoveCommand.builder().gameId(gameId).from(from).to(to).build(),
                    EndTurnCommand.builder().gameId(gameId).build()
            );
        } catch (Exception e) {
            log.error("[AI Game {} turn {}] Stockfish error: {}, using fallback", gameId, turn, e.getMessage());
            return fallback.decideMove(gameId, boardState);
        }
    }

    private String queryStockfish(String fen) throws IOException, InterruptedException, TimeoutException {
        Process process = new ProcessBuilder(binaryPath)
                .redirectErrorStream(true)
                .start();

        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            writer.write("uci\n");
            writer.write("isready\n");
            writer.flush();
            waitForLine(reader, "readyok");

            writer.write("position fen " + fen + "\n");
            writer.write("go movetime " + MOVE_TIME_MS + "\n");
            writer.flush();

            String bestMoveLine = waitForLine(reader, "bestmove");
            if (bestMoveLine == null) return null;

            // Parse "bestmove e2e4 ponder d7d5" or "bestmove e2e4"
            String[] parts = bestMoveLine.split("\\s+");
            if (parts.length >= 2 && "bestmove".equals(parts[0])) {
                String move = parts[1];
                if ("(none)".equals(move)) return null;
                return move;
            }
            return null;
        } finally {
            process.destroyForcibly();
            process.waitFor(1, TimeUnit.SECONDS);
        }
    }

    private String waitForLine(BufferedReader reader, String prefix) throws IOException, TimeoutException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<String> future = executor.submit(() -> {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(prefix)) return line;
                }
                return null;
            });
            return future.get(PROCESS_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new TimeoutException("Stockfish timed out waiting for: " + prefix);
        } catch (ExecutionException e) {
            throw new IOException("Error reading Stockfish output", e.getCause());
        } finally {
            executor.shutdownNow();
        }
    }
}
