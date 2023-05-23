package card;

import board.ChessBoard;
import piece.Knight;
import piece.Rock;
import position.Position;

import java.util.List;

// TODO generify me to allow other swap cards
public class StableCard extends Card {

    private Rock rock;
    private Knight knight;


    public StableCard() {
        super("Écurie", "Sur l'échiquier, permutez l'un de vos cavaliers avec l'une de vos tours", CardType.AFTER_TURN);
    }

    @Override
    protected void setupParams(List<?> params) {
        this.rock = (Rock) params.get(0);
        this.knight = (Knight) params.get(1);
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
        Position rockPosition = rock.getPosition();
        Position knightPosition = knight.getPosition();

        chessBoard.removePieceFromTheBoard(rock);
        chessBoard.removePieceFromTheBoard(knight);

        chessBoard.add(rock, knightPosition);
        chessBoard.add(knight, rockPosition);

        return true;
    }
}
