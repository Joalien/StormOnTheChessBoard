import {renderHook, act} from '@testing-library/react';
import {useGameActions} from '../hooks/useGameActions';

describe('useGameActions', () => {
    let mockFetch;
    let deps;

    beforeEach(() => {
        mockFetch = jest.fn();
        global.fetch = mockFetch;
        deps = {
            base: 'http://localhost:9000/api/chessboard/',
            gameId: 1,
            fetchGame: jest.fn(),
            setSelectedCard: jest.fn(),
            setSelectedParam: jest.fn(),
            setCurrentPlayerColor: jest.fn(),
            currentPlayerColor: 'white',
            selectedCard: {name: 'BombingCard', param: {position: 'e4'}},
            setLegalMoves: jest.fn(),
            showErrorMessage: jest.fn(),
        };
    });

    afterEach(() => {
        delete global.fetch;
    });

    function setup(overrides = {}) {
        return renderHook(() => useGameActions({...deps, ...overrides}));
    }

    // ── movePiece ──

    test('movePiece sends POST to correct URL', async () => {
        mockFetch.mockResolvedValue({ok: true});
        const {result} = setup();

        await act(() => result.current.movePiece('e2', 'e4'));

        expect(mockFetch).toHaveBeenCalledWith(
            'http://localhost:9000/api/chessboard/1/move/e2/to/e4',
            {method: 'POST'}
        );
    });

    test('movePiece clears legal moves', async () => {
        mockFetch.mockResolvedValue({ok: true});
        const {result} = setup();

        await act(() => result.current.movePiece('e2', 'e4'));

        expect(deps.setLegalMoves).toHaveBeenCalledWith([]);
    });

    test('movePiece fetches game on success', async () => {
        mockFetch.mockResolvedValue({ok: true});
        const {result} = setup();

        await act(() => result.current.movePiece('e2', 'e4'));

        expect(deps.fetchGame).toHaveBeenCalled();
    });

    test('movePiece shows error on failure', async () => {
        const errorRes = {ok: false, text: () => Promise.resolve('Illegal move')};
        mockFetch.mockResolvedValue(errorRes);
        const {result} = setup();

        await act(() => result.current.movePiece('e2', 'e5'));

        expect(deps.showErrorMessage).toHaveBeenCalledWith(errorRes);
        expect(deps.fetchGame).not.toHaveBeenCalled();
    });

    // ── playCard ──

    test('playCard sends POST with card params as JSON body', async () => {
        mockFetch.mockResolvedValue({ok: true});
        const {result} = setup();

        await act(() => result.current.playCard());

        expect(mockFetch).toHaveBeenCalledWith(
            'http://localhost:9000/api/chessboard/1/card/BombingCard',
            {
                method: 'POST',
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({position: 'e4'}),
            }
        );
    });

    test('playCard clears selection and fetches game on success', async () => {
        mockFetch.mockResolvedValue({ok: true});
        const {result} = setup();

        await act(() => result.current.playCard());

        expect(deps.setSelectedCard).toHaveBeenCalledWith(null);
        expect(deps.setSelectedParam).toHaveBeenCalledWith(null);
        expect(deps.fetchGame).toHaveBeenCalled();
    });

    test('playCard shows error on failure', async () => {
        const errorRes = {ok: false, text: () => Promise.resolve('Card error')};
        mockFetch.mockResolvedValue(errorRes);
        const {result} = setup();

        await act(() => result.current.playCard());

        expect(deps.showErrorMessage).toHaveBeenCalledWith(errorRes);
        expect(deps.setSelectedCard).not.toHaveBeenCalled();
    });

    // ── endTurn ──

    test('endTurn sends POST to endTurn', async () => {
        mockFetch.mockResolvedValue({ok: true});
        const {result} = setup();

        await act(() => result.current.endTurn());

        expect(mockFetch).toHaveBeenCalledWith(
            'http://localhost:9000/api/chessboard/1/endTurn',
            {method: 'POST'}
        );
    });

    test('endTurn swaps color and fetches game on success', async () => {
        mockFetch.mockResolvedValue({ok: true});
        const {result} = setup();

        await act(() => result.current.endTurn());

        expect(deps.setCurrentPlayerColor).toHaveBeenCalledWith('black');
        expect(deps.setSelectedCard).toHaveBeenCalledWith(null);
        expect(deps.fetchGame).toHaveBeenCalled();
    });

    test('endTurn shows error on failure', async () => {
        const errorRes = {ok: false, text: () => Promise.resolve('Not your turn')};
        mockFetch.mockResolvedValue(errorRes);
        const {result} = setup();

        await act(() => result.current.endTurn());

        expect(deps.showErrorMessage).toHaveBeenCalledWith(errorRes);
        expect(deps.fetchGame).not.toHaveBeenCalled();
    });

    // ── undo ──

    test('undo sends POST to undo', async () => {
        mockFetch.mockResolvedValue({ok: true});
        const {result} = setup();

        await act(() => result.current.undo());

        expect(mockFetch).toHaveBeenCalledWith(
            'http://localhost:9000/api/chessboard/1/undo',
            {method: 'POST'}
        );
    });

    test('undo clears selection and fetches game on success', async () => {
        mockFetch.mockResolvedValue({ok: true});
        const {result} = setup();

        await act(() => result.current.undo());

        expect(deps.setSelectedCard).toHaveBeenCalledWith(null);
        expect(deps.setSelectedParam).toHaveBeenCalledWith(null);
        expect(deps.fetchGame).toHaveBeenCalled();
    });

    test('undo in AI game calls undo multiple times until currentTurn matches myColor', async () => {
        mockFetch.mockResolvedValue({ok: true});
        const fetchGame = jest.fn()
            .mockResolvedValueOnce({currentTurn: 'black'})
            .mockResolvedValueOnce({currentTurn: 'black'})
            .mockResolvedValueOnce({currentTurn: 'white'});
        const {result} = setup({fetchGame, myColor: 'white'});

        await act(() => result.current.undo());

        expect(mockFetch).toHaveBeenCalledTimes(3);
        expect(mockFetch).toHaveBeenCalledWith('http://localhost:9000/api/chessboard/1/undo', {method: 'POST'});
    });

    test('undo in AI game stops after one call when already on own turn mid-turn', async () => {
        mockFetch.mockResolvedValue({ok: true});
        const fetchGame = jest.fn().mockResolvedValue({currentTurn: 'white'});
        const {result} = setup({fetchGame, myColor: 'white'});

        await act(() => result.current.undo());

        expect(mockFetch).toHaveBeenCalledTimes(1);
    });

    // ── promote ──

    test('promote sends POST with position and piece', async () => {
        mockFetch.mockResolvedValue({ok: true});
        const {result} = setup();

        await act(() => result.current.promote('e8', 'ROOK'));

        expect(mockFetch).toHaveBeenCalledWith(
            'http://localhost:9000/api/chessboard/1/promote/e8/ROOK',
            {method: 'POST'}
        );
    });

    test('promote fetches game on success', async () => {
        mockFetch.mockResolvedValue({ok: true});
        const {result} = setup();

        await act(() => result.current.promote('e8', 'ROOK'));

        expect(deps.fetchGame).toHaveBeenCalled();
    });

    // ── onPieceDragBegin / onPieceDragEnd ──

    test('onPieceDragBegin fetches legal moves', async () => {
        mockFetch.mockResolvedValue({ok: true, json: () => Promise.resolve(['e3', 'e4'])});
        const {result} = setup();

        await act(() => {
            result.current.onPieceDragBegin('wP', 'e2');
            return new Promise(r => setTimeout(r, 0));
        });

        expect(mockFetch).toHaveBeenCalledWith(
            'http://localhost:9000/api/chessboard/1/legalMoves/e2'
        );
    });

    test('onPieceDragEnd clears legal moves', () => {
        const {result} = setup();

        act(() => result.current.onPieceDragEnd());

        expect(deps.setLegalMoves).toHaveBeenCalledWith([]);
    });

    // ── auto end-turn ──

    describe('auto end-turn', () => {
        function endOfTurnState(overrides = {}) {
            return {currentState: 'END_OF_THE_TURN', currentTurn: 'white', gameResult: 'ONGOING', ...overrides};
        }

        test('movePiece auto-passes when the new state is END_OF_THE_TURN', async () => {
            // Scenario: player played a BEFORE_TURN card, then moves — state becomes END_OF_THE_TURN.
            mockFetch.mockResolvedValue({ok: true});
            const fetchGame = jest.fn().mockResolvedValue(endOfTurnState());
            const {result} = setup({fetchGame, myColor: 'white'});

            await act(() => result.current.movePiece('e2', 'e4'));

            expect(mockFetch).toHaveBeenCalledWith('http://localhost:9000/api/chessboard/1/endTurn', {method: 'POST'});
        });

        test('playCard auto-passes on END_OF_THE_TURN', async () => {
            mockFetch.mockResolvedValue({ok: true});
            const fetchGame = jest.fn().mockResolvedValue(endOfTurnState());
            const {result} = setup({fetchGame, myColor: 'white'});

            await act(() => result.current.playCard());

            expect(mockFetch).toHaveBeenCalledWith('http://localhost:9000/api/chessboard/1/endTurn', {method: 'POST'});
        });

        test('does not auto-pass when only a move was played (MOVE_WITHOUT_CARD_PLAYED)', async () => {
            // The critical case: user still wants to be able to play an AFTER_TURN card.
            mockFetch.mockResolvedValue({ok: true});
            const fetchGame = jest.fn().mockResolvedValue({currentState: 'MOVE_WITHOUT_CARD_PLAYED', currentTurn: 'white', gameResult: 'ONGOING'});
            const {result} = setup({fetchGame, myColor: 'white'});

            await act(() => result.current.movePiece('e2', 'e4'));

            expect(mockFetch).toHaveBeenCalledTimes(1); // the move POST only
            expect(mockFetch).not.toHaveBeenCalledWith('http://localhost:9000/api/chessboard/1/endTurn', expect.anything());
        });

        test('does not auto-pass from the opponent side in multiplayer', async () => {
            // When the remote client refetches after the opponent's action, it sees END_OF_THE_TURN
            // but currentTurn is the opponent's — only the opponent's own client should pass.
            mockFetch.mockResolvedValue({ok: true});
            const fetchGame = jest.fn().mockResolvedValue(endOfTurnState({currentTurn: 'black'}));
            const {result} = setup({fetchGame, myColor: 'white'});

            await act(() => result.current.movePiece('e2', 'e4'));

            expect(mockFetch).toHaveBeenCalledTimes(1);
        });

        test('does not auto-pass when the game is over', async () => {
            mockFetch.mockResolvedValue({ok: true});
            const fetchGame = jest.fn().mockResolvedValue(endOfTurnState({gameResult: 'WHITE_WINS'}));
            const {result} = setup({fetchGame, myColor: 'white'});

            await act(() => result.current.movePiece('e2', 'e4'));

            expect(mockFetch).toHaveBeenCalledTimes(1);
        });

        test('auto-passes in solo/training mode (no myColor)', async () => {
            mockFetch.mockResolvedValue({ok: true});
            const fetchGame = jest.fn().mockResolvedValue(endOfTurnState());
            const {result} = setup({fetchGame, myColor: null});

            await act(() => result.current.playCard());

            expect(mockFetch).toHaveBeenCalledWith('http://localhost:9000/api/chessboard/1/endTurn', {method: 'POST'});
        });

        test('undo does not auto-pass even if state is END_OF_THE_TURN after rollback', async () => {
            // Regression guard: undo must not cause a ping-pong of endTurn-undo-endTurn.
            mockFetch.mockResolvedValue({ok: true});
            const fetchGame = jest.fn().mockResolvedValue(endOfTurnState());
            const {result} = setup({fetchGame, myColor: 'white'});

            await act(() => result.current.undo());

            expect(mockFetch).toHaveBeenCalledTimes(1); // only the undo POST
        });
    });
});
