package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.Piece;

import java.util.Arrays;

public class CastleAndRollCard extends Card<NoCardParam> {

    public CastleAndRollCard() {
        super("Château Fort",
                "Si aucune pièce adverse ne se trouve sur votre première rangée, vous pouvez réarranger comme vous le souhaitez toutes vos pièces qui s'y trouvent.",
                CardType.REPLACE_TURN,
                NoCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, NoCardParam param) {
        Color currentColor = chessBoard.getCurrentTurn();
        Row homeRow = currentColor.homeRow();

        boolean enemyOnHomeRow = Arrays.stream(File.values())
                .map(file -> Position.posToSquare(file, homeRow))
                .flatMap(pos -> chessBoard.at(pos).getPiece().stream())
                .anyMatch(piece -> piece.getColor() != currentColor);

        if (enemyOnHomeRow)
            throw new IllegalArgumentException("Il y a une pièce adverse sur votre première rangée");

        boolean anyAllyOnHomeRow = Arrays.stream(File.values())
                .map(file -> Position.posToSquare(file, homeRow))
                .anyMatch(pos -> chessBoard.at(pos).getPiece()
                        .map(p -> p.getColor() == currentColor)
                        .orElse(false));

        if (!anyAllyOnHomeRow)
            throw new IllegalArgumentException("Aucune de vos pièces ne se trouve sur votre première rangée");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, NoCardParam param) {
        throw new UnsupportedOperationException("Le réarrangement nécessite une interface spécifique non encore implémentée");
    }
}
