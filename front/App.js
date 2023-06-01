import {Chessboard} from "react-chessboard";
import {useEffect, useState} from "react";
import {Player} from "./Player";
import {Card} from "./Card";

const base = "http://localhost:9000/chessboard/";

const highlight = {boxShadow: "rgba(255, 0, 0, 0.75) 0px 0px 20px 0px inset"};
export default function App() {

    const [game, setGame] = useState({});
    const [gameId, setGameId] = useState(1);
    const [currentPlayerColor, setCurrentPlayerColor] = useState("WHITE");
    const [whitePlayer, setWhitePlayer] = useState({cards: []});
    const [blackPlayer, setBlackPlayer] = useState({cards: []});
    const [selectedCard, setSelectedCard] = useState(null)
    const [selectedParam, setSelectedParam] = useState(null)


    useEffect(() => { // FIXME use framework tool to fetch data on startup
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

    async function movePiece(sourceSquare, targetSquare) {
        const res = await fetch(base + gameId + "/move/" + sourceSquare + "/to/" + targetSquare, {method: 'POST'})
        if (res.ok) {
            setTimeout(() => endTurn(), 2000) // FIXME find prettier way to automatically end turn
            fetchGame()
        } else alert((await res.json()).message)
    }

    async function playCard(sourceSquare, targetSquare) {
        let params = {
            method: 'POST',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(selectedCard.param)
        };
        const res = await fetch(base + gameId + "/card/" + selectedCard.name, params)
        if (res.ok) {
            setSelectedCard(null)
            setSelectedParam(null)
            setTimeout(() => endTurn(), 2000) // FIXME find prettier way to automatically end turn
            fetchGame()
        } else alert((await res.json()).message)
    }

    async function endTurn() {
        const res = await fetch(base + gameId + "/endTurn", {method: 'POST'})
        if (res.ok) {
            setCurrentPlayerColor(oppositeColor(currentPlayerColor))
            setSelectedCard(null)
            fetchGame()
        } else alert((await res.json()).message)
    }

    function fetchGame() {
        fetch(base + gameId)
            .then(response => response.json())
            .then(data => setGame(data.pieces))
    }

    function oppositeColor(color) {
        return color === "WHITE" ? "BLACK" : "WHITE";
    }

    function showCard(card) {
        setSelectedCard(card !== selectedCard ? card : null)
        // setSelectedSquares(new Set())
    }

    function onSquareRightClick(square) {
        if (selectedCard && selectedParam) {
            if (selectedCard.param[selectedParam] === square) selectedCard.param[selectedParam] = null
            else selectedCard.param[selectedParam] = square
            setSelectedCard({...selectedCard})
        }
    }

    return (
        <div style={{
            margin: '3rem auto',
            maxWidth: '70vh',
            width: '70vw'
        }}>
            <h1>Tempête sur l'Échiquier</h1>
            <h2>Trait aux {currentPlayerColor}</h2>
            <Player player={currentPlayerColor === "WHITE" ? blackPlayer : whitePlayer} showCard={showCard}
                    hiddenCards={true}/>
            <Chessboard id="BasicBoard"
                        onPieceDrop={movePiece}
                        position={game}
                        arePiecesDraggable={selectedCard === null}
                        boardOrientation={currentPlayerColor}
                        onSquareRightClick={onSquareRightClick}
                        customSquareStyles={selectedCard && selectedParam && [...Object.values(selectedCard.param)].reduce((obj, square) => ({
                            ...obj,
                            [square]: highlight
                        }), {})}
            />
            {selectedCard && <Card card={selectedCard} selectedParam={selectedParam} setSelectedParam={setSelectedParam} playCardCallback={playCard}/>}
            <Player player={currentPlayerColor === "BLACK" ? blackPlayer : whitePlayer} showCard={showCard}
                    hiddenCards={false}/>
            <button
                onClick={startNewGame}
            >
                Commencer une nouvelle partie
            </button>
            <button
                onClick={endTurn}
            >
                Passer son Tour
            </button>
        </div>
    );
}
