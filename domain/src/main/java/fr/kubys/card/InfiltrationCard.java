package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.InfiltrationCardParam;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;

import java.util.List;
import java.util.Set;

public class InfiltrationCard extends Card<InfiltrationCardParam> {

    public InfiltrationCard() {
        super("Infiltration",
                "Sur l'échiquier, permutez 1, 2 ou 3 de vos pions avec des pions adverses.",
                CardType.REPLACE_TURN,
                InfiltrationCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, InfiltrationCardParam param) {
        Set<Piece> pawns = param.pawns();
        if (pawns == null || pawns.isEmpty())
            throw new IllegalStateException("Paramètre de carte manquant");

        pawns.forEach(p -> {
            if (!(p instanceof Pawn))
                throw new IllegalArgumentException("Les pièces sélectionnées doivent être des Pions");
        });

        long ownCount = pawns.stream().filter(p -> !p.getColor().cannotBeMovedBy(chessBoard.getCurrentTurn())).count();
        long enemyCount = pawns.stream().filter(p -> p.getColor().cannotBeMovedBy(chessBoard.getCurrentTurn())).count();

        if (ownCount != enemyCount)
            throw new IllegalArgumentException("Le nombre de pions alliés et adverses doit être identique");
        if (ownCount > 3)
            throw new IllegalArgumentException("Vous pouvez permuter au maximum 3 pions");
        if (ownCount == 0)
            throw new IllegalArgumentException("Vous devez sélectionner au moins un pion allié et un pion adverse");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, InfiltrationCardParam param) {
        Color currentTurn = chessBoard.getCurrentTurn();
        List<Piece> own = param.pawns().stream().filter(p -> !p.getColor().cannotBeMovedBy(currentTurn)).toList();
        List<Piece> enemy = param.pawns().stream().filter(p -> p.getColor().cannotBeMovedBy(currentTurn)).toList();

        Position[] ownPositions = own.stream().map(Piece::getPosition).toArray(Position[]::new);
        Position[] enemyPositions = enemy.stream().map(Piece::getPosition).toArray(Position[]::new);

        own.forEach(chessBoard::removePieceFromTheBoard);
        enemy.forEach(chessBoard::removePieceFromTheBoard);

        for (int i = 0; i < own.size(); i++) {
            chessBoard.add(own.get(i), enemyPositions[i]);
            chessBoard.add(enemy.get(i), ownPositions[i]);
        }
    }
}
