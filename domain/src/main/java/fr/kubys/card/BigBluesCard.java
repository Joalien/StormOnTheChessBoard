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
        if (param.piece() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (!(param.piece() instanceof Pawn))
            throw new IllegalArgumentException("Vous ne pouvez cibler qu'un Pion");
        if (!param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new IllegalArgumentException("Vous devez cibler un Pion adverse");
        if (chessBoard.getOutOfTheBoardPieces().contains(param.piece()))
            throw new IllegalArgumentException("Le Pion n'est pas sur le plateau");
        if (!allNeighborsEmpty(chessBoard, param.piece().getPosition()))
            throw new IllegalArgumentException("Toutes les cases voisines ne sont pas vides");
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
