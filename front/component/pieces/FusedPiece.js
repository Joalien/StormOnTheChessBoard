const pieceImages = {
    wP: require('../../assets/images/pieces/wP.svg'),
    wN: require('../../assets/images/pieces/wN.svg'),
    wB: require('../../assets/images/pieces/wB.svg'),
    wR: require('../../assets/images/pieces/wR.svg'),
    wQ: require('../../assets/images/pieces/wQ.svg'),
    wK: require('../../assets/images/pieces/wK.svg'),
    wCrab: require('../../assets/images/pieces/wCrab.svg'),
    wKangaroo: require('../../assets/images/pieces/wKangaroo.svg'),
    bP: require('../../assets/images/pieces/bP.svg'),
    bN: require('../../assets/images/pieces/bN.svg'),
    bB: require('../../assets/images/pieces/bB.svg'),
    bR: require('../../assets/images/pieces/bR.svg'),
    bQ: require('../../assets/images/pieces/bQ.svg'),
    bK: require('../../assets/images/pieces/bK.svg'),
    bCrab: require('../../assets/images/pieces/bCrab.svg'),
    bKangaroo: require('../../assets/images/pieces/bKangaroo.svg'),
};

/**
 * Parses a fused piece code like "wFused_N_B" and returns a component
 * showing left half of the first piece and right half of the second piece.
 * Returns null if the code is not a valid fused piece.
 */
export function createFusedPieceComponent(pieceCode) {
    const match = pieceCode.match(/^([wb])Fused_(\w+)_(\w+)$/);
    if (!match) return null;

    const [, color, firstType, secondType] = match;
    const leftImage = pieceImages[color + firstType];
    const rightImage = pieceImages[color + secondType];
    if (!leftImage || !rightImage) return null;

    return ({ squareWidth }) => (
        <div style={{ width: squareWidth, height: squareWidth, position: 'relative' }}>
            <img src={leftImage} alt={pieceCode}
                 style={{ position: 'absolute', inset: 0, width: '100%', height: '100%', clipPath: 'inset(0 50% 0 0)' }} />
            <img src={rightImage} alt={pieceCode}
                 style={{ position: 'absolute', inset: 0, width: '100%', height: '100%', clipPath: 'inset(0 0 0 50%)' }} />
        </div>
    );
}
