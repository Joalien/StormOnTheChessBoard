export const bKangaroo = ({ squareWidth }) => (
    <img
        src={require('../../assets/images/pieces/bKangaroo.svg')}
        alt="Black Kangaroo"
        style={{
            width: squareWidth * 0.85,
            height: squareWidth * 0.85,
            padding: squareWidth * 0.075
        }}
    />
);