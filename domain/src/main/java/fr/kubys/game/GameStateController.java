package fr.kubys.game;

import fr.kubys.api.ChessBoardService;
import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.Effect;
import fr.kubys.card.*;
import fr.kubys.card.params.CardParam;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;
import fr.kubys.player.Player;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GameStateController implements ChessBoardService {

    private static final int NUMBER_OF_CARDS_IN_HAND = 4;
    //    private static final Logger log = org.slf4j.LoggerFactory.getLogger(GameStateController.class);
    private ChessBoard chessBoard;
    private Player white;
    private Player black;
    private List<Card<? extends CardParam>> cards;
    private Player currentPlayer;
    private StateEnum currentState;

    public GameStateController() {
    }

    @Override
    public void startGame(long seed) {
        if (this.chessBoard != null)
            throw new IllegalStateException("Game has already started!");

        this.chessBoard = ChessBoard.createWithInitialState();
        white = new Player("Name1", Color.WHITE);
        black = new Player("Name2", Color.BLACK);

        initDeck(seed);
        IntStream.range(0, NUMBER_OF_CARDS_IN_HAND)
                .peek(x -> dealCard(white))
                .forEach(x -> dealCard(black));
        currentState = StateEnum.BEGINNING_OF_THE_TURN;
    }

    private void initDeck(long seed) {
        cards = new LinkedList<>();
        cards.add(new BlackHoleCard());
        cards.add(new BombingCard());
        cards.add(new ChargeCard());
        cards.add(new CourtlyLoveCard());
        cards.add(new HomeCard());
        cards.add(new KangarooCard());
        cards.add(new LightweightSquadCard());
        cards.add(new MagnetismCard());
        cards.add(new QuadrilleCard());
        cards.add(new ReflectedBishopCard());
        cards.add(new StableCard());

        Collections.shuffle(cards, new Random(seed));
    }

    private void dealCard(Player player) {
        player.getCards().add(cards.remove(0));
    }

    @Override
    public void tryToMove(Position from, Position to) {
        assertGameHasAlreadyStarted();
        Optional<Piece> pieceToMove = chessBoard.at(from).getPiece();
        if (pieceToMove.isEmpty()) throw new IllegalArgumentException("There is no piece on %s".formatted(from));
        if (pieceToMove.get().getColor() != getCurrentPlayer().getColor())
            throw new IllegalStateException("%s player cannot move %s piece".formatted(getCurrentPlayer().getColor(), pieceToMove.get().getColor()));

        currentState.getState().tryToMove(this, from, to);
    }

    @Override
    public <T extends CardParam> void tryToPlayCard(Card<T> card, T params) {
        assertGameHasAlreadyStarted();
        if (!getCurrentPlayer().getCards().contains(card))
            throw new CardNotFoundException("Player %s does not have %s in hand!".formatted(getCurrentPlayer(), card));

        currentState.getState().tryToPlayCard(this, card, params);
        getCurrentPlayer().getCards().remove(card);
        dealCard(getCurrentPlayer());
    }

    @Override
    public void tryToPass() {
        assertGameHasAlreadyStarted();
        currentState.getState().tryToPass(this);
        setCurrentState(StateEnum.BEGINNING_OF_THE_TURN);
        swapCurrentPlayer();
        chessBoard.setTurn(getCurrentPlayer().getColor());
    }

    private void swapCurrentPlayer() {
        chessBoard.setTurn(chessBoard.getCurrentTurn().opposite());
    }

    private void assertGameHasAlreadyStarted() {
        if (chessBoard == null) throw new IllegalStateException("Game has not started yet!");
    }

    @Override
    public Set<Piece> getPieces() {
        return chessBoard.getPieces();
    }

    @Override
    public Set<Effect> getEffects() {
        return chessBoard.getEffects();
    }

    @Override
    public List<Card<? extends CardParam>> getCards() {
        return this.cards;
    }

    @Override
    public Player getCurrentPlayer() {
        return Stream.of(white, black)
                .filter(player -> player.getColor() == chessBoard.getCurrentTurn())
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    public Player getWhite() {
        return this.white;
    }

    @Override
    public Player getBlack() {
        return this.black;
    }

    ChessBoard getChessBoard() {
        return this.chessBoard;
    }

    StateEnum getCurrentState() {
        return this.currentState;
    }

    void setCurrentState(StateEnum currentState) {
//        log.debug("{} is now in state {}", this.currentPlayer, currentState);
        this.currentState = currentState;
    }
}
