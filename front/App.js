import {Chessboard} from "react-chessboard";
import {useEffect, useState} from "react";
import {Player} from "./Player";

const base = "http://localhost:9000/chessboard/";
export default function App() {

    const [game, setGame] = useState({});
    const [gameId, setGameId] = useState(1);
    const [currentPlayerColor, setCurrentPlayerColor] = useState("white");
    const [whitePlayer, setWhitePlayer] = useState({cards: []});
    const [blackPlayer, setBlackPlayer] = useState({cards: []});

    useEffect(() => {
        fetchGame(gameId)
        fetchPlayer(gameId, "white").then(data => setWhitePlayer(data));
        fetchPlayer(gameId, "black").then(data => setBlackPlayer(data));
    }, [gameId])

    function fetchPlayer(gameId, color) {
        return fetch(base + gameId + "/players/" + color)
            .then(res => res.json())
    }

    function startNewGame() {
        fetch("http://localhost:9000/chessboard", {method: 'POST'})
            .then(res => res.json())
            .then(id => setGameId(id))
            .catch(err => alert(err))
    }

    function onDrop(sourceSquare, targetSquare) {
        fetch(base + gameId + "/move/" + sourceSquare + "/to/" + targetSquare, {method: 'POST'})
            .then(res => {
                if (res.ok) {
                    setTimeout(() => endTurn(), 2000)
                    fetchGame()
                }
                else alert(res.statusText)
            })
    }

    function endTurn() {
        fetch(base + gameId + "/endTurn", {method: 'POST'})
            .then(res => {
                if (res.ok) {
                    setCurrentPlayerColor(oppositeColor(currentPlayerColor))
                    fetchGame()
                }
                else alert(res.statusText)
            })
    }

    function fetchGame() {
        fetch(base + gameId)
            .then(response => response.json())
            .then(data => setGame(data.pieces))
    }

    function oppositeColor(color) {
        return color === "white" ? "black" : "white";
    }

    return (
        <div style={{
            margin: '3rem auto',
            maxWidth: '70vh',
            width: '70vw'
        }}>
            <h1>Tempête sur l'Échiquier</h1>
            <h2>Trait aux {currentPlayerColor}</h2>
                <Player player={currentPlayerColor === "white" ? blackPlayer : whitePlayer} hiddenCards={true}/>
                <Chessboard id="BasicBoard"
                            onPieceDrop={onDrop}
                            position={game}
                            boardOrientation={currentPlayerColor}
                />
                <Player player={currentPlayerColor === "black" ? blackPlayer : whitePlayer} hiddenCards={false}/>
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
