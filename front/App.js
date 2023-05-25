import { Chessboard } from "react-chessboard";
import {useState} from "react";

export default function App() {

    const [game, setGame] = useState({});
    function onSquareRightClick(square) {
        console.log(square)
        setGame({ [square]: 'wK'})
    }

    function getInitialState() {
        fetch("http://localhost:8080/chessboard/1")
            .then(response => response.json())
            .then(data => {
                console.log(data.pieces)
                setGame(data.pieces);
            })
    }

  return (
      <div style={{
          margin: '3rem auto',
          maxWidth: '70vh',
          width: '70vw'
      }}>
        <h1>Tempête sur l'Échiquier</h1>
        <Chessboard id="BasicBoard"
                    onSquareRightClick={getInitialState}
                    position={game}/>
      </div>
  );
}
