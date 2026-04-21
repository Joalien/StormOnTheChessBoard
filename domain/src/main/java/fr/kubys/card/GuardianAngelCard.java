package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.util.Set;

public class GuardianAngelCard extends Card<PieceToPositionCardParam> {

    public GuardianAngelCard() {
        super("Ange Gardien",
                "Jouez cette carte lorsque vous êtes en échec, même mat. Vous reprenez alors l'une des pièces que vous a prises votre adversaire pour l'intercaler sur une case libre entre votre Roi et la pièce qui le menace.",
                CardType.ENEMY_TURN_AFTER_MOVE,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null)
            throw new IllegalStateException("Paramètre de carte manquant");
        if (!chessBoard.getOutOfTheBoardPieces().contains(param.piece()))
            throw new IllegalArgumentException("La pièce doit avoir été capturée");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        if (!chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn()))
            throw new IllegalArgumentException("Vous devez être en échec pour jouer cette carte");
        if (chessBoard.at(param.positionToMoveOn()).getPiece().isPresent())
            throw new IllegalArgumentException("La case d'interception doit être libre");
        // Verify the position is between king and attacker
        Piece king = chessBoard.allyPieces(chessBoard.getCurrentTurn()).stream()
                .filter(Piece::isKing).findFirst().orElseThrow();
        boolean isOnAttackPath = chessBoard.enemyPieces(chessBoard.getCurrentTurn()).stream()
                .filter(p -> chessBoard.canAttack(p, king.getPosition()))
                .anyMatch(attacker -> {
                    Set<Position> path = attacker.squaresOnThePath(king.getPosition());
                    return path.contains(param.positionToMoveOn()) || param.positionToMoveOn().equals(attacker.getPosition());
                });
        if (!isOnAttackPath)
            throw new IllegalArgumentException("La pièce doit être intercalée entre le Roi et la pièce menaçante");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.add(param.piece(), param.positionToMoveOn());
    }
}
