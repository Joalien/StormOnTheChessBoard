import {Chessboard} from "react-chessboard";
import {useState} from "react";

const base = "http://localhost:9000/chessboard";
export default function App() {

    const [game, setGame] = useState({});

    function onDrop(sourceSquare, targetSquare) {
        fetch(base + "/1/move/" + sourceSquare + "/to/" + targetSquare, {method: 'POST'})
            .then(() => getInitialState())
            .catch(err => alert(err))
    }

    function getInitialState() {
        fetch(base + "/1")
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
                        onPieceDrop={onDrop}
                        position={game}/>
        </div>
    );
}
