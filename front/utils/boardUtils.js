export function parseRoute(pathname) {
    if (!pathname || pathname === '/') return {page: 'home'};
    const match = pathname.match(/^\/chessboard\/(\d+)$/);
    if (match) return {page: 'game', gameId: parseInt(match[1], 10)};
    return {page: '404'};
}

export function parsePlayerColor(search) {
    const params = new URLSearchParams(search || '');
    return params.get('color');
}

export function getRouteFromUrl() {
    if (typeof window === 'undefined') return {page: 'home'};
    return parseRoute(window.location.pathname);
}

export function getPlayerColorFromUrl() {
    if (typeof window === 'undefined') return null;
    return parsePlayerColor(window.location.search);
}

export function squareToCoords(square, orientation, boardSize) {
    const file = square.charCodeAt(0) - 97;
    const rank = parseInt(square[1]) - 1;
    const size = boardSize / 8;
    let x, y;
    if (orientation === 'white') {
        x = file * size;
        y = (7 - rank) * size;
    } else {
        x = (7 - file) * size;
        y = rank * size;
    }
    return {x, y};
}

export function oppositeColor(color) {
    return color === "white" ? "black" : "white";
}

export function isMovablePiece(pieceCode, currentPlayerColor) {
    if (!pieceCode) return false;
    const colorChar = pieceCode[0];
    if (colorChar !== 'w' && colorChar !== 'b') return true;
    return colorChar === currentPlayerColor[0];
}
