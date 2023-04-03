package card;

import board.ChessBoard;
import lombok.extern.slf4j.Slf4j;
import piece.Bishop;
import position.PositionUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ReflectedBishopCard extends Card {

    private final Bishop bishop;
    private final String positionToMoveOn;

    public ReflectedBishopCard(Bishop bishop, String positionToMoveOn) {
        super("Fou réfléchi", "Déplacez l'un de vos fous en le faisant \"rebondir\" sur les côtés de l'échiquier poursuivant son chemin en décrivnat en angle droit. Il n'y a pas de limites au nombre de rebonds au cours d'un déplacement", SCType.REPLACE_TURN);
        this.bishop = bishop;
        this.positionToMoveOn = positionToMoveOn;
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (bishop == null) throw new IllegalStateException();
        if (positionToMoveOn == null) throw new IllegalStateException();

        Set<String> reachablePositions = new HashSet<>(chessBoard.getAllAttackablePosition(bishop));

        Set<String> positionsToStart = Set.copyOf(reachablePositions);
        do {
            positionsToStart = positionsToStart.stream()
                    .filter(PositionUtil::isBorder)
                    .map(s -> createFakeBishop(chessBoard, s))
                    .map(chessBoard::getAllAttackablePosition)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
        } while (reachablePositions.addAll(positionsToStart));
        chessBoard.unfakeAllSquares();

        log.debug("{} can move on {}", bishop, reachablePositions);

        if (!reachablePositions.contains(positionToMoveOn))
            throw new IllegalArgumentException("%s cannot reflect to %s".formatted(bishop, positionToMoveOn));
    }

    private Bishop createFakeBishop(ChessBoard chessBoard, String s) {
        Bishop bishop = new Bishop(this.bishop.getColor());
        chessBoard.fakeSquare(bishop, s);
        bishop.setSquare(chessBoard.at(s));
        return bishop;
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard) {
        chessBoard.fakeSquare(null, bishop.getPosition());
        chessBoard.fakeSquare(bishop, positionToMoveOn);
        boolean isKingUnderAttack = chessBoard.isKingUnderAttack(bishop.getColor());
        chessBoard.unfakeAllSquares();
        return !isKingUnderAttack;
    }

    @Override
    protected boolean doAction(ChessBoard chessBoard) {
        chessBoard.move(bishop, positionToMoveOn);
        return true;
    }
}
