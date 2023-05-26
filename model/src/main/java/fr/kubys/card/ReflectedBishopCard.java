package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Position;
import fr.kubys.piece.Bishop;


import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ReflectedBishopCard extends Card {

    private Bishop bishop;
    private Position positionToMoveOn;

    public ReflectedBishopCard() {
        super("Fou réfléchi", "Déplacez l'un de vos fous en le faisant \"rebondir\" sur les côtés de l'échiquier poursuivant son chemin en décrivnat en angle droit. Il n'y a pas de limites au nombre de rebonds au cours d'un déplacement", CardType.REPLACE_TURN);
    }

    @Override
    protected void setupParams(List<?> params) {
        this.bishop = (Bishop) params.get(0);
        this.positionToMoveOn = (Position) params.get(1);
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (bishop == null) throw new IllegalStateException();
        if (positionToMoveOn == null) throw new IllegalStateException();

        Set<Position> reachablePositions = new HashSet<>(chessBoard.getAllAttackablePosition(bishop));

        Set<Position> positionsToStart = Set.copyOf(reachablePositions);
        do {
            positionsToStart = positionsToStart.stream()
                    .filter(Position::isBorder)
                    .map(s -> createFakeBishop(chessBoard, s))
                    .map(chessBoard::getAllAttackablePosition)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
        } while (reachablePositions.addAll(positionsToStart));
        chessBoard.unfakeAllSquares();

//        log.debug("{} can move on {}", bishop, reachablePositions);

        if (!reachablePositions.contains(positionToMoveOn))
            throw new IllegalArgumentException("%s cannot reflect to %s".formatted(bishop, positionToMoveOn));
    }

    private Bishop createFakeBishop(ChessBoard chessBoard, Position s) {
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
    protected void doAction(ChessBoard chessBoard) {
        chessBoard.move(bishop, positionToMoveOn);
    }
}
