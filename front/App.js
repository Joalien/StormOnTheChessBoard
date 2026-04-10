import {Chessboard} from "react-chessboard";
import {useEffect, useState} from "react";
import {Player} from "./component/Player";
import {CardParameters} from "./component/CardParameters";
import {barricadeLines, BarricadeSelectionOverlay} from "./component/barricadeOverlay";
import {toast, ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';

const base = "http://localhost:9000/chessboard/";
const highlight = {boxShadow: "rgba(212, 168, 67, 0.85) 0px 0px 24px 0px inset"};

const globalCSS = `
  @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

  *, *::before, *::after { box-sizing: border-box; }

  html, body, #root {
    margin: 0;
    padding: 0;
    min-height: 100vh;
    background: #0d1117;
    font-family: 'Inter', ui-sans-serif, system-ui, -apple-system, 'Segoe UI', sans-serif;
    color: #e6edf3;
  }

  .sotc-btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    padding: 9px 18px;
    border: 1px solid rgba(255,255,255,0.1);
    border-radius: 8px;
    background: rgba(255,255,255,0.05);
    color: #e6edf3;
    font-family: inherit;
    font-size: 13px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.18s ease;
    white-space: nowrap;
    letter-spacing: 0.2px;
    line-height: 1;
  }
  .sotc-btn:hover {
    background: rgba(255,255,255,0.1);
    border-color: rgba(255,255,255,0.18);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.3);
  }
  .sotc-btn:active { transform: translateY(0); }

  .sotc-btn-gold {
    background: linear-gradient(135deg, #c9963a, #e8b84b);
    border-color: transparent;
    color: #0d1117;
    font-weight: 700;
    box-shadow: 0 2px 8px rgba(200,150,50,0.25);
  }
  .sotc-btn-gold:hover {
    background: linear-gradient(135deg, #d4a843, #f0c860);
    box-shadow: 0 6px 20px rgba(212,168,67,0.45);
    transform: translateY(-1px);
  }
  .sotc-btn-gold:disabled {
    opacity: 0.35;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
    background: rgba(255,255,255,0.08);
    color: #8b949e;
    border-color: rgba(255,255,255,0.08);
  }

  .sotc-btn-end {
    border-color: rgba(63,185,80,0.3);
    color: #3fb950;
  }
  .sotc-btn-end:hover {
    background: rgba(63,185,80,0.1);
    border-color: rgba(63,185,80,0.5);
    box-shadow: 0 4px 12px rgba(63,185,80,0.2);
  }

  .sotc-btn-danger {
    border-color: rgba(248,81,73,0.25);
    color: #f85149;
  }
  .sotc-btn-danger:hover {
    background: rgba(248,81,73,0.08);
    border-color: rgba(248,81,73,0.45);
    box-shadow: 0 4px 12px rgba(248,81,73,0.15);
  }

  .sotc-panel {
    background: rgba(22,27,34,0.85);
    border: 1px solid rgba(255,255,255,0.07);
    border-radius: 16px;
    backdrop-filter: blur(16px);
  }

  .sotc-card-wrapper {
    cursor: pointer;
    border-radius: 8px;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
    position: relative;
    display: inline-block;
    flex-shrink: 0;
  }
  .sotc-card-wrapper:hover {
    transform: translateY(-9px) scale(1.05);
    z-index: 10;
    box-shadow: 0 16px 32px rgba(0,0,0,0.55);
  }
  .sotc-card-wrapper.selected {
    transform: translateY(-9px) scale(1.05);
    z-index: 10;
    box-shadow: 0 0 0 2px #d4a843, 0 0 24px rgba(212,168,67,0.55), 0 16px 32px rgba(0,0,0,0.5);
  }
  .sotc-card-hidden {
    opacity: 0.7;
    pointer-events: none;
    display: inline-block;
    flex-shrink: 0;
    border-radius: 8px;
  }

  .sotc-turn-indicator {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 7px 16px;
    border-radius: 20px;
    font-size: 13px;
    font-weight: 600;
    letter-spacing: 0.3px;
    transition: all 0.4s ease;
  }
  .sotc-turn-indicator .dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    animation: pulse 2s ease-in-out infinite;
  }
  @keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.5; }
  }
  .sotc-turn-white {
    background: rgba(230,237,243,0.07);
    border: 1px solid rgba(230,237,243,0.16);
    color: #e6edf3;
  }
  .sotc-turn-white .dot {
    background: #e6edf3;
    box-shadow: 0 0 8px rgba(230,237,243,0.8);
  }
  .sotc-turn-black {
    background: rgba(167,139,250,0.07);
    border: 1px solid rgba(167,139,250,0.22);
    color: #a78bfa;
  }
  .sotc-turn-black .dot {
    background: #a78bfa;
    box-shadow: 0 0 8px rgba(167,139,250,0.8);
  }

  .param-row {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px 12px;
    border-radius: 10px;
    border: 1px solid rgba(255,255,255,0.06);
    background: rgba(255,255,255,0.02);
    cursor: pointer;
    transition: all 0.15s ease;
    margin-bottom: 8px;
    width: 100%;
    text-align: left;
  }
  .param-row:hover { background: rgba(255,255,255,0.05); border-color: rgba(255,255,255,0.12); }
  .param-row.active { background: rgba(212,168,67,0.07); border-color: rgba(212,168,67,0.28); }

  input[type="radio"].param-radio {
    appearance: none;
    -webkit-appearance: none;
    width: 16px;
    height: 16px;
    border: 2px solid rgba(255,255,255,0.25);
    border-radius: 50%;
    cursor: pointer;
    transition: all 0.15s;
    flex-shrink: 0;
    margin: 0;
  }
  input[type="radio"].param-radio:checked {
    background: #d4a843;
    border-color: #d4a843;
    box-shadow: 0 0 8px rgba(212,168,67,0.5);
  }

  .type-badge {
    display: inline-block;
    padding: 3px 10px;
    border-radius: 10px;
    font-size: 10px;
    font-weight: 700;
    letter-spacing: 0.7px;
    text-transform: uppercase;
  }
  .type-BEFORE_TURN  { background: rgba(88,166,255,0.12); color: #58a6ff; border: 1px solid rgba(88,166,255,0.28); }
  .type-AFTER_TURN   { background: rgba(63,185,80,0.12);  color: #3fb950; border: 1px solid rgba(63,185,80,0.28); }
  .type-REPLACE_TURN { background: rgba(210,153,34,0.12); color: #d4a843; border: 1px solid rgba(210,153,34,0.28); }
  .type-ENEMY_TURN   { background: rgba(248,81,73,0.12);  color: #f85149; border: 1px solid rgba(248,81,73,0.28); }

  .Toastify__toast {
    border-radius: 10px !important;
    font-family: 'Inter', sans-serif !important;
    background: #1c2128 !important;
    color: #e6edf3 !important;
    border: 1px solid rgba(255,255,255,0.08) !important;
    box-shadow: 0 8px 32px rgba(0,0,0,0.4) !important;
  }
  .Toastify__toast--error { border-color: rgba(248,81,73,0.3) !important; }
  .Toastify__toast--success { border-color: rgba(63,185,80,0.3) !important; }
  .Toastify__close-button { color: #8b949e !important; }
`;

if (typeof document !== 'undefined') {
    const existing = document.getElementById('sotc-global-styles');
    if (!existing) {
        const styleEl = document.createElement('style');
        styleEl.id = 'sotc-global-styles';
        styleEl.innerHTML = globalCSS;
        document.head.appendChild(styleEl);
    }
}

async function showErrorMessage(res) {
    const errorMessage = await res.text();
    toast.error(errorMessage);
}

function getGameIdFromUrl() {
    if (typeof window === 'undefined') return 1;
    const match = window.location.pathname.match(/^\/(\d+)$/);
    return match ? parseInt(match[1], 10) : 1;
}

export default function App() {
    const [game, setGame] = useState({});
    const [gameId, setGameId] = useState(getGameIdFromUrl);
    const [currentPlayerColor, setCurrentPlayerColor] = useState("white");
    const [whitePlayer, setWhitePlayer] = useState({cards: []});
    const [blackPlayer, setBlackPlayer] = useState({cards: []});
    const [selectedCard, setSelectedCard] = useState(null);
    const [selectedParam, setSelectedParam] = useState(null);
    const [effects, setEffects] = useState([]);
    const [pendingPromotions, setPendingPromotions] = useState([]);
    const [promotionSquare, setPromotionSquare] = useState(null);
    const [barricadeEdges, setBarricadeEdges] = useState([]);
    const [checkMateTargets, setCheckMateTargets] = useState([]);

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

    useEffect(() => {
        fetchGame(gameId);
        fetchPlayer(gameId, "white").then(data => setWhitePlayer(data));
        fetchPlayer(gameId, "black").then(data => setBlackPlayer(data));
    }, [gameId]);

    function fetchPlayer(gameId, color) {
        return fetch(base + gameId + "/players/" + color).then(res => res.json());
    }

    function navigateToGame(id) {
        window.history.pushState({}, '', `/${id}`);
        setGameId(id);
    }

    useEffect(() => {
        function onPopState() { setGameId(getGameIdFromUrl()); }
        window.addEventListener('popstate', onPopState);
        return () => window.removeEventListener('popstate', onPopState);
    }, []);

    function startNewGame() {
        fetch("http://localhost:9000/chessboard", {method: 'POST'})
            .then(res => res.json())
            .then(id => navigateToGame(id))
            .catch(err => alert(err));
    }

    async function movePiece(sourceSquare, targetSquare) {
        const res = await fetch(base + gameId + "/move/" + sourceSquare + "/to/" + targetSquare, {method: 'POST'});
        if (res.ok) fetchGame();
        else await showErrorMessage(res);
    }

    async function playCard() {
        const params = {
            method: 'POST',
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(selectedCard.param)
        };
        const res = await fetch(base + gameId + "/card/" + selectedCard.name, params);
        if (res.ok) {
            setSelectedCard(null);
            setSelectedParam(null);
            fetchGame();
        } else await showErrorMessage(res);
    }

    async function endTurn() {
        const res = await fetch(base + gameId + "/endTurn", {method: 'POST'});
        if (res.ok) {
            setCurrentPlayerColor(oppositeColor(currentPlayerColor));
            setSelectedCard(null);
            fetchGame();
        } else await showErrorMessage(res);
    }

    async function undo() {
        const res = await fetch(base + gameId + "/undo", {method: 'POST'});
        if (res.ok) {
            setSelectedCard(null);
            setSelectedParam(null);
            fetchGame();
        } else await showErrorMessage(res);
    }

    function fetchGame() {
        fetch(base + gameId)
            .then(response => response.json())
            .then(data => {
                setGame(data.pieces);
                setCurrentPlayerColor(data.currentTurn);
                setEffects(data.effects || []);
                setBlackPlayer(data.blackPlayer);
                setWhitePlayer(data.whitePlayer);
                setPendingPromotions(data.pendingPromotions || []);
                setCheckMateTargets(data.checkMateTargets || []);
                setPromotionSquare(null);
            });
    }

    async function promote(position, piece) {
        const res = await fetch(base + gameId + "/promote/" + position + "/" + piece, {method: 'POST'});
        if (res.ok) fetchGame();
        else await showErrorMessage(res);
    }

    function onSquareClick(square) {
        if (pendingPromotions.includes(square)) {
            setPromotionSquare(promotionSquare === square ? null : square);
        }
    }

    function squareToCoords(square, orientation) {
        const file = square.charCodeAt(0) - 97; // 'a' = 0
        const rank = parseInt(square[1]) - 1;   // '1' = 0
        const size = 70; // 560 / 8
        let x, y;
        if (orientation === 'white') {
            x = file * size;
            y = (7 - rank) * size;
        } else {
            x = (7 - file) * size;
            y = rank * size;
        }
        return {x, y};
    }

    function oppositeColor(color) {
        return color === "white" ? "black" : "white";
    }

    function isBarricadeCard(card) {
        return card && card.englishName === 'BarricadeCard';
    }

    function firstUnsetParam(card) {
        if (!card || !card.param) return null;
        if (isBarricadeCard(card)) {
            // For barricade, params are grouped: edge1 = from1+to1, edge2 = from2+to2
            if (card.param.from1 === null) return 'from1';
            if (card.param.to1 === null) return 'to1';
            if (card.param.from2 === null) return 'from2';
            if (card.param.to2 === null) return 'to2';
            return null;
        }
        return Object.keys(card.param).find(k => card.param[k] === null) || null;
    }

    function showCard(card) {
        if (card !== selectedCard) {
            setSelectedCard(card);
            setSelectedParam(firstUnsetParam(card));
            setBarricadeEdges([]);
        } else {
            setSelectedCard(null);
            setSelectedParam(null);
            setBarricadeEdges([]);
        }
    }

    function onBarricadeEdgeClick(edge) {
        if (barricadeEdges.length >= 2) return;
        const newEdges = [...barricadeEdges, edge];
        setBarricadeEdges(newEdges);

        // Sync to card params
        const newParam = {...selectedCard.param};
        if (newEdges.length === 1) {
            newParam.from1 = edge[0];
            newParam.to1 = edge[1];
        } else if (newEdges.length === 2) {
            newParam.from2 = edge[0];
            newParam.to2 = edge[1];
        }
        const updated = {...selectedCard, param: newParam};
        setSelectedCard(updated);
        setSelectedParam(firstUnsetParam(updated));
    }

    function onSquareRightClick(square) {
        if (selectedCard && selectedParam && !isBarricadeCard(selectedCard)) {
            const newParam = {...selectedCard.param};
            if (newParam[selectedParam] === square) newParam[selectedParam] = null;
            else newParam[selectedParam] = square;
            const updated = {...selectedCard, param: newParam};
            setSelectedCard(updated);
            setSelectedParam(firstUnsetParam(updated));
        }
    }

    const isWhiteTurn = currentPlayerColor === "white";
    const opponentPlayer = isWhiteTurn ? blackPlayer : whitePlayer;
    const currentPlayer = isWhiteTurn ? whitePlayer : blackPlayer;
    const opponentColor = isWhiteTurn ? "black" : "white";

    const promotionHighlight = {boxShadow: "rgba(248, 81, 73, 0.85) 0px 0px 24px 0px inset", cursor: "pointer"};
    const customSquareStyles = {
        ...customSquares(),
        ...(selectedCard
            ? Object.values(selectedCard.param || {}).reduce((obj, square) => {
                if (square) obj[square] = highlight;
                return obj;
            }, {})
            : {}
        ),
        ...pendingPromotions.reduce((obj, sq) => { obj[sq] = promotionHighlight; return obj; }, {}),
    };

    return (
        <div style={{minHeight: '100vh', display: 'flex', flexDirection: 'column', background: 'linear-gradient(160deg, #0a0d16 0%, #111520 50%, #0d1117 100%)'}}>
            <ToastContainer
                position="top-right"
                closeOnClick
                pauseOnFocusLoss
                draggable
                pauseOnHover
                autoClose={3500}
            />

            {/* ── Header ── */}
            <header style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '0 28px',
                height: '60px',
                borderBottom: '1px solid rgba(255,255,255,0.06)',
                background: 'rgba(10,13,22,0.85)',
                backdropFilter: 'blur(16px)',
                position: 'sticky',
                top: 0,
                zIndex: 100,
                flexShrink: 0,
            }}>
                <div style={{display: 'flex', alignItems: 'center', gap: '10px'}}>
                    <span style={{fontSize: '22px', lineHeight: 1}}>♞</span>
                    <span style={{
                        fontSize: '15px',
                        fontWeight: '700',
                        letterSpacing: '0.8px',
                        textTransform: 'uppercase',
                        background: 'linear-gradient(135deg, #c9963a, #f0c860)',
                        WebkitBackgroundClip: 'text',
                        WebkitTextFillColor: 'transparent',
                    }}>
                        Storm on the Chess Board
                    </span>
                </div>
                <div style={{display: 'flex', gap: '8px', alignItems: 'center'}}>
                    {[1, 2].map(id => (
                        <button key={id} onClick={() => navigateToGame(id)} className="sotc-btn" style={gameId === id ? {borderColor: 'rgba(212,168,67,0.6)', color: '#d4a843'} : {}}>
                            #{id}
                        </button>
                    ))}
                    <button className="sotc-btn sotc-btn-danger" onClick={undo}>↩ Undo</button>
                    <button className="sotc-btn" onClick={startNewGame}>＋ New Game</button>
                </div>
            </header>

            {/* ── Main ── */}
            <main style={{
                display: 'flex',
                alignItems: 'flex-start',
                justifyContent: 'center',
                gap: '20px',
                padding: '28px 20px',
                flex: 1,
            }}>
                {/* Left panel */}
                <aside style={{width: '252px', flexShrink: 0, position: 'sticky', top: '80px', display: 'flex', flexDirection: 'column', gap: '12px'}}>
                    {selectedCard ? (
                        <CardParameters
                            card={selectedCard}
                            selectedParam={selectedParam}
                            setSelectedParam={setSelectedParam}
                            playCardCallback={playCard}
                            barricadeEdges={barricadeEdges}
                            setBarricadeEdges={setBarricadeEdges}
                        />
                    ) : (
                        <div className="sotc-panel" style={{padding: '28px 20px', textAlign: 'center', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '12px'}}>
                            <div style={{fontSize: '36px', opacity: 0.2}}>🃏</div>
                            <p style={{color: '#484f58', fontSize: '13px', lineHeight: '1.7', margin: 0}}>
                                Click one of your cards to view its details and play it.
                            </p>
                        </div>
                    )}

                    <button className="sotc-btn sotc-btn-end" style={{width: '100%', padding: '13px'}} onClick={endTurn}>
                        ✓ End Turn
                    </button>
                </aside>

                {/* Center: board column */}
                <section style={{display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '14px'}}>
                    {/* Turn indicator */}
                    <span className={`sotc-turn-indicator sotc-turn-${currentPlayerColor}`}>
                        <span className="dot"/>
                        {isWhiteTurn ? "White's turn" : "Black's turn"}
                    </span>

                    {/* Opponent cards (face-up for ENEMY_TURN testing) */}
                    <Player
                        player={opponentPlayer}
                        showCard={showCard}
                        hiddenCards={false}
                        color={opponentColor}
                        selectedCard={selectedCard}
                    />

                    {/* Board */}
                    <div style={{position: 'relative'}}>
                        <div style={{
                            borderRadius: '10px',
                            overflow: 'hidden',
                            boxShadow: '0 0 0 1px rgba(255,255,255,0.07), 0 24px 64px rgba(0,0,0,0.65)',
                        }}>
                            <Chessboard
                                id="BasicBoard"
                                boardWidth={560}
                                onPieceDrop={movePiece}
                                position={game}
                                arePiecesDraggable={selectedCard === null && pendingPromotions.length === 0}
                                boardOrientation={currentPlayerColor}
                                onSquareRightClick={onSquareRightClick}
                                onSquareClick={onSquareClick}
                                customPieces={customPieces}
                                customSquareStyles={customSquareStyles}
                                customDarkSquareStyle={{backgroundColor: '#b58863'}}
                                customLightSquareStyle={{backgroundColor: '#f0d9b5'}}
                            />
                        </div>
                        {/* Barricade selection overlay (interactive, when selecting edges) */}
                        {isBarricadeCard(selectedCard) && barricadeEdges.length < 2 && (
                            <BarricadeSelectionOverlay
                                orientation={currentPlayerColor}
                                selectedEdges={barricadeEdges}
                                onEdgeClick={onBarricadeEdgeClick}
                            />
                        )}
                        {/* Barricade selection preview (non-interactive, when both edges selected) */}
                        {isBarricadeCard(selectedCard) && barricadeEdges.length === 2 && (
                            <svg style={{position: 'absolute', top: 0, left: 0, width: 560, height: 560, pointerEvents: 'none', zIndex: 5}}>
                                {barricadeEdges.map((edge, i) => {
                                    const line = barricadeLines({edges: [edge]}, 560, currentPlayerColor)[0];
                                    return line && <line key={i} x1={line.x1} y1={line.y1} x2={line.x2} y2={line.y2} stroke="#3fb950" strokeWidth={6} strokeLinecap="round" />;
                                })}
                            </svg>
                        )}
                        {/* Barricade effect display (existing barricades on the board) */}
                        {effects.filter(e => e.name === 'BarricadeEffect' && e.edges).map((effect, idx) => (
                            <svg key={`barricade-${idx}`} style={{position: 'absolute', top: 0, left: 0, width: 560, height: 560, pointerEvents: 'none', zIndex: 5}}>
                                {barricadeLines(effect, 560, currentPlayerColor).map((line, i) => (
                                    <g key={i}>
                                        <line x1={line.x1} y1={line.y1} x2={line.x2} y2={line.y2} stroke="#8B4513" strokeWidth={8} strokeLinecap="round" />
                                        <line x1={line.x1} y1={line.y1} x2={line.x2} y2={line.y2} stroke="#D2691E" strokeWidth={4} strokeLinecap="round" />
                                    </g>
                                ))}
                            </svg>
                        ))}
                        {/* Crown overlay on non-King checkmate targets */}
                        {checkMateTargets.length > 0 && (
                            <svg style={{position: 'absolute', top: 0, left: 0, width: 560, height: 560, pointerEvents: 'none', zIndex: 6}}>
                                {checkMateTargets.map(sq => {
                                    const {x, y} = squareToCoords(sq, currentPlayerColor);
                                    return (
                                        <g key={`crown-${sq}`} transform={`translate(${x + 23}, ${y + 2})`}>
                                            <path
                                                d="M2 14 L5 6 L9 10 L14 2 L19 10 L23 6 L26 14 Z"
                                                fill="#d4a843"
                                                stroke="#8B6914"
                                                strokeWidth="1"
                                                opacity="0.9"
                                            />
                                        </g>
                                    );
                                })}
                            </svg>
                        )}
                        {promotionSquare && (() => {
                            const {x, y} = squareToCoords(promotionSquare, currentPlayerColor);
                            const isWhitePiece = (game[promotionSquare] || '').startsWith('w');
                            const popupBelow = y < 280;
                            const pieces = [
                                {name: 'ROOK',   symbol: isWhitePiece ? '♖' : '♜'},
                                {name: 'BISHOP', symbol: isWhitePiece ? '♗' : '♝'},
                                {name: 'KNIGHT', symbol: isWhitePiece ? '♘' : '♞'},
                            ];
                            const popupWidth = 160;
                            const clampedLeft = Math.min(Math.max(x - (popupWidth / 2 - 35), 0), 560 - popupWidth);
                            return (
                                <div style={{
                                    position: 'absolute',
                                    left: clampedLeft,
                                    top: popupBelow ? y + 70 : y - 66,
                                    zIndex: 1000,
                                    display: 'flex',
                                    gap: '6px',
                                    background: '#1c2128',
                                    border: '1px solid rgba(255,255,255,0.15)',
                                    borderRadius: '10px',
                                    padding: '8px',
                                    boxShadow: '0 8px 32px rgba(0,0,0,0.7)',
                                }}>
                                    {pieces.map(p => (
                                        <button key={p.name} title={p.name} onClick={() => promote(promotionSquare, p.name)} style={{
                                            background: 'rgba(255,255,255,0.04)',
                                            border: '1px solid rgba(255,255,255,0.1)',
                                            borderRadius: '7px',
                                            color: isWhitePiece ? '#f0d9b5' : '#b58863',
                                            fontSize: '30px',
                                            cursor: 'pointer',
                                            width: '44px',
                                            height: '44px',
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'center',
                                            lineHeight: 1,
                                            transition: 'background 0.15s, transform 0.1s',
                                        }}
                                        onMouseEnter={e => { e.currentTarget.style.background = 'rgba(212,168,67,0.15)'; e.currentTarget.style.transform = 'scale(1.12)'; }}
                                        onMouseLeave={e => { e.currentTarget.style.background = 'rgba(255,255,255,0.04)'; e.currentTarget.style.transform = 'scale(1)'; }}
                                        >
                                            {p.symbol}
                                        </button>
                                    ))}
                                </div>
                            );
                        })()}
                    </div>

                    {/* Current player cards */}
                    <Player
                        player={currentPlayer}
                        showCard={showCard}
                        hiddenCards={false}
                        color={currentPlayerColor}
                        selectedCard={selectedCard}
                    />
                </section>
            </main>
        </div>
    );
}
