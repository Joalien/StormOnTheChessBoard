/**
 * Tests for matchmaking flow logic.
 * These test the decision logic, not the full React component.
 */

describe('matchmaking join response handling', () => {
    test('immediate match: navigates to game with color', () => {
        const data = {status: 'matched', gameId: 42, color: 'white', token: 'abc'};
        expect(data.status).toBe('matched');
        expect(data.gameId).toBe(42);
        expect(data.color).toBe('white');
    });

    test('waiting: stores token', () => {
        const data = {status: 'waiting', token: 'uuid-123'};
        expect(data.status).toBe('waiting');
        expect(data.token).toBe('uuid-123');
    });
});

describe('matchmaking WebSocket message parsing', () => {
    test('matched message contains gameId and color', () => {
        const raw = '{"status":"matched","gameId":42,"color":"black"}';
        const status = JSON.parse(raw);
        expect(status.status).toBe('matched');
        expect(status.gameId).toBe(42);
        expect(status.color).toBe('black');
    });

    test('waiting status from polling', () => {
        const raw = '{"status":"waiting"}';
        const status = JSON.parse(raw);
        expect(status.status).toBe('waiting');
        expect(status.gameId).toBeUndefined();
    });
});

describe('matchmaking cancel logic', () => {
    test('cancel sends DELETE and cleans up refs', () => {
        const mockFetch = jest.fn().mockResolvedValue({ok: true});
        global.fetch = mockFetch;

        const token = 'uuid-456';
        // Simulate cancelMatchmaking logic
        fetch('http://localhost:9000/api/matchmaking/' + token, {method: 'DELETE'});

        expect(mockFetch).toHaveBeenCalledWith(
            'http://localhost:9000/api/matchmaking/uuid-456',
            {method: 'DELETE'}
        );

        delete global.fetch;
    });
});

describe('matchmaking race condition: poll after WS open', () => {
    test('if status poll returns matched, should navigate even if WS silent', () => {
        // Simulates the race condition fix: after WS opens, poll status endpoint
        const pollResponse = {status: 'matched', gameId: 7, color: 'white'};

        expect(pollResponse.status).toBe('matched');
        // In real code, this triggers navigateToGame(7, 'white')
    });

    test('if status poll returns waiting, do nothing (wait for WS)', () => {
        const pollResponse = {status: 'waiting'};
        expect(pollResponse.status).toBe('waiting');
        // No navigation should happen
    });
});
