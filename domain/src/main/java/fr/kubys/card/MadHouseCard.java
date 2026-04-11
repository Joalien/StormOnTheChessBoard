package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.MadHouseCardParam;
import fr.kubys.core.Position;

public class MadHouseCard extends Card<MadHouseCardParam> {

    public MadHouseCard() {
        super("Asile", "Sur l'échiquier, permutez l'un de vos Fous avec l'une de vos Tours.", CardType.AFTER_TURN, MadHouseCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, MadHouseCardParam param) {
        if (param.rock() == null) throw new IllegalStateException("Missing required card parameter");
        if (param.bishop() == null) throw new IllegalStateException("Missing required card parameter");
        if (param.rock().getColor() != chessBoard.getCurrentTurn())
            throw new IllegalArgumentException("You can only swap your own pieces");
        if (param.bishop().getColor() != chessBoard.getCurrentTurn())
            throw new IllegalArgumentException("You can only swap your own pieces");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, MadHouseCardParam param) {
        return true;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, MadHouseCardParam param) {
        Position rockPosition = param.rock().getPosition();
        Position bishopPosition = param.bishop().getPosition();

        chessBoard.removePieceFromTheBoard(param.rock());
        chessBoard.removePieceFromTheBoard(param.bishop());

        chessBoard.add(param.rock(), bishopPosition);
        chessBoard.add(param.bishop(), rockPosition);
    }
}
