package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.King;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Rock;

import java.util.HashSet;
import java.util.Set;

public class AcrobaticCastlingCard extends Card<PieceCardParam> {

    public AcrobaticCastlingCard() {
        super("Roque Acrobatique",
                "Quels que soient les antécédents et la situation actuelle, si vous avez votre Roi et l'une de vos Tours sur une même rangée ou colonne, et s'il n'y a aucune pièce entre eux, vous pouvez roquer en plaçant la Tour à côté du Roi, puis en faisant passer le Roi par dessus la Tour.",
                CardType.REPLACE_TURN,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException("Paramètre de carte manquant");
        if (!(param.piece() instanceof Rock))
            throw new IllegalArgumentException("Vous devez sélectionner une Tour");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());

        Piece king = findKing(chessBoard);
        Rock rock = (Rock) param.piece();

        if (!onSameLineOrColumn(king, rock))
            throw new IllegalArgumentException("La Tour et le Roi doivent être sur la même rangée ou colonne");
        if (!noPiecesBetween(chessBoard, king.getPosition(), rock.getPosition()))
            throw new IllegalArgumentException("Il ne doit y avoir aucune pièce entre le Roi et la Tour");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceCardParam param) {
        Piece king = findKing(chessBoard);
        Rock rock = (Rock) param.piece();
        Position rockDest = computeRockDestination(king, rock);
        Position kingDest = computeKingDestination(king, rockDest);

        chessBoard.fakeSquare(null, king.getPosition());
        chessBoard.fakeSquare(null, rock.getPosition());
        chessBoard.fakeSquare(rock, rockDest);
        chessBoard.fakeSquare(king, kingDest);
        boolean check = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeAllSquares();
        return !check;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        Piece king = findKing(chessBoard);
        Rock rock = (Rock) param.piece();
        Position rockDest = computeRockDestination(king, rock);
        Position kingDest = computeKingDestination(king, rockDest);

        chessBoard.at(rock.getPosition()).removePiece();
        rock.setPosition(null);
        chessBoard.at(king.getPosition()).removePiece();
        king.setPosition(null);

        chessBoard.add(rock, rockDest);
        chessBoard.add(king, kingDest);
    }

    private Piece findKing(ChessBoard chessBoard) {
        return chessBoard.allyPieces(chessBoard.getCurrentTurn()).stream()
                .filter(Piece::isKing)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Pas de Roi trouvé"));
    }

    private boolean onSameLineOrColumn(Piece king, Piece rock) {
        return king.getFile() == rock.getFile() || king.getRow() == rock.getRow();
    }

    private boolean noPiecesBetween(ChessBoard chessBoard, Position a, Position b) {
        Set<Position> between = getPositionsBetween(a, b);
        return between.stream().allMatch(pos -> chessBoard.at(pos).getPiece().isEmpty());
    }

    static Set<Position> getPositionsBetween(Position a, Position b) {
        Set<Position> positions = new HashSet<>();
        if (a.getFile() == b.getFile()) {
            int minRow = Math.min(a.getRow().getRowNumber(), b.getRow().getRowNumber());
            int maxRow = Math.max(a.getRow().getRowNumber(), b.getRow().getRowNumber());
            for (int r = minRow + 1; r < maxRow; r++) {
                positions.add(Position.posToSquare(a.getFile(), Row.fromNumber(r)));
            }
        } else if (a.getRow() == b.getRow()) {
            int minFile = Math.min(a.getFile().getFileNumber(), b.getFile().getFileNumber());
            int maxFile = Math.max(a.getFile().getFileNumber(), b.getFile().getFileNumber());
            for (int f = minFile + 1; f < maxFile; f++) {
                positions.add(Position.posToSquare(File.fromNumber(f), a.getRow()));
            }
        }
        return positions;
    }

    // Rock moves next to King, on the side closest to the Rock
    static Position computeRockDestination(Piece king, Piece rock) {
        if (king.getFile() == rock.getFile()) {
            // Same column
            if (rock.getRow().getRowNumber() > king.getRow().getRowNumber()) {
                return Position.posToSquare(king.getFile(), Row.fromNumber(king.getRow().getRowNumber() + 1));
            } else {
                return Position.posToSquare(king.getFile(), Row.fromNumber(king.getRow().getRowNumber() - 1));
            }
        } else {
            // Same row
            if (rock.getFile().getFileNumber() > king.getFile().getFileNumber()) {
                return Position.posToSquare(File.fromNumber(king.getFile().getFileNumber() + 1), king.getRow());
            } else {
                return Position.posToSquare(File.fromNumber(king.getFile().getFileNumber() - 1), king.getRow());
            }
        }
    }

    // King jumps over the Rock (to the other side)
    static Position computeKingDestination(Piece king, Position rockDest) {
        if (king.getFile() == rockDest.getFile()) {
            // Same column
            if (rockDest.getRow().getRowNumber() > king.getRow().getRowNumber()) {
                return Position.posToSquare(king.getFile(), Row.fromNumber(rockDest.getRow().getRowNumber() + 1));
            } else {
                return Position.posToSquare(king.getFile(), Row.fromNumber(rockDest.getRow().getRowNumber() - 1));
            }
        } else {
            // Same row
            if (rockDest.getFile().getFileNumber() > king.getFile().getFileNumber()) {
                return Position.posToSquare(File.fromNumber(rockDest.getFile().getFileNumber() + 1), king.getRow());
            } else {
                return Position.posToSquare(File.fromNumber(rockDest.getFile().getFileNumber() - 1), king.getRow());
            }
        }
    }
}
