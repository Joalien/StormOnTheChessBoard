package fr.kubys.repository;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.*;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.*;
import fr.kubys.piece.extra.Crab;
import fr.kubys.piece.extra.Kangaroo;

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
            board.add(new Pawn(Color.WHITE), a6);
            board.add(new Pawn(Color.WHITE), b6);
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

    /**
     * Creates a game with many persistent effects already active on the board:
     * Barricade, Trou noir, Attentat, Bouche d'égout, Magnétisme, plus a Kangaroo piece.
     * Pieces are developed in a Sicilian-like structure.
     * Useful as a starting point for testing new cards in an effect-rich environment.
     */
    public static Integer createEffectShowcaseGame(ChessBoardRepository repository) {
        Integer gameId = repository.createCustomGame(() -> {
            ChessBoard board = ChessBoard.createEmpty();

            // White pieces - Sicilian-like development
            board.add(new King(Color.WHITE), c1);
            board.add(new Queen(Color.WHITE), d2);
            board.add(new Rock(Color.WHITE), d1);
            board.add(new Rock(Color.WHITE), h1);
            board.add(new Kangaroo(Color.WHITE), c7);
            board.add(new Knight(Color.WHITE), c3);
            board.add(new Bishop(Color.WHITE), e3);
            board.add(new Pawn(Color.WHITE), a2);
            board.add(new Pawn(Color.WHITE), b2);
            board.add(new Pawn(Color.WHITE), c2);
            board.add(new Pawn(Color.WHITE), e4);
            board.add(new Pawn(Color.WHITE), f2);
            board.add(new Pawn(Color.WHITE), g2);
            board.add(new Pawn(Color.WHITE), h2);

            // Black pieces - Sicilian Dragon
            board.add(new King(Color.BLACK), g8);
            board.add(new Queen(Color.BLACK), a5);
            board.add(new Rock(Color.BLACK), f8);
            board.add(new Rock(Color.BLACK), d8);
            board.add(new Knight(Color.BLACK), f6);
            board.add(new Bishop(Color.BLACK), g7);
            board.add(new Bishop(Color.BLACK), e6);
            board.add(new Pawn(Color.BLACK), a7);
            board.add(new Crab(Color.BLACK), b7);
            board.add(new Pawn(Color.BLACK), d6);
            board.add(new Pawn(Color.BLACK), e5);
            board.add(new Pawn(Color.BLACK), f7);
            board.add(new Pawn(Color.BLACK), g6);
            board.add(new Pawn(Color.BLACK), h7);

            // Neutral piece (bishop on e6 made neutral via NeutralityCard effect)
            board.at(e6).getPiece().orElseThrow().setColor(Color.NONE);
            board.at(a5).getPiece().orElseThrow().setColor(Color.NONE);
            board.at(c3).getPiece().orElseThrow().setColor(Color.NONE);
            board.at(c7).getPiece().orElseThrow().setColor(Color.NONE);
            board.at(b7).getPiece().orElseThrow().setColor(Color.NONE);
            board.at(h1).getPiece().orElseThrow().setColor(Color.NONE);
            board.at(g6).getPiece().orElseThrow().setColor(Color.NONE);

            // === Persistent Effects ===

            // Barricade: wall between e3-e4 and f3-f4 (blocks central files)
            board.addEffect(new BarricadeEffect(e3, e4, f3, f4));

            // Black Hole on b5 (blocks a key square)
            board.addEffect(new BlackHoleEffect(b5));

            // Bombing on c4 (trap for black pieces approaching queenside)
            board.addEffect(new BombingEffect(c4, Color.WHITE));

            // Manholes connecting a1 and h8 (teleportation tunnel)
            board.addEffect(new ManHoleEffect(a1, h8));

            // Magnetism on black knight f6 (freezes nearby pieces)
            Piece blackKnight = board.at(f6).getPiece().orElseThrow();
            board.addEffect(new MagnetismEffect(blackKnight));

            return board;
        });

        // Play a few moves so the game is mid-turn and ready for testing
        List.of(
                List.of(a2, a3), List.of(h7, h5),
                List.of(h2, h3), List.of(a7, a6)
        ).forEach(m -> saveMove(repository, gameId, m.get(0), m.get(1)));

        return gameId;
    }

    private static void saveMove(ChessBoardRepository repository, Integer gameId, Position from, Position to) {
        repository.saveCommand(PlayMoveCommand.builder().gameId(gameId).from(from).to(to).build());
        repository.saveCommand(EndTurnCommand.builder().gameId(gameId).build());
    }
}
