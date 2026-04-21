package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.King;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;

import java.util.Optional;

public class PushCard extends Card<PieceCardParam> {

    public PushCard() {
        super("Poussée",
                "L'un de vos pions avance sur une case occupée, en poussant devant lui la pièce qui s'y trouve. Une pièce poussée hors de l'échiquier est retirée du jeu. Le Roi ne peut pas être poussé.",
                CardType.REPLACE_TURN,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException("Paramètre de carte manquant");
        Piece piece = param.piece();
        if (!(piece instanceof Pawn pawn))
            throw new IllegalArgumentException("La pièce sélectionnée doit être un pion");
        if (piece.getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(piece.getColor());

        Position forward = pawn.oneSquareForward()
                .orElseThrow(() -> new IllegalArgumentException("Le pion ne peut pas avancer"));
        Piece occupant = chessBoard.at(forward).getPiece()
                .orElseThrow(() -> new IllegalArgumentException("La case devant le pion est vide"));
        if (occupant instanceof King)
            throw new IllegalArgumentException("Le Roi ne peut pas être poussé");

        pawn.twoSquaresForward().ifPresent(pushDest ->
                chessBoard.at(pushDest).getPiece().ifPresent(p -> {
                    throw new IllegalArgumentException("La case de destination de la pièce poussée est occupée");
                }));
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        Pawn pawn = (Pawn) param.piece();
        Position forward = pawn.oneSquareForward().orElseThrow();
        Piece occupant = chessBoard.at(forward).getPiece().orElseThrow();
        Optional<Position> pushDest = pawn.twoSquaresForward();

        chessBoard.removePieceFromTheBoard(occupant);
        chessBoard.move(pawn, forward);
        pushDest.ifPresent(dest -> chessBoard.add(occupant, dest));
    }
}
