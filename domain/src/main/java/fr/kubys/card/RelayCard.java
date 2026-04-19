package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Piece;

import java.util.Set;

public class RelayCard extends Card<PieceToPositionCardParam> {

    public RelayCard() {
        super("Relais",
                "Déplacez l'une de vos pièces en lui faisant traverser toutes les cases de son chemin où se trouvent vos propres pièces. Vous ne pouvez pas prendre ainsi une pièce adverse.",
                CardType.REPLACE_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null)
            throw new IllegalStateException("Paramètre de carte manquant");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        if (param.piece() instanceof Knight)
            throw new IllegalArgumentException("Un Cavalier n'a pas de chemin à traverser");

        Position dest = param.positionToMoveOn();
        Piece piece = param.piece();

        // Destination must be empty (no capture allowed)
        if (chessBoard.at(dest).getPiece().isPresent())
            throw new IllegalArgumentException("La case de destination doit être vide (pas de capture avec cette carte)");

        // The move must follow the piece's normal movement rules (direction-wise)
        if (!piece.isPositionTheoreticallyReachable(dest))
            throw new IllegalArgumentException("La destination n'est pas accessible par cette pièce");

        // Path must contain only ally pieces or be empty (no enemy pieces on the path)
        Set<Position> pathSquares = piece.squaresOnThePath(dest);
        pathSquares.stream()
                .flatMap(pos -> chessBoard.at(pos).getPiece().stream())
                .filter(occupant -> occupant.getColor() != piece.getColor())
                .findFirst()
                .ifPresent(occupant -> {
                    throw new IllegalArgumentException("Une pièce adverse bloque le chemin en %s".formatted(occupant.getPosition()));
                });

        // The path must actually have ally pieces on it (otherwise normal move would work)
        boolean hasAllyOnPath = pathSquares.stream()
                .anyMatch(pos -> chessBoard.at(pos).getPiece()
                        .map(p -> p.getColor() == piece.getColor())
                        .orElse(false));
        if (!hasAllyOnPath)
            throw new IllegalArgumentException("Le chemin ne contient aucune pièce amie à traverser");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.fakeSquare(null, param.piece().getPosition());
        chessBoard.fakeSquare(param.piece(), param.positionToMoveOn());
        boolean check = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeAllSquares();
        return !check;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.at(param.piece().getPosition()).removePiece();
        param.piece().setPosition(null);
        chessBoard.add(param.piece(), param.positionToMoveOn());
    }
}
