import {Chessboard} from "react-chessboard";
import {useEffect, useState} from "react";
import {Player} from "./component/Player";
import {CardParameters} from "./component/CardParameters";
import {toast, ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';

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

    function loadCustomPieces() {
        const requirePiece = require.context('./component/pieces', false, /\.js$/);
        const pieces = {};

        requirePiece.keys().forEach(fileName => {
            const pieceName = fileName.replace('./', '').replace('.js', '');
            pieces[pieceName] = Object.values(requirePiece(fileName))[0];
        });

        return pieces;
    }
    const customPieces = loadCustomPieces();
    function customSquares() {
        const squares = {};
        const loadEffect = require.context('./component/effects', false, /\.js$/);

        effects.forEach(effect => {
            const effectFileName = `./${effect.name}.js`;

            if (loadEffect.keys().includes(effectFileName)) {
                const effectConfig = Object.values(loadEffect(effectFileName))[0];
                effect.positions.forEach(position => {
                    squares[position] = effectConfig.applyStyle(position);
                });
            }
        });
        return squares;
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

    async function undo() {
        const res = await fetch(base + gameId + "/undo", {method: 'POST'})
        if (res.ok) {
            setSelectedCard(null)
            setSelectedParam(null)
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
                setBlackPlayer(data.blackPlayer)
                setWhitePlayer(data.whitePlayer)
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

    return (
        <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            minHeight: '100vh',
            padding: '1rem',
            gap: '2rem',
            width: '100%',
        }}>
            <ToastContainer
                position="top-right"
                closeOnClick
                pauseOnFocusLoss
                draggable
                pauseOnHover
                autoClose={3000}
            />

            {/* Colonne centrale : Cartes + Échiquier + Cartes */}
            <div style={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                gap: '0.5rem',
            }}>
                {/* Cartes adversaire en haut (petites) */}
                <Player
                    player={currentPlayerColor === "white" ? blackPlayer : whitePlayer}
                    showCard={showCard}
                    hiddenCards={true}
                />

                {/* Échiquier au centre */}
                <Chessboard
                    id="BasicBoard"
                    onPieceDrop={movePiece}
                    position={game}
                    arePiecesDraggable={selectedCard === null}
                    boardOrientation={currentPlayerColor}
                    onSquareRightClick={onSquareRightClick}
                    customPieces={customPieces}
                    customSquareStyles={{
                        ...customSquares(),
                        ...(selectedCard && selectedParam && [...Object.values(selectedCard.param)].reduce((obj, square) => ({
                            ...obj,
                            [square]: highlight
                        }), {}))
                    }}
                />

                {/* Cartes joueur en bas (petites) */}
                <Player
                    player={currentPlayerColor === "black" ? blackPlayer : whitePlayer}
                    showCard={showCard}
                    hiddenCards={false}
                />

                {/* Boutons sous les cartes */}
                <div style={{
                    display: 'flex',
                    gap: '0.5rem',
                    marginTop: '1rem'
                }}>
                    <button onClick={startNewGame}>
                        Commencer une nouvelle partie
                    </button>
                    <button onClick={endTurn}>
                        Passer son Tour
                    </button>
                    <button onClick={undo}>
                        Annuler
                    </button>
                </div>
            </div>

            {/* Carte sélectionnée à droite en grand */}
            {selectedCard && (
                <div style={{
                    width: '600px',
                    height: '600px',
                    flexShrink: 0
                }}>
                    <CardParameters
                        card={selectedCard}
                        selectedParam={selectedParam}
                        setSelectedParam={setSelectedParam}
                        playCardCallback={playCard}
                    />
                </div>
            )}
        </div>
    );
}
