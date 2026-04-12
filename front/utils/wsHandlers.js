/**
 * Parse and classify a presence WebSocket message.
 * Returns an object with the fields to merge into stats, or null if invalid.
 */
export function parsePresenceMessage(rawData) {
    try {
        const data = JSON.parse(rawData);
        if (typeof data !== 'object' || data === null) return null;
        return data;
    } catch (e) {
        return null;
    }
}

/**
 * Parse a matchmaking WebSocket message.
 * Returns {matched: true, gameId, color} or null.
 */
export function parseMatchmakingMessage(rawData) {
    try {
        const data = JSON.parse(rawData);
        if (data.status === 'matched') {
            return {matched: true, gameId: data.gameId, color: data.color};
        }
        return null;
    } catch (e) {
        return null;
    }
}

/**
 * Parse a game WebSocket message.
 * Returns true if the message is a refresh signal.
 */
export function isRefreshMessage(rawData) {
    return rawData === 'refresh';
}
