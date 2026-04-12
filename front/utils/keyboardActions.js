/**
 * Determines the action for a keyboard event during a game.
 * Returns 'playCard', 'endTurn', 'undo', or null.
 */
export function resolveKeyAction(e, {gameId, myColor, currentPlayerColor, selectedCard}) {
    if (gameId === null) return null;

    const isMyTurn = !myColor || myColor === currentPlayerColor;
    if (!isMyTurn) return null;

    if (e.code === 'Space') {
        if (selectedCard && !Object.values(selectedCard.param || {}).some(x => x === null)) {
            return 'playCard';
        }
        return 'endTurn';
    }

    if (e.key === 'z' && (e.ctrlKey || e.metaKey)) {
        return 'undo';
    }

    return null;
}
