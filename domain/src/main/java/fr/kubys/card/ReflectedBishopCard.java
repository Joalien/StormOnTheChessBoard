package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.ReflectedBishopCardParam;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.Bishop;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ReflectedBishopCard extends Card<ReflectedBishopCardParam> {

    public ReflectedBishopCard() {
        super("Fou réfléchi", "Déplacez l'un de vos fous en le faisant \"rebondir\" sur les côtés de l'échiquier poursuivant son chemin en décrivnat en angle droit. Il n'y a pas de limites au nombre de rebonds au cours d'un déplacement", CardType.REPLACE_TURN, ReflectedBishopCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, ReflectedBishopCardParam param) {
        if (param.bishop() == null) throw new IllegalStateException();
        if (param.positionToMoveOn() == null) throw new IllegalStateException();

        Set<Position> reachablePositions = new HashSet<>(chessBoard.getAllAttackablePosition(param.bishop()));

        Set<Position> positionsToStart = Set.copyOf(reachablePositions);
        do {
            positionsToStart = positionsToStart.stream()
                    .filter(Position::isBorder)
                    .map(s -> createFakeBishop(chessBoard, s, param.bishop().getColor()))
                    .map(chessBoard::getAllAttackablePosition)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
        } while (reachablePositions.addAll(positionsToStart));
        chessBoard.unfakeAllSquares();

//        log.debug("{} can move on {}", param.bishop(, reachablePositions);

        if (!reachablePositions.contains(param.positionToMoveOn()))
            throw new IllegalArgumentException("%s cannot reflect to %s".formatted(param.bishop(), param.positionToMoveOn()));
    }

    private Bishop createFakeBishop(ChessBoard chessBoard, Position s, Color bishopColor) {
        Bishop bishop = new Bishop(bishopColor);
        chessBoard.fakeSquare(bishop, s);
        bishop.setSquare(chessBoard.at(s));
        return bishop;
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, ReflectedBishopCardParam param) {
        chessBoard.fakeSquare(null, param.bishop().getPosition());
        chessBoard.fakeSquare(param.bishop(), param.positionToMoveOn());
        boolean isKingUnderAttack = chessBoard.isKingUnderAttack(param.bishop().getColor());
        chessBoard.unfakeAllSquares();
        return !isKingUnderAttack;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, ReflectedBishopCardParam param) {
        chessBoard.move(param.bishop(), param.positionToMoveOn());
    }
}
