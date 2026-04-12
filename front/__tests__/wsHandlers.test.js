import {parsePresenceMessage, parseMatchmakingMessage, isRefreshMessage} from '../utils/wsHandlers';

describe('Presence WebSocket messages', () => {
    test('connected count update', () => {
        const result = parsePresenceMessage('{"connected":3}');
        expect(result).toEqual({connected: 3});
    });

    test('matchmaking stats update', () => {
        const result = parsePresenceMessage('{"waitingCount":1,"activeMatchCount":2}');
        expect(result).toEqual({waitingCount: 1, activeMatchCount: 2});
    });

    test('connected count zero', () => {
        const result = parsePresenceMessage('{"connected":0}');
        expect(result).toEqual({connected: 0});
    });

    test('can be merged into existing stats', () => {
        const prev = {connected: 2, waitingCount: 0, activeMatchCount: 1};
        const msg = parsePresenceMessage('{"connected":3}');
        expect({...prev, ...msg}).toEqual({connected: 3, waitingCount: 0, activeMatchCount: 1});
    });

    test('stats update does not overwrite connected', () => {
        const prev = {connected: 3, waitingCount: 0, activeMatchCount: 0};
        const msg = parsePresenceMessage('{"waitingCount":1,"activeMatchCount":0}');
        expect({...prev, ...msg}).toEqual({connected: 3, waitingCount: 1, activeMatchCount: 0});
    });

    test('invalid JSON returns null', () => {
        expect(parsePresenceMessage('not json')).toBeNull();
    });

    test('empty string returns null', () => {
        expect(parsePresenceMessage('')).toBeNull();
    });

    test('primitive value returns null', () => {
        expect(parsePresenceMessage('42')).toBeNull();
        expect(parsePresenceMessage('"hello"')).toBeNull();
    });
});

describe('Matchmaking WebSocket messages', () => {
    test('matched message returns gameId and color', () => {
        const result = parseMatchmakingMessage('{"status":"matched","gameId":42,"color":"white"}');
        expect(result).toEqual({matched: true, gameId: 42, color: 'white'});
    });

    test('matched as black', () => {
        const result = parseMatchmakingMessage('{"status":"matched","gameId":7,"color":"black"}');
        expect(result).toEqual({matched: true, gameId: 7, color: 'black'});
    });

    test('waiting message returns null (not a match)', () => {
        const result = parseMatchmakingMessage('{"status":"waiting"}');
        expect(result).toBeNull();
    });

    test('invalid JSON returns null', () => {
        expect(parseMatchmakingMessage('bad')).toBeNull();
    });

    test('missing status returns null', () => {
        expect(parseMatchmakingMessage('{"gameId":1}')).toBeNull();
    });
});

describe('Game WebSocket messages', () => {
    test('"refresh" is a refresh signal', () => {
        expect(isRefreshMessage('refresh')).toBe(true);
    });

    test('other messages are not refresh signals', () => {
        expect(isRefreshMessage('something')).toBe(false);
        expect(isRefreshMessage('')).toBe(false);
        expect(isRefreshMessage('{"type":"refresh"}')).toBe(false);
    });
});
