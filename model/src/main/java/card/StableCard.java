package card;

import board.ChessBoard;
import piece.Knight;
import piece.Rock;

// TODO generify me to allow other swap cards
public class StableCard extends Card {

    private final Rock rock;
    private final Knight knight;


    public StableCard(Rock rock, Knight knight) {
        super("Écurie", "Sur l'échiquier, permutez l'un de vos cavaliers avec l'une de vos tours", SCType.AFTER_TURN);
        this.rock = rock;
        this.knight = knight;
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (rock == null) throw new IllegalStateException();
        if (knight == null) throw new IllegalStateException();
        if (rock.getColor() != knight.getColor())
            throw new IllegalArgumentException("You should swap pieces of the same color");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard) {
        return true;
    }

    @Override
    protected boolean doAction(ChessBoard chessBoard) {
        String rockPosition = rock.getPosition();
        String knightPosition = knight.getPosition();

        chessBoard.removePieceFromTheBoard(rock);
        chessBoard.removePieceFromTheBoard(knight);

        chessBoard.add(rock, knightPosition);
        chessBoard.add(knight, rockPosition);

        return true;
    }
}
