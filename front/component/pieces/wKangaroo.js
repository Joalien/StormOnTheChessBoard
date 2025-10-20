export const wKangaroo = ({ squareWidth }) => (
    <img
        src={require('../../assets/images/pieces/wKangaroo.svg')}
        alt="White Kangaroo"
        style={{
            width: squareWidth * 0.85,
            height: squareWidth * 0.85,
            padding: squareWidth * 0.075
        }}
    />
);