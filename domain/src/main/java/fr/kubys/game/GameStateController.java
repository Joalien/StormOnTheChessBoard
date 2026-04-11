package fr.kubys.game;

import fr.kubys.api.ChessBoardService;
import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.Effect;
import fr.kubys.card.Card;
import fr.kubys.card.CardNotFoundException;
import fr.kubys.card.CardType;
import fr.kubys.card.params.CardParam;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.game.exception.InvalidGameActionException;
import fr.kubys.piece.Piece;
import fr.kubys.piece.PromotionPiece;
import fr.kubys.player.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GameStateController implements ChessBoardService {

    private static final int NUMBER_OF_CARDS_IN_HAND = 5;
    //    private static final Logger log = org.slf4j.LoggerFactory.getLogger(GameStateController.class);
    private ChessBoard chessBoard;
    private Player white;
    private Player black;
    private CardDeck deck;
    private Player currentPlayer;
    private StateEnum currentState;
    private final Set<Position> pendingPromotions = new HashSet<>();
    private StateEnum returnStateAfterPromotion;
    private boolean enemyCardPlayedThisTurn = false;
    private StateEnum returnStateAfterEnemyReaction;

    private final java.util.function.Supplier<ChessBoard> boardFactory;

    public GameStateController() {
        this(ChessBoard::createWithInitialState);
    }

    public GameStateController(java.util.function.Supplier<ChessBoard> boardFactory) {
        this.boardFactory = boardFactory;
    }

    @Override
    public void startGame(long seed) {
        startWith(boardFactory.get(), seed);
    }

    private void startWith(ChessBoard board, long seed) {
        if (this.chessBoard != null)
            throw new InvalidGameActionException("La partie a déjà commencé !");

        this.chessBoard = board;
        white = new Player("Name1", Color.WHITE);
        black = new Player("Name2", Color.BLACK);

        deck = new CardDeck(seed);
        IntStream.range(0, NUMBER_OF_CARDS_IN_HAND)
                .peek(x -> deck.dealCard(white))
                .forEach(x -> deck.dealCard(black));
        currentState = StateEnum.BEGINNING_OF_THE_TURN;
    }

    @Override
    public void tryToMove(Position from, Position to) {
        assertGameHasAlreadyStarted();
        autoResolveEnemyReaction();
        Optional<Piece> pieceToMove = chessBoard.at(from).getPiece();
        if (pieceToMove.isEmpty()) throw new IllegalArgumentException("Il n'y a pas de pièce sur %s".formatted(from));
        if (pieceToMove.get().getColor().cannotBeMovedBy(getCurrentPlayer().getColor()))
            throw new InvalidGameActionException("Le joueur %s ne peut pas déplacer une pièce %s".formatted(getCurrentPlayer().getColor(), pieceToMove.get().getColor()));

        currentState.getState().tryToMove(this, from, to);
    }

    @Override
    public <T extends CardParam> void tryToPlayCard(Card<T> card, T params) {
        assertGameHasAlreadyStarted();

        if (currentState == StateEnum.ENEMY_REACTION && card.getType() == CardType.ENEMY_TURN) {
            // Opponent is reacting with an ENEMY_TURN card — check opponent's hand
            Player opponent = getOpponent();
            if (!opponent.getCards().contains(card))
                throw new CardNotFoundException("Le joueur %s n'a pas %s en main !".formatted(opponent, card));
            currentState.getState().tryToPlayCard(this, card, params);
            opponent.getCards().remove(card);
            deck.discardAndDraw(card, opponent);
            return;
        }

        autoResolveEnemyReaction();
        Player player = getCurrentPlayer();
        if (!player.getCards().contains(card))
            throw new CardNotFoundException("Le joueur %s n'a pas %s en main !".formatted(player, card));

        currentState.getState().tryToPlayCard(this, card, params);
        player.getCards().remove(card);
        deck.discardAndDraw(card, player);
    }

    @Override
    public void tryToPass() {
        assertGameHasAlreadyStarted();
        autoResolveEnemyReaction();
        currentState.getState().tryToPass(this);
        enemyCardPlayedThisTurn = false;
        setCurrentState(StateEnum.BEGINNING_OF_THE_TURN);
        swapCurrentPlayer();
        chessBoard.setTurn(getCurrentPlayer().getColor());
    }

    private void autoResolveEnemyReaction() {
        if (currentState == StateEnum.ENEMY_REACTION) {
            setCurrentState(returnStateAfterEnemyReaction);
        }
    }

    private void swapCurrentPlayer() {
        chessBoard.setTurn(chessBoard.getCurrentTurn().opposite());
    }

    private void assertGameHasAlreadyStarted() {
        if (chessBoard == null) throw new InvalidGameActionException("La partie n'a pas encore commencé !");
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
    public List<Card<? extends CardParam>> getStack() {
        return deck.getStack();
    }

    @Override
    public List<Card<? extends CardParam>> getDiscard() {
        return deck.getDiscard();
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

    CardDeck getDeck() {
        return this.deck;
    }

    @Override
    public List<Piece> getCapturedPieces() {
        return chessBoard.getOutOfTheBoardPieces();
    }

    @Override
    public String getCurrentStateName() {
        return this.currentState.name();
    }

    StateEnum getCurrentState() {
        return this.currentState;
    }

    void clearPendingPromotions() {
        pendingPromotions.clear();
    }

    void setCurrentState(StateEnum currentState) {
//        log.debug("{} is now in state {}", this.currentPlayer, currentState);
        this.currentState = currentState;
    }

    void transitionToState(StateEnum nextState) {
        List<Position> promoted = chessBoard.drainPromotedPositions();
        if (!promoted.isEmpty()) {
            pendingPromotions.addAll(promoted);
            returnStateAfterPromotion = nextState;
            setCurrentState(StateEnum.PROMOTION_PENDING);
        } else {
            transitionToStateAfterPromotion(nextState);
        }
    }

    void transitionToStateAfterPromotion(StateEnum nextState) {
        if (!enemyCardPlayedThisTurn) {
            returnStateAfterEnemyReaction = nextState;
            setCurrentState(StateEnum.ENEMY_REACTION);
        } else {
            setCurrentState(nextState);
        }
    }

    @Override
    public void promote(Position position, PromotionPiece piece) {
        assertGameHasAlreadyStarted();
        if (!pendingPromotions.remove(position))
            throw new IllegalArgumentException("Pas de promotion en attente sur %s".formatted(position));
        chessBoard.overridePromotion(position, piece);
        if (pendingPromotions.isEmpty()) {
            transitionToStateAfterPromotion(returnStateAfterPromotion);
        }
    }

    @Override
    public Set<Position> getPendingPromotions() {
        return Set.copyOf(pendingPromotions);
    }

    @Override
    public Set<Position> getLegalMoves(Position position) {
        Piece piece = chessBoard.at(position).getPiece()
                .orElseThrow(() -> new IllegalArgumentException("No piece on %s".formatted(position)));
        return chessBoard.getLegalMoves(piece);
    }

    Player getOpponent() {
        return Stream.of(white, black)
                .filter(player -> player.getColor() != chessBoard.getCurrentTurn())
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }

    void setEnemyCardPlayedThisTurn(boolean played) {
        this.enemyCardPlayedThisTurn = played;
    }

    StateEnum getReturnStateAfterEnemyReaction() {
        return returnStateAfterEnemyReaction;
    }
}
