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
        if (!boardState.getEffects().isEmpty() || FenConverter.hasNonStandardPieces(boardState)) {
            log.info("[AI Game {}] Non-standard position detected, using fallback strategy", gameId);
            return fallback.decideMove(gameId, boardState);
        }

        try {
            String fen = FenConverter.toFen(boardState);
            log.info("[AI Game {}] Asking Fairy-Stockfish for position: {}", gameId, fen);
            String bestMove = queryStockfish(fen);

            if (bestMove == null) {
                log.warn("[AI Game {}] Stockfish returned no move, using fallback", gameId);
                return fallback.decideMove(gameId, boardState);
            }

            Position from = Position.valueOf(bestMove.substring(0, 2));
            Position to = Position.valueOf(bestMove.substring(2, 4));

            // Validate that the move is legal in our engine
            Set<Position> legalMoves = boardState.getLegalMoves(from);
            if (!legalMoves.contains(to)) {
                log.warn("[AI Game {}] Stockfish move {}→{} is illegal in our engine, using fallback", gameId, from, to);
                return fallback.decideMove(gameId, boardState);
            }

            log.info("[AI Game {}] Fairy-Stockfish plays {}→{}", gameId, from, to);
            return List.of(
                    PlayMoveCommand.builder().gameId(gameId).from(from).to(to).build(),
                    EndTurnCommand.builder().gameId(gameId).build()
            );
        } catch (Exception e) {
            log.error("[AI Game {}] Stockfish error: {}, using fallback", gameId, e.getMessage());
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
