export const bCrab = ({ squareWidth }) => (
    <img
        src={require('../../assets/images/pieces/bCrab.svg')}
        alt="Black Crab"
        style={{
            width: squareWidth * 0.85,
            height: squareWidth * 0.85,
            padding: squareWidth * 0.075
        }}
    />
);
