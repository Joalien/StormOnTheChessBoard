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
});
