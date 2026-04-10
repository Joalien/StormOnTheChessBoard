export const wCrab = ({ squareWidth }) => (
    <img
        src={require('../../assets/images/pieces/wCrab.svg')}
        alt="White Crab"
        style={{
            width: squareWidth * 0.85,
            height: squareWidth * 0.85,
            padding: squareWidth * 0.075
        }}
    />
);
