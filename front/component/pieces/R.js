export const R = ({ squareWidth }) => (
    <div style={{ width: squareWidth, height: squareWidth, position: 'relative' }}>
        <img src={require('../../assets/images/pieces/wR.svg')} style={{ position: 'absolute', inset: 0, width: '100%', height: '100%', clipPath: 'inset(0 50% 0 0)' }} />
        <img src={require('../../assets/images/pieces/bR.svg')} style={{ position: 'absolute', inset: 0, width: '100%', height: '100%', clipPath: 'inset(0 0 0 50%)' }} />
    </div>
);
