import { Chessboard } from "react-chessboard";
import {useState} from "react";

export default function App() {

    const [game, setGame] = useState({});
    function onSquareRightClick(square) {
        console.log(square)
        setGame({ [square]: 'wK'})
    }

  return (
      <div style={{
          margin: '3rem auto',
          maxWidth: '70vh',
          width: '70vw'
      }}>
        <h1>Tempête sur l'Échiquier</h1>
        <Chessboard id="BasicBoard"
                    onSquareRightClick={onSquareRightClick}
                    position={game}/>
      </div>
  );
}
