import {Chessboard} from "react-chessboard";
import {useState} from "react";

const base = "http://localhost:9000/chessboard";
export default function App() {

    const [game, setGame] = useState({});
    const [gameId, setGameId] = useState(1);
    const [color, setColor] = useState("Blancs");

    function startNewGame() {
        fetch(base, {method: 'POST'})
            .then(res => res.json())
            .then(id => setGameId(id))
            .catch(err => alert(err))
            .finally(() => fetchGame())
    }

    function onDrop(sourceSquare, targetSquare) {
        fetch(base + "/" + gameId + "/move/" + sourceSquare + "/to/" + targetSquare, {method: 'POST'})
            .then(() => setTimeout(() => endTurn(), 2000))
            .then(() => fetchGame())
            .catch(err => alert(err))
    }

    function fetchGame() {
        fetch(base + "/" + gameId)
            .then(response => response.json())
            .then(data => {
                console.log(data.pieces)
                setGame(data.pieces);
            })
    }

    function endTurn() {
        fetch(base + "/" + gameId + "/endTurn", {method: 'POST'})
            .then(() => setColor(color === "Blancs" ? "Noirs" : "Blancs"))
            .then(() => fetchGame())
            .catch(err => alert(err))
    }

    return (
        <div style={{
            margin: '3rem auto',
            maxWidth: '70vh',
            width: '70vw'
        }}>
            <h1>Tempête sur l'Échiquier</h1>
            <h1>Trait aux {color}</h1>
            <Chessboard id="BasicBoard"
                        onPieceDrop={onDrop}
                        position={game}/>
            <button
                onClick={startNewGame}
            >
                Start a new Game
            </button>
            <button
                onClick={endTurn}
            >
                Passer son Tour
            </button>
        </div>

    );
}
