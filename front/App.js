import {Chessboard} from "react-chessboard";
import {useEffect, useState} from "react";
import {Player} from "./component/Player";
import {CardParameters} from "./component/CardParameters";
import {toast, ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
// Add CSS animation for rotating black hole
const style = document.createElement('style');
style.textContent = `
    @keyframes rotate {
        from { transform: rotate(0deg); }
        to { transform: rotate(360deg); }
    }
`;
document.head.appendChild(style);
const base = "http://localhost:9000/chessboard/";

const highlight = {boxShadow: "rgba(255, 0, 0, 0.75) 0px 0px 20px 0px inset"};

async function showErrorMessage(res) {
    const errorMessage = (await res.text());
    toast.error(errorMessage);
}

export default function App() {

    const [game, setGame] = useState({});
    const [gameId, setGameId] = useState(1);
    const [currentPlayerColor, setCurrentPlayerColor] = useState("white");
    const [whitePlayer, setWhitePlayer] = useState({cards: []});
    const [blackPlayer, setBlackPlayer] = useState({cards: []});
    const [selectedCard, setSelectedCard] = useState(null)
    const [selectedParam, setSelectedParam] = useState(null)
    const [effects, setEffects] = useState([])

    const customPieces = {
        wKangaroo: ({ squareWidth }) => (
            <img
                src={require('./assets/images/wKangaroo.svg')}
                alt="White Kangaroo"
                style={{
                    width: squareWidth * 0.85,
                    height: squareWidth * 0.85,
                    padding: squareWidth * 0.075
                }}
            />
        ),
        bKangaroo: ({ squareWidth }) => (
            <img
                src={require('./assets/images/bKangaroo.svg')}
                alt="Black Kangaroo"
                style={{
                    width: squareWidth * 0.85,
                    height: squareWidth * 0.85,
                    padding: squareWidth * 0.075
                }}
            />
        ),
        BlackHole: ({ squareWidth }) => (
            <img
                src={require('./assets/images/BlackHole.png')}
                alt="BlackHole"
                style={{
                    width: squareWidth,
                    height: squareWidth,
                    animation: 'rotate 60s linear infinite'
                }}
            />
        )
    }


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
            // setTimeout(() => endTurn(), 2000) // FIXME find prettier way to automatically end turn
            fetchGame()
        } else await showErrorMessage(res);
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
            // setTimeout(() => endTurn(), 2000) // FIXME find prettier way to automatically end turn
            fetchGame()
        } else await showErrorMessage(res);
    }

    async function endTurn() {
        const res = await fetch(base + gameId + "/endTurn", {method: 'POST'})
        if (res.ok) {
            setCurrentPlayerColor(oppositeColor(currentPlayerColor))
            setSelectedCard(null)
            fetchGame()
        } else await showErrorMessage(res);
    }

    function fetchGame() {
        fetch(base + gameId)
            .then(response => response.json())
            .then(data => {
                setGame(data.pieces)
                setCurrentPlayerColor(data.currentTurn)
                setEffects(data.effects || [])
            })
    }

    function oppositeColor(color) {
        return color === "white" ? "black" : "white";
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

    function customSquares() {
        const squares = {};

        effects.forEach(effect => {
            if (effect.name === "ManHoleEffect") {
                for (const position of effect.positions) {
                    squares[position] = {
                        backgroundImage: `url(${require('./assets/images/ManHoleEffect.png')})`,
                        backgroundSize: 'cover'
                    };
                }
            }
        });

        return squares;
    }

    return (
        <div style={{
            margin: '3rem auto',
            maxWidth: '70vh',
            width: '70vw'
        }}>
            <ToastContainer
                position="top-right"
                closeOnClick
                pauseOnFocusLoss
                draggable
                pauseOnHover
                autoClose={3000}
            />
            <h1>Tempête sur l'Échiquier</h1>
            <h2>Trait aux {currentPlayerColor}</h2>
            <Player player={currentPlayerColor === "white" ? blackPlayer : whitePlayer} showCard={showCard}
                    hiddenCards={true}/>
            <Chessboard id="BasicBoard"
                        onPieceDrop={movePiece}
                        position={game}
                        arePiecesDraggable={selectedCard === null}
                        boardOrientation={currentPlayerColor}
                        onSquareRightClick={onSquareRightClick}
                        customPieces={customPieces}
                        customSquareStyles={{
                            ...customSquares(),  // Toujours appliqué
                            ...(selectedCard && selectedParam && [...Object.values(selectedCard.param)].reduce((obj, square) => ({
                                ...obj,
                                [square]: highlight
                            }), {}))
                        }}
            />
            {selectedCard && <CardParameters card={selectedCard} selectedParam={selectedParam} setSelectedParam={setSelectedParam} playCardCallback={playCard}/>}
            <Player player={currentPlayerColor === "black" ? blackPlayer : whitePlayer} showCard={showCard}
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
