import {resolveKeyAction} from '../utils/keyboardActions';

describe('resolveKeyAction', () => {
    const base = {gameId: 1, myColor: null, currentPlayerColor: 'white', selectedCard: null};

    function space() { return {code: 'Space', key: ' ', ctrlKey: false, metaKey: false}; }
    function ctrlZ() { return {code: 'KeyZ', key: 'z', ctrlKey: true, metaKey: false}; }
    function metaZ() { return {code: 'KeyZ', key: 'z', ctrlKey: false, metaKey: true}; }
    function otherKey() { return {code: 'KeyA', key: 'a', ctrlKey: false, metaKey: false}; }

    // ── Space ──

    test('space with no card selected → endTurn', () => {
        expect(resolveKeyAction(space(), base)).toBe('endTurn');
    });

    test('space with card selected and all params set → playCard', () => {
        const ctx = {...base, selectedCard: {param: {position: 'e4'}}};
        expect(resolveKeyAction(space(), ctx)).toBe('playCard');
    });

    test('space with card selected but params missing → endTurn', () => {
        const ctx = {...base, selectedCard: {param: {position: null}}};
        expect(resolveKeyAction(space(), ctx)).toBe('endTurn');
    });

    test('space with card with no params → playCard', () => {
        const ctx = {...base, selectedCard: {param: {}}};
        expect(resolveKeyAction(space(), ctx)).toBe('playCard');
    });

    // ── Ctrl+Z / Cmd+Z ──

    test('ctrl+z → undo', () => {
        expect(resolveKeyAction(ctrlZ(), base)).toBe('undo');
    });

    test('cmd+z → undo', () => {
        expect(resolveKeyAction(metaZ(), base)).toBe('undo');
    });

    // ── Not my turn ──

    test('space when not my turn → null', () => {
        const ctx = {...base, myColor: 'black', currentPlayerColor: 'white'};
        expect(resolveKeyAction(space(), ctx)).toBeNull();
    });

    test('ctrl+z when not my turn → null', () => {
        const ctx = {...base, myColor: 'black', currentPlayerColor: 'white'};
        expect(resolveKeyAction(ctrlZ(), ctx)).toBeNull();
    });

    // ── No game ──

    test('space with no gameId → null', () => {
        expect(resolveKeyAction(space(), {...base, gameId: null})).toBeNull();
    });

    // ── My turn in matchmaking ──

    test('space when myColor matches currentPlayerColor → endTurn', () => {
        const ctx = {...base, myColor: 'white', currentPlayerColor: 'white'};
        expect(resolveKeyAction(space(), ctx)).toBe('endTurn');
    });

    // ── Other keys ──

    test('other key → null', () => {
        expect(resolveKeyAction(otherKey(), base)).toBeNull();
    });
});
