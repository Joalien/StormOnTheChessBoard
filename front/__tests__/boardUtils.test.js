import {parseRoute, parsePlayerColor, squareToCoords, oppositeColor, isMovablePiece} from '../utils/boardUtils';

describe('parseRoute', () => {
    test('/ returns home', () => {
        expect(parseRoute('/')).toEqual({page: 'home'});
    });

    test('empty string returns home', () => {
        expect(parseRoute('')).toEqual({page: 'home'});
    });

    test('/chessboard/42 returns game with id 42', () => {
        expect(parseRoute('/chessboard/42')).toEqual({page: 'game', gameId: 42});
    });

    test('/chessboard/1 returns game with id 1', () => {
        expect(parseRoute('/chessboard/1')).toEqual({page: 'game', gameId: 1});
    });

    test('/invalid returns 404', () => {
        expect(parseRoute('/invalid')).toEqual({page: '404'});
    });

    test('/chessboard/ without id returns 404', () => {
        expect(parseRoute('/chessboard/')).toEqual({page: '404'});
    });

    test('/chessboard/abc returns 404', () => {
        expect(parseRoute('/chessboard/abc')).toEqual({page: '404'});
    });

    test('/chessboard/42/extra returns 404', () => {
        expect(parseRoute('/chessboard/42/extra')).toEqual({page: '404'});
    });
});

describe('parsePlayerColor', () => {
    test('?color=white returns white', () => {
        expect(parsePlayerColor('?color=white')).toBe('white');
    });

    test('?color=black returns black', () => {
        expect(parsePlayerColor('?color=black')).toBe('black');
    });

    test('empty string returns null', () => {
        expect(parsePlayerColor('')).toBeNull();
    });

    test('null returns null', () => {
        expect(parsePlayerColor(null)).toBeNull();
    });

    test('?other=value returns null', () => {
        expect(parsePlayerColor('?other=value')).toBeNull();
    });
});

describe('squareToCoords', () => {
    const boardSize = 560;
    const sqSize = 70;

    test('a1 white orientation is bottom-left', () => {
        const {x, y} = squareToCoords('a1', 'white', boardSize);
        expect(x).toBe(0);
        expect(y).toBe(7 * sqSize);
    });

    test('h8 white orientation is top-right', () => {
        const {x, y} = squareToCoords('h8', 'white', boardSize);
        expect(x).toBe(7 * sqSize);
        expect(y).toBe(0);
    });

    test('a1 black orientation is top-right', () => {
        const {x, y} = squareToCoords('a1', 'black', boardSize);
        expect(x).toBe(7 * sqSize);
        expect(y).toBe(0);
    });

    test('h8 black orientation is bottom-left', () => {
        const {x, y} = squareToCoords('h8', 'black', boardSize);
        expect(x).toBe(0);
        expect(y).toBe(7 * sqSize);
    });

    test('e4 white orientation', () => {
        const {x, y} = squareToCoords('e4', 'white', boardSize);
        expect(x).toBe(4 * sqSize);
        expect(y).toBe(4 * sqSize);
    });

    test('different board sizes scale proportionally', () => {
        const small = squareToCoords('a1', 'white', 400);
        const large = squareToCoords('a1', 'white', 800);
        expect(large.y).toBe(small.y * 2);
    });
});

describe('oppositeColor', () => {
    test('white returns black', () => {
        expect(oppositeColor('white')).toBe('black');
    });

    test('black returns white', () => {
        expect(oppositeColor('black')).toBe('white');
    });
});

describe('isMovablePiece', () => {
    test('white piece is movable by white player', () => {
        expect(isMovablePiece('wP', 'white')).toBe(true);
    });

    test('black piece is movable by black player', () => {
        expect(isMovablePiece('bN', 'black')).toBe(true);
    });

    test('white piece is not movable by black player', () => {
        expect(isMovablePiece('wP', 'black')).toBe(false);
    });

    test('black piece is not movable by white player', () => {
        expect(isMovablePiece('bN', 'white')).toBe(false);
    });

    test('neutral piece is movable by white player', () => {
        expect(isMovablePiece('P', 'white')).toBe(true);
    });

    test('neutral piece is movable by black player', () => {
        expect(isMovablePiece('N', 'black')).toBe(true);
    });

    test('neutral fused piece is movable by either player', () => {
        expect(isMovablePiece('Fused_P_N', 'white')).toBe(true);
        expect(isMovablePiece('Fused_P_N', 'black')).toBe(true);
    });

    test('white fused piece is movable only by white', () => {
        expect(isMovablePiece('wFused_P_N', 'white')).toBe(true);
        expect(isMovablePiece('wFused_P_N', 'black')).toBe(false);
    });

    test('empty square is not movable', () => {
        expect(isMovablePiece(undefined, 'white')).toBe(false);
        expect(isMovablePiece(null, 'black')).toBe(false);
        expect(isMovablePiece('', 'white')).toBe(false);
    });
});
