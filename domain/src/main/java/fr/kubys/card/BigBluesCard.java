package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.Pawn;

import java.util.Optional;

public class BigBluesCard extends Card<PieceCardParam> {

    public BigBluesCard() {
        super("Grosse Déprime",
                "Retirez de l'échiquier un Pion adverse dont toutes les cases voisines sont vides.",
                CardType.AFTER_TURN,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException("Missing required card parameter");
        if (!(param.piece() instanceof Pawn))
            throw new IllegalArgumentException("You can only target a Pawn");
        if (!param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new IllegalArgumentException("You must target an enemy Pawn");
        if (chessBoard.getOutOfTheBoardPieces().contains(param.piece()))
            throw new IllegalArgumentException("Pawn is not on the board");
        if (!allNeighborsEmpty(chessBoard, param.piece().getPosition()))
            throw new IllegalArgumentException("Not all neighboring squares are empty");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceCardParam param) {
        chessBoard.fakeSquare(null, param.piece().getPosition());
        boolean check = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeSquare(param.piece().getPosition());
        return !check;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        chessBoard.removePieceFromTheBoard(param.piece());
    }

    private boolean allNeighborsEmpty(ChessBoard chessBoard, Position pos) {
        for (int df = -1; df <= 1; df++) {
            for (int dr = -1; dr <= 1; dr++) {
                if (df == 0 && dr == 0) continue;
                Optional<File> file = safeFile(pos.getFile(), df);
                Optional<Row> row = safeRow(pos.getRow(), dr);
                if (file.isPresent() && row.isPresent()) {
                    Position neighbor = Position.posToSquare(file.get(), row.get());
                    if (chessBoard.at(neighbor).getPiece().isPresent()) return false;
                }
            }
        }
        return true;
    }

    private Optional<File> safeFile(File f, int delta) {
        int target = f.getFileNumber() + delta;
        if (target < 1 || target > 8) return Optional.empty();
        return Optional.of(java.util.Arrays.stream(File.values())
                .filter(file -> file.getFileNumber() == target)
                .findFirst().orElseThrow());
    }

    private Optional<Row> safeRow(Row r, int delta) {
        if (delta == 0) return Optional.of(r);
        if (delta > 0) return r.next();
        return r.previous();
    }
}
