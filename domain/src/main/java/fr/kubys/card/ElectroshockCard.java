package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.Bishop;
import fr.kubys.piece.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ElectroshockCard extends Card<NoCardParam> {

    public ElectroshockCard() {
        super("Electrochoc",
                "Tous les Fous qui peuvent l'être doivent être déplacés horizontalement ou verticalement d'une case, sans prendre de pièce. Votre adversaire déplace ses Fous en premier.",
                CardType.AFTER_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        boolean anyBishopCanMove = chessBoard.getPieces().stream()
                .filter(p -> p instanceof Bishop)
                .anyMatch(bishop -> !findOrthogonalMoves(chessBoard, bishop).isEmpty());
        if (!anyBishopCanMove)
            throw new IllegalArgumentException("Aucun Fou ne peut être déplacé orthogonalement");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, NoCardParam param) {
        // Auto-move card; check is verified implicitly
        return true;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        Color currentTurn = chessBoard.getCurrentTurn();
        Color enemy = currentTurn.opposite();

        // Move enemy bishops first, then ally bishops
        moveBishopsOfColor(chessBoard, enemy);
        moveBishopsOfColor(chessBoard, currentTurn);
    }

    private void moveBishopsOfColor(ChessBoard chessBoard, Color color) {
        List<Piece> bishops = new ArrayList<>(chessBoard.getPieces().stream()
                .filter(p -> p instanceof Bishop && p.getColor() == color)
                .toList());

        for (Piece bishop : bishops) {
            if (bishop.getPosition() == null) continue; // already removed
            List<Position> moves = findOrthogonalMoves(chessBoard, bishop);
            if (!moves.isEmpty()) {
                Position dest = moves.get(0);
                chessBoard.at(bishop.getPosition()).removePiece();
                bishop.setPosition(null);
                chessBoard.add(bishop, dest);
            }
        }
    }

    static List<Position> findOrthogonalMoves(ChessBoard chessBoard, Piece bishop) {
        List<Position> moves = new ArrayList<>();
        Position pos = bishop.getPosition();
        int file = pos.getFile().getFileNumber();
        int row = pos.getRow().getRowNumber();

        // Try up, right, down, left
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] dir : directions) {
            int newFile = file + dir[0];
            int newRow = row + dir[1];
            if (newFile >= 1 && newFile <= 8 && newRow >= 1 && newRow <= 8) {
                Position dest = Position.posToSquare(File.fromNumber(newFile), Row.fromNumber(newRow));
                if (chessBoard.at(dest).getPiece().isEmpty()) {
                    moves.add(dest);
                }
            }
        }
        return moves;
    }
}
