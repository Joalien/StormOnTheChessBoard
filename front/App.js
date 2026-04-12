import {Chessboard} from "react-chessboard";
import {useCallback, useEffect, useMemo, useRef, useState} from "react";
import {Image} from "react-native";
import {Player} from "./component/Player";
import {Card} from "./component/Card";
import {CardParameters} from "./component/CardParameters";
import {barricadeLines, BarricadeSelectionOverlay} from "./component/barricadeOverlay";
import {HomeScreen} from "./component/HomeScreen";
import {WaitingScreen} from "./component/WaitingScreen";
import {NotFoundScreen} from "./component/NotFoundScreen";
import {toast, ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';

const backendOrigin = typeof window !== 'undefined'
    ? window.location.origin
    : 'http://localhost:9000';
const wsOrigin = typeof window !== 'undefined'
    ? (window.location.protocol === 'https:' ? 'wss://' : 'ws://') + window.location.host
    : 'ws://localhost:9000';
const base = backendOrigin + "/api/chessboard/";
const highlight = {boxShadow: "rgba(212, 168, 67, 0.85) 0px 0px 24px 0px inset"};

const globalCSS = `
  @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

  *, *::before, *::after { box-sizing: border-box; }

  @keyframes magnetPulse {
    0%, 100% { transform: scale(1); opacity: 0.9; }
    50% { transform: scale(1.04); opacity: 1; }
  }
  @keyframes magN  { 0%,100%{ transform:translateY(0); } 50%{ transform:translateY(-6px); } }
  @keyframes magS  { 0%,100%{ transform:translateY(0); } 50%{ transform:translateY(6px); } }
  @keyframes magE  { 0%,100%{ transform:translateX(0); } 50%{ transform:translateX(6px); } }
  @keyframes magW  { 0%,100%{ transform:translateX(0); } 50%{ transform:translateX(-6px); } }
  @keyframes magNE { 0%,100%{ transform:translate(0,0); } 50%{ transform:translate(4px,-4px); } }
  @keyframes magNW { 0%,100%{ transform:translate(0,0); } 50%{ transform:translate(-4px,-4px); } }
  @keyframes magSE { 0%,100%{ transform:translate(0,0); } 50%{ transform:translate(4px,4px); } }
  @keyframes magSW { 0%,100%{ transform:translate(0,0); } 50%{ transform:translate(-4px,4px); } }

  html, body, #root {
    margin: 0;
    padding: 0;
    width: 100%;
    min-height: 100vh;
    background: #0d1117;
    font-family: 'Inter', ui-sans-serif, system-ui, -apple-system, 'Segoe UI', sans-serif;
    color: #e6edf3;
  }

  /* Override Expo Web container max-width */
  #root > div { max-width: 100% !important; }

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
  .sotc-card-wrapper.disabled {
    opacity: 0.35;
    filter: grayscale(0.6);
    cursor: default;
  }
  .sotc-card-wrapper.disabled:hover {
    transform: none;
    box-shadow: none;
    z-index: auto;
  }
  .sotc-resize-handle { opacity: 0.35; transition: opacity 0.15s; }
  .sotc-resize-handle:hover { opacity: 0.8; }

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

  @media (max-width: 1200px) {
    .sotc-header-title { display: none; }
    .sotc-main { flex-direction: column !important; align-items: center !important; padding: 12px 8px !important; }
    .sotc-aside { width: 100% !important; max-width: 640px; position: static !important; }
    .sotc-aside-right { order: 2; }
    .sotc-board-section { order: 1; flex: unset !important; }
  }

  @media (max-width: 480px) {
    .sotc-btn { padding: 7px 12px; font-size: 12px; }
    .sotc-header { padding: 0 12px !important; }
  }
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

function getRouteFromUrl() {
    if (typeof window === 'undefined') return {page: 'home'};
    const path = window.location.pathname;
    if (path === '/') return {page: 'home'};
    const match = path.match(/^\/chessboard\/(\d+)$/);
    if (match) return {page: 'game', gameId: parseInt(match[1], 10)};
    return {page: '404'};
}

function getPlayerColorFromUrl() {
    if (typeof window === 'undefined') return null;
    const params = new URLSearchParams(window.location.search);
    return params.get('color');
}

export default function App() {
    const [game, setGame] = useState({});
    const [route, setRoute] = useState(getRouteFromUrl);
    const gameId = route.page === 'game' ? route.gameId : null;
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
    const [currentState, setCurrentState] = useState(null);
    const [capturedPieces, setCapturedPieces] = useState([]);
    const [myColor, setMyColor] = useState(getPlayerColorFromUrl);
    const [legalMoves, setLegalMoves] = useState([]);
    const [windowWidth, setWindowWidth] = useState(typeof window !== 'undefined' ? window.innerWidth : 900);
    const [gameIds, setGameIds] = useState([]);
    const [expandedGameId, setExpandedGameId] = useState(null);
    const [matchmakingStats, setMatchmakingStats] = useState(null);
    const matchmakingPollRef = useRef(null);
    const matchmakingTokenRef = useRef(null);
    const [userBoardSize, setUserBoardSize] = useState(() => {
        if (typeof localStorage !== 'undefined') {
            const saved = localStorage.getItem('sotc-board-size');
            if (saved) return parseInt(saved, 10);
        }
        return null;
    });
    useEffect(() => {
        function onResize() { setWindowWidth(window.innerWidth); }
        window.addEventListener('resize', onResize);
        return () => window.removeEventListener('resize', onResize);
    }, []);

    const defaultBoardSize = useMemo(() => {
        const maxBoard = 640;
        const padding = 40;
        if (windowWidth <= 900) return Math.min(maxBoard, windowWidth - padding);
        return Math.min(maxBoard, windowWidth - 380 - padding);
    }, [windowWidth]);

    const boardSize = userBoardSize ? Math.max(280, Math.min(userBoardSize, windowWidth - 380)) : defaultBoardSize;

    const onResizeStart = useCallback((e) => {
        e.preventDefault();
        const clientX = e.touches ? e.touches[0].clientX : e.clientX;
        const clientY = e.touches ? e.touches[0].clientY : e.clientY;
        const startSize = boardSize;

        function onMove(ev) {
            ev.preventDefault();
            const cx = ev.touches ? ev.touches[0].clientX : ev.clientX;
            const cy = ev.touches ? ev.touches[0].clientY : ev.clientY;
            const delta = Math.max(cx - clientX, cy - clientY);
            const newSize = Math.round(Math.max(280, Math.min(startSize + delta, windowWidth - 380)));
            setUserBoardSize(newSize);
            localStorage.setItem('sotc-board-size', String(newSize));
        }
        function onEnd() {
            document.removeEventListener('mousemove', onMove);
            document.removeEventListener('mouseup', onEnd);
            document.removeEventListener('touchmove', onMove);
            document.removeEventListener('touchend', onEnd);
        }
        document.addEventListener('mousemove', onMove);
        document.addEventListener('mouseup', onEnd);
        document.addEventListener('touchmove', onMove, {passive: false});
        document.addEventListener('touchend', onEnd);
    }, [boardSize, windowWidth]);

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
        const white = (myColor || currentPlayerColor) === 'white';
        effects.filter(e => e.name === 'MagnetismEffect').forEach(effect => {
            effect.positions.forEach(pos => {
                const file = pos.charCodeAt(0) - 97;
                const rank = parseInt(pos[1]) - 1;
                const deltas = [[-1,1],[0,1],[1,1],[-1,0],[1,0],[-1,-1],[0,-1],[1,-1]];
                deltas.forEach(([df, dr]) => {
                    const nf = file + df, nr = rank + dr;
                    if (nf < 0 || nf > 7 || nr < 0 || nr > 7) return;
                    const adjSq = String.fromCharCode(97 + nf) + (nr + 1);
                    if (!game[adjSq]) return;
                    const vdx = white ? -df : df;
                    const vdy = white ? dr : -dr;
                    const animMap = {
                        '0,-1': 'magN', '0,1': 'magS', '1,0': 'magE', '-1,0': 'magW',
                        '1,-1': 'magNE', '-1,-1': 'magNW', '1,1': 'magSE', '-1,1': 'magSW',
                    };
                    const anim = animMap[`${vdx},${vdy}`] || '';
                    squares[adjSq] = { animation: `${anim} 4s ease-in-out infinite` };
                });
            });
        });
        return squares;
    }

    useEffect(() => {
        fetch(backendOrigin + '/api/chessboard')
            .then(res => res.json())
            .then(ids => setGameIds(ids.sort((a, b) => a - b)))
            .catch(() => {});
        fetch(backendOrigin + '/api/matchmaking/stats')
            .then(res => res.json())
            .then(setMatchmakingStats)
            .catch(() => {});
    }, []);

    useEffect(() => {
        if (gameId === null) return;
        fetchGame(gameId);
        fetchPlayer(gameId, "white").then(data => setWhitePlayer(data));
        fetchPlayer(gameId, "black").then(data => setBlackPlayer(data));
    }, [gameId]);

    // WebSocket for real-time game updates
    useEffect(() => {
        if (gameId === null) return;
        const ws = new WebSocket(wsOrigin + '/ws/game/' + gameId);
        ws.onmessage = () => fetchGame();
        ws.onclose = () => {};
        return () => ws.close();
    }, [gameId]);

    function fetchPlayer(gameId, color) {
        return fetch(base + gameId + "/players/" + color).then(res => res.json());
    }

    function navigateToGame(id, color = null) {
        if (id === null) {
            window.history.pushState({}, '', '/');
            setRoute({page: 'home'});
            setMyColor(null);
        } else {
            const url = color ? `/chessboard/${id}?color=${color}` : `/chessboard/${id}`;
            window.history.pushState({}, '', url);
            setRoute({page: 'game', gameId: id});
            setMyColor(color);
        }
    }

    useEffect(() => {
        function onPopState() {
            setRoute(getRouteFromUrl());
            setMyColor(getPlayerColorFromUrl());
        }
        window.addEventListener('popstate', onPopState);
        return () => window.removeEventListener('popstate', onPopState);
    }, []);

    useEffect(() => {
        function onKeyDown(e) {
            if (e.code === 'Space' && gameId !== null) {
                if (myColor && myColor !== currentPlayerColor) return;
                e.preventDefault();
                if (selectedCard && !Object.values(selectedCard.param || {}).some(x => x === null)) {
                    playCard();
                } else {
                    endTurn();
                }
            }
            if (e.key === 'z' && (e.ctrlKey || e.metaKey) && gameId !== null) {
                if (myColor && myColor !== currentPlayerColor) return;
                e.preventDefault();
                undo();
            }
        }
        window.addEventListener('keydown', onKeyDown, true);
        return () => window.removeEventListener('keydown', onKeyDown, true);
    }, [gameId, myColor, currentPlayerColor, selectedCard]);

    function startNewGame() {
        fetch(backendOrigin + '/api/chessboard', {method: 'POST'})
            .then(res => res.text())
            .then(text => navigateToGame(parseInt(text, 10)))
            .catch(err => alert(err));
    }

    useEffect(() => {
        function onBeforeUnload() {
            if (matchmakingTokenRef.current) {
                fetch(backendOrigin + '/api/matchmaking/' + matchmakingTokenRef.current, {method: 'DELETE', keepalive: true}).catch(() => {});
            }
        }
        window.addEventListener('beforeunload', onBeforeUnload);
        return () => window.removeEventListener('beforeunload', onBeforeUnload);
    }, []);

    function notifyMatchFound() {
        toast.success("Adversaire trouvé !");
        if (typeof Notification !== 'undefined' && Notification.permission === 'granted') {
            new Notification("Storm on the Chess Board", {body: "Un adversaire a été trouvé !"});
        }
    }

    function matchmaking() {
        setRoute({page: 'waiting'});
        if (typeof Notification !== 'undefined' && Notification.permission === 'default') {
            Notification.requestPermission();
        }
        fetch(backendOrigin + '/api/matchmaking/join', {method: 'POST'})
            .then(res => res.json())
            .then(data => {
                if (data.status === 'matched') {
                    notifyMatchFound();
                    navigateToGame(data.gameId, data.color);
                    return;
                }
                matchmakingTokenRef.current = data.token;
                const ws = new WebSocket(wsOrigin + '/ws/matchmaking/' + data.token);
                matchmakingPollRef.current = ws;
                ws.onmessage = (event) => {
                    const status = JSON.parse(event.data);
                    if (status.status === 'matched') {
                        ws.close();
                        matchmakingPollRef.current = null;
                        matchmakingTokenRef.current = null;
                        notifyMatchFound();
                        navigateToGame(status.gameId, status.color);
                    }
                };
            })
            .catch(err => {
                toast.error("Erreur: " + err);
                navigateToGame(null);
            });
    }

    function cancelMatchmaking() {
        if (matchmakingPollRef.current) {
            matchmakingPollRef.current.close();
            matchmakingPollRef.current = null;
        }
        if (matchmakingTokenRef.current) {
            fetch(backendOrigin + '/api/matchmaking/' + matchmakingTokenRef.current, {method: 'DELETE'}).catch(() => {});
            matchmakingTokenRef.current = null;
        }
        navigateToGame(null);
    }

    function onPieceDragBegin(piece, sourceSquare) {
        fetch(base + gameId + "/legalMoves/" + sourceSquare)
            .then(res => res.json())
            .then(moves => setLegalMoves(moves))
            .catch(() => setLegalMoves([]));
    }

    function onPieceDragEnd() {
        setLegalMoves([]);
    }

    async function movePiece(sourceSquare, targetSquare) {
        setLegalMoves([]);
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
                setCurrentState(data.currentState);
                setCapturedPieces(data.capturedPieces || []);
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
            return;
        }
        // Click on an effect position → select that effect card
        const matchingEffect = effects.find(e => e.cardEnglishName && (
            (e.positions && e.positions.includes(square)) ||
            (e.edges && e.edges.some(edge => edge.includes(square)))
        ));
        if (matchingEffect) {
            const alreadySelected = selectedCard && selectedCard.isEffect && selectedCard.englishName === matchingEffect.cardEnglishName;
            if (alreadySelected) {
                setSelectedCard(null);
                setSelectedParam(null);
            } else {
                showCard({
                    englishName: matchingEffect.cardEnglishName,
                    name: matchingEffect.cardName,
                    description: matchingEffect.cardDescription,
                    param: {},
                    isEffect: true,
                });
            }
        }
    }

    function squareToCoords(square, orientation) {
        const file = square.charCodeAt(0) - 97; // 'a' = 0
        const rank = parseInt(square[1]) - 1;   // '1' = 0
        const size = boardSize / 8;
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

    function playableCardTypes(state, isOpponent) {
        if (isOpponent) return ['ENEMY_TURN'];
        if (myColor && myColor !== currentPlayerColor) return [];
        const map = {
            'BEGINNING_OF_THE_TURN': ['BEFORE_TURN', 'REPLACE_TURN'],
            'BEFORE_MOVE': [],
            'MOVE_WITHOUT_CARD_PLAYED': ['AFTER_TURN'],
            'END_OF_THE_TURN': [],
            'ENEMY_REACTION': [],
            'PROMOTION_PENDING': [],
        };
        return map[state] || [];
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

    function isSelectedCardPlayable() {
        return selectedCard && playableCardTypes(currentState, false).includes(selectedCard.type);
    }

    function onSquareRightClick(square) {
        if (selectedCard && selectedParam && isSelectedCardPlayable() && !isBarricadeCard(selectedCard)) {
            const newParam = {...selectedCard.param};
            if (newParam[selectedParam] === square) newParam[selectedParam] = null;
            else newParam[selectedParam] = square;
            const updated = {...selectedCard, param: newParam};
            setSelectedCard(updated);
            setSelectedParam(firstUnsetParam(updated));
        }
    }

    function onCapturedPieceClick(index) {
        if (selectedCard && selectedParam && isSelectedCardPlayable() && !isBarricadeCard(selectedCard)) {
            const value = "captured:" + index;
            const newParam = {...selectedCard.param};
            if (newParam[selectedParam] === value) newParam[selectedParam] = null;
            else newParam[selectedParam] = value;
            const updated = {...selectedCard, param: newParam};
            setSelectedCard(updated);
            setSelectedParam(firstUnsetParam(updated));
        }
    }

    const isWhiteTurn = currentPlayerColor === "white";
    const isMyTurn = !myColor || myColor === currentPlayerColor;

    // In matchmaking: layout is fixed (my cards at bottom). In solo: follows current turn.
    const bottomColor = myColor || currentPlayerColor;
    const topColor = oppositeColor(bottomColor);
    const bottomPlayer = bottomColor === 'white' ? whitePlayer : blackPlayer;
    const topPlayer = bottomColor === 'white' ? blackPlayer : whitePlayer;

    const promotionHighlight = {boxShadow: "rgba(248, 81, 73, 0.85) 0px 0px 24px 0px inset", cursor: "pointer"};
    const legalMoveDot = {background: "radial-gradient(circle, rgba(0,0,0,0.25) 25%, transparent 25%)"};
    const legalMoveCapture = {
        background: "radial-gradient(circle, transparent 80%, rgba(0,0,0,0.25) 80%)",
    };
    const effectHighlight = { boxShadow: 'rgba(255,143,0,0.85) 0px 0px 28px 4px inset', background: 'rgba(255,143,0,0.15)' };
    const selectedEffectPositions = selectedCard && selectedCard.isEffect
        ? effects
            .filter(e => e.cardEnglishName === selectedCard.englishName)
            .flatMap(e => {
                if (e.positions) return e.positions;
                if (e.edges) return [...new Set(e.edges.flat())];
                return [];
            })
        : [];
    const customSquareStyles = {
        ...customSquares(),
        ...(selectedCard && !selectedCard.isEffect
            ? Object.values(selectedCard.param || {}).reduce((obj, square) => {
                if (square) obj[square] = highlight;
                return obj;
            }, {})
            : {}
        ),
        ...selectedEffectPositions.reduce((obj, sq) => { obj[sq] = effectHighlight; return obj; }, {}),
        ...pendingPromotions.reduce((obj, sq) => { obj[sq] = promotionHighlight; return obj; }, {}),
        ...legalMoves.reduce((obj, sq) => { obj[sq] = game[sq] ? legalMoveCapture : legalMoveDot; return obj; }, {}),
    };

    if (route.page === 'home') {
        return (
            <>
                <ToastContainer position="top-right" closeOnClick pauseOnFocusLoss draggable pauseOnHover autoClose={3500} />
                <HomeScreen onPlaySolo={startNewGame} onMatchmaking={matchmaking} />
            </>
        );
    }

    if (route.page === 'waiting') {
        return (
            <>
                <ToastContainer position="top-right" closeOnClick pauseOnFocusLoss draggable pauseOnHover autoClose={3500} />
                <WaitingScreen onCancel={cancelMatchmaking} />
            </>
        );
    }

    if (route.page === '404') {
        return <NotFoundScreen onGoHome={() => navigateToGame(null)} />;
    }

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
            <header className="sotc-header" style={{
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
                    <span className="sotc-header-title" style={{
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
                <div style={{display: 'flex', gap: '8px', alignItems: 'center', flexWrap: 'wrap'}}>
                    {matchmakingStats && (
                        <div style={{display: 'flex', gap: '10px', alignItems: 'center', marginRight: '6px', padding: '4px 12px', borderRadius: '8px', background: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.07)'}}>
                            <span style={{fontSize: '11px', color: '#8b949e', display: 'flex', alignItems: 'center', gap: '5px'}}>
                                <span style={{fontSize: '13px'}}>⚔</span>
                                <span style={{color: '#e6edf3', fontWeight: 600}}>{matchmakingStats.activeMatchCount}</span> match{matchmakingStats.activeMatchCount !== 1 ? 's' : ''}
                            </span>
                            <span style={{width: '1px', height: '14px', background: 'rgba(255,255,255,0.1)'}}/>
                            <span style={{fontSize: '11px', color: '#8b949e', display: 'flex', alignItems: 'center', gap: '5px'}}>
                                <span style={{fontSize: '13px'}}>⏳</span>
                                <span style={{color: '#e6edf3', fontWeight: 600}}>{matchmakingStats.waitingCount}</span> en file
                            </span>
                        </div>
                    )}
                    {gameIds.map(id => (
                        <div key={id} style={{position: 'relative', display: 'inline-flex'}}>
                            <button
                                className={`sotc-btn${id === gameId && !myColor ? ' sotc-btn-gold' : ''}`}
                                style={{padding: '6px 12px', fontSize: '12px', minWidth: 0}}
                                onClick={() => { setExpandedGameId(null); navigateToGame(id); }}
                                onContextMenu={(e) => { e.preventDefault(); setExpandedGameId(expandedGameId === id ? null : id); }}
                            >
                                #{id}
                            </button>
                            {expandedGameId === id && (
                                <div style={{
                                    position: 'absolute', top: '100%', left: '50%', transform: 'translateX(-50%)',
                                    marginTop: '4px', display: 'flex', gap: '4px', zIndex: 200,
                                    background: 'rgba(10,13,22,0.95)', borderRadius: '8px', padding: '4px',
                                    border: '1px solid rgba(255,255,255,0.1)', boxShadow: '0 8px 24px rgba(0,0,0,0.5)',
                                }}>
                                    <button className="sotc-btn" style={{padding: '5px 8px', fontSize: '11px', color: '#f0d9b5'}}
                                        onClick={() => { setExpandedGameId(null); navigateToGame(id, 'white'); }}>
                                        ♔
                                    </button>
                                    <button className="sotc-btn" style={{padding: '5px 8px', fontSize: '11px', color: '#6a5a4a'}}
                                        onClick={() => { setExpandedGameId(null); navigateToGame(id, 'black'); }}>
                                        ♚
                                    </button>
                                </div>
                            )}
                        </div>
                    ))}
                </div>
                <div style={{display: 'flex', gap: '8px', alignItems: 'center'}}>
                    <button className="sotc-btn" onClick={() => navigateToGame(null)}>⌂ Accueil</button>
                </div>
            </header>

            {/* Dimming overlay when an effect is selected */}
            {selectedCard && selectedCard.isEffect && (
                <div onClick={() => { setSelectedCard(null); setSelectedParam(null); setBarricadeEdges([]); }} style={{
                    position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
                    background: 'rgba(0,0,0,0.7)',
                    zIndex: 50,
                }}/>
            )}

            {/* ── Main ── */}
            <main className="sotc-main" onClick={() => { if (selectedCard) { setSelectedCard(null); setSelectedParam(null); setBarricadeEdges([]); } }} style={{
                display: 'flex',
                alignItems: 'flex-start',
                justifyContent: 'center',
                gap: '20px',
                padding: '28px 20px',
                flex: 1,
            }}>
                {/* Center: board column */}
                <section className="sotc-board-section" style={{display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '14px', flex: 1, minWidth: 0}}>
                    {/* Turn indicator */}
                    <span className={`sotc-turn-indicator sotc-turn-${currentPlayerColor}`}>
                        <span className="dot"/>
                        {isWhiteTurn ? "Tour des Blancs" : "Tour des Noirs"}
                    </span>

                    {/* Top player cards */}
                    <Player
                        player={topPlayer}
                        showCard={showCard}
                        hiddenCards={false}
                        color={topColor}
                        selectedCard={selectedCard}
                        playableTypes={playableCardTypes(currentState, true)}
                    />

                    {/* Board */}
                    <div style={{position: 'relative', zIndex: selectedCard && selectedCard.isEffect ? 51 : undefined}}>
                        <div style={{
                            borderRadius: '10px',
                            overflow: 'hidden',
                            boxShadow: '0 0 0 1px rgba(255,255,255,0.07), 0 24px 64px rgba(0,0,0,0.65)',
                        }}>
                            <Chessboard
                                id="BasicBoard"
                                boardWidth={boardSize}
                                onPieceDrop={movePiece}
                                onPieceDragBegin={onPieceDragBegin}
                                onPieceDragEnd={onPieceDragEnd}
                                position={game}
                                arePiecesDraggable={pendingPromotions.length === 0 && isMyTurn}
                                boardOrientation={myColor || currentPlayerColor}
                                onSquareRightClick={onSquareRightClick}
                                onSquareClick={onSquareClick}
                                customPieces={customPieces}
                                customSquareStyles={customSquareStyles}
                                customDarkSquareStyle={{backgroundColor: '#b58863'}}
                                customLightSquareStyle={{backgroundColor: '#f0d9b5'}}
                            />
                        </div>
                        {/* Barricade selection overlay (interactive, when selecting edges) */}
                        {isBarricadeCard(selectedCard) && isSelectedCardPlayable() && barricadeEdges.length < 2 && (
                            <BarricadeSelectionOverlay
                                orientation={currentPlayerColor}
                                selectedEdges={barricadeEdges}
                                onEdgeClick={onBarricadeEdgeClick}
                                boardSize={boardSize}
                            />
                        )}
                        {/* Barricade selection preview (non-interactive, when both edges selected) */}
                        {isBarricadeCard(selectedCard) && barricadeEdges.length === 2 && (
                            <svg style={{position: 'absolute', top: 0, left: 0, width: boardSize, height: boardSize, pointerEvents: 'none', zIndex: 5}}>
                                {barricadeEdges.map((edge, i) => {
                                    const line = barricadeLines({edges: [edge]}, boardSize, currentPlayerColor)[0];
                                    return line && <line key={i} x1={line.x1} y1={line.y1} x2={line.x2} y2={line.y2} stroke="#3fb950" strokeWidth={6} strokeLinecap="round" />;
                                })}
                            </svg>
                        )}
                        {/* Barricade effect display (existing barricades on the board) */}
                        {effects.filter(e => e.name === 'BarricadeEffect' && e.edges).map((effect, idx) => {
                            const clickable = !!effect.cardEnglishName;
                            const firstSquare = effect.edges[0] && effect.edges[0][0];
                            return (
                                <svg key={`barricade-${idx}`} style={{position: 'absolute', top: 0, left: 0, width: boardSize, height: boardSize, pointerEvents: 'none', zIndex: 5}}>
                                    {barricadeLines(effect, boardSize, currentPlayerColor).map((line, i) => (
                                        <g key={i} onClick={clickable && firstSquare ? () => onSquareClick(firstSquare) : undefined} style={{pointerEvents: clickable ? 'auto' : 'none', cursor: clickable ? 'pointer' : 'default'}}>
                                            <line x1={line.x1} y1={line.y1} x2={line.x2} y2={line.y2} stroke="transparent" strokeWidth={16} strokeLinecap="round" />
                                            <line x1={line.x1} y1={line.y1} x2={line.x2} y2={line.y2} stroke="#BF360C" strokeWidth={8} strokeLinecap="round" />
                                            <line x1={line.x1} y1={line.y1} x2={line.x2} y2={line.y2} stroke="#FFB300" strokeWidth={4} strokeLinecap="round" />
                                        </g>
                                    ))}
                                </svg>
                            );
                        })}
                        {/* Magnetism: center magnets + adjacent pulse overlay */}
                        {effects.filter(e => e.name === 'MagnetismEffect' && e.positions && e.positions.length > 0).map((effect, idx) => {
                            const sqSize = boardSize / 8;
                            const orientation = myColor || currentPlayerColor;
                            const white = orientation === 'white';
                            const elements = [];
                            effect.positions.forEach((pos, pi) => {
                                const {x: cx, y: cy} = squareToCoords(pos, orientation);
                                const magSize = sqSize * 0.16;
                                const magStyle = { position: 'absolute', fontSize: magSize, lineHeight: 1 };
                                const magClickable = !!effect.cardEnglishName;
                                elements.push(
                                    <div key={`mag-center-${idx}-${pi}`} onClick={magClickable ? () => onSquareClick(pos) : undefined} style={{
                                        position: 'absolute',
                                        left: cx, top: cy,
                                        width: sqSize, height: sqSize,
                                        pointerEvents: magClickable ? 'auto' : 'none',
                                        cursor: magClickable ? 'pointer' : 'default',
                                        zIndex: 6,
                                        animation: 'magnetPulse 4s ease-in-out infinite',
                                    }}>
                                        <span style={{ ...magStyle, top: 2, left: 2, transform: 'rotate(-135deg)' }}>🧲</span>
                                        <span style={{ ...magStyle, top: 2, right: 2, transform: 'rotate(-45deg)' }}>🧲</span>
                                        <span style={{ ...magStyle, bottom: 2, left: 2, transform: 'rotate(135deg)' }}>🧲</span>
                                        <span style={{ ...magStyle, bottom: 2, right: 2, transform: 'rotate(45deg)' }}>🧲</span>
                                    </div>
                                );
                                const file = pos.charCodeAt(0) - 97;
                                const rank = parseInt(pos[1]) - 1;
                                const deltas = [[-1,-1],[-1,0],[-1,1],[0,-1],[0,1],[1,-1],[1,0],[1,1]];
                                deltas.forEach(([df, dr], di) => {
                                    const nf = file + df, nr = rank + dr;
                                    if (nf < 0 || nf > 7 || nr < 0 || nr > 7) return;
                                    const adjSq = String.fromCharCode(97 + nf) + (nr + 1);
                                    if (!game[adjSq]) return;
                                    const {x: ax, y: ay} = squareToCoords(adjSq, orientation);
                                    const vdx = white ? -df : df;
                                    const vdy = white ? dr : -dr;
                                    const animMap = {
                                        '0,-1': 'magN', '0,1': 'magS', '1,0': 'magE', '-1,0': 'magW',
                                        '1,-1': 'magNE', '-1,-1': 'magNW', '1,1': 'magSE', '-1,1': 'magSW',
                                    };
                                    const anim = animMap[`${vdx},${vdy}`] || '';
                                    elements.push(
                                        <div key={`mag-adj-${idx}-${pi}-${di}`} style={{
                                            position: 'absolute',
                                            left: ax, top: ay,
                                            width: sqSize, height: sqSize,
                                            pointerEvents: 'none',
                                            zIndex: 4,
                                            animation: `${anim} 4s ease-in-out infinite`,
                                        }}/>
                                    );
                                });
                            });
                            return elements;
                        })}
                        {/* Other effect overlays (rendered above pieces) */}
                        {effects.filter(e => e.name !== 'BarricadeEffect' && e.name !== 'MagnetismEffect' && e.positions && e.positions.length > 0).map((effect, idx) => {
                            const loadEffect = require.context('./component/effects', false, /\.js$/);
                            const effectFileName = `./${effect.name}.js`;
                            if (!loadEffect.keys().includes(effectFileName)) return null;
                            const effectConfig = Object.values(loadEffect(effectFileName))[0];
                            const sqSize = boardSize / 8;
                            const clickable = !!effect.cardEnglishName;
                            return effect.positions.map((pos, pi) => {
                                const {x, y} = squareToCoords(pos, currentPlayerColor);
                                const style = effectConfig.applyStyle(pos);
                                return (
                                    <div key={`eff-${idx}-${pi}`} onClick={clickable ? () => onSquareClick(pos) : undefined} style={{
                                        position: 'absolute',
                                        left: x, top: y,
                                        width: sqSize, height: sqSize,
                                        pointerEvents: clickable ? 'auto' : 'none',
                                        cursor: clickable ? 'pointer' : 'default',
                                        zIndex: 5,
                                        ...style,
                                    }}/>
                                );
                            });
                        })}
                        {/* Crown overlay on non-King checkmate targets */}
                        {checkMateTargets.length > 0 && (
                            <svg style={{position: 'absolute', top: 0, left: 0, width: boardSize, height: boardSize, pointerEvents: 'none', zIndex: 6}}>
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
                            const popupBelow = y < boardSize / 2;
                            const pieces = [
                                {name: 'ROOK',   symbol: isWhitePiece ? '♖' : '♜'},
                                {name: 'BISHOP', symbol: isWhitePiece ? '♗' : '♝'},
                                {name: 'KNIGHT', symbol: isWhitePiece ? '♘' : '♞'},
                            ];
                            const popupWidth = 160;
                            const clampedLeft = Math.min(Math.max(x - (popupWidth / 2 - 35), 0), boardSize - popupWidth);
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
                        {/* Spotlight overlay on board when effect selected */}
                        {selectedCard && selectedCard.isEffect && selectedEffectPositions.length > 0 && (() => {
                            const sqSize = boardSize / 8;
                            const orientation = myColor || currentPlayerColor;
                            const centers = selectedEffectPositions.map(sq => {
                                const {x, y} = squareToCoords(sq, orientation);
                                return {cx: x + sqSize / 2, cy: y + sqSize / 2};
                            });
                            // Group all positions into a single spotlight: barycenter + radius covering all
                            const avgX = centers.reduce((s, c) => s + c.cx, 0) / centers.length;
                            const avgY = centers.reduce((s, c) => s + c.cy, 0) / centers.length;
                            const maxDist = Math.max(...centers.map(c => Math.hypot(c.cx - avgX, c.cy - avgY)));
                            const r = maxDist + sqSize * 1.8;
                            return (
                                <svg style={{position: 'absolute', top: 0, left: 0, width: boardSize, height: boardSize, pointerEvents: 'none', zIndex: 8, borderRadius: '10px'}}>
                                    <defs>
                                        <radialGradient id="spotGrad">
                                            <stop offset="0%" stopColor="black"/>
                                            <stop offset="40%" stopColor="black" stopOpacity="0.9"/>
                                            <stop offset="100%" stopColor="white"/>
                                        </radialGradient>
                                        <mask id="spotlightMask">
                                            <rect width={boardSize} height={boardSize} fill="white"/>
                                            <circle cx={avgX} cy={avgY} r={r} fill="url(#spotGrad)"/>
                                        </mask>
                                    </defs>
                                    <rect width={boardSize} height={boardSize} fill="black" opacity="0.65" mask="url(#spotlightMask)"/>
                                </svg>
                            );
                        })()}
                        {/* Resize handle */}
                        <div
                            className="sotc-resize-handle"
                            onMouseDown={onResizeStart}
                            onTouchStart={onResizeStart}
                            style={{
                                position: 'absolute',
                                right: -6,
                                bottom: -6,
                                width: 22,
                                height: 22,
                                cursor: 'nwse-resize',
                                zIndex: 20,
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                            }}
                        >
                            <svg width="12" height="12" viewBox="0 0 12 12">
                                <line x1="11" y1="1" x2="1" y2="11" stroke="#e6edf3" strokeWidth="1.5" strokeLinecap="round"/>
                                <line x1="11" y1="5" x2="5" y2="11" stroke="#e6edf3" strokeWidth="1.5" strokeLinecap="round"/>
                                <line x1="11" y1="9" x2="9" y2="11" stroke="#e6edf3" strokeWidth="1.5" strokeLinecap="round"/>
                            </svg>
                        </div>
                    </div>

                    {/* Bottom player cards */}
                    <Player
                        player={bottomPlayer}
                        showCard={showCard}
                        hiddenCards={false}
                        color={bottomColor}
                        selectedCard={selectedCard}
                        playableTypes={playableCardTypes(currentState, false)}
                        large
                    />
                </section>

                {/* Right panel: Actions + Card details + Effects + Captured */}
                <aside className="sotc-aside sotc-aside-right" onClick={e => e.stopPropagation()} style={{width: '320px', flexShrink: 0, position: 'sticky', top: '80px', display: 'flex', flexDirection: 'column', gap: '12px', zIndex: selectedCard && selectedCard.isEffect ? 51 : undefined}}>
                    {/* Action buttons */}
                    <div style={{display: 'flex', gap: '8px'}}>
                        <button className="sotc-btn sotc-btn-end" style={{flex: 1, padding: '13px'}} onClick={endTurn} disabled={!isMyTurn}>
                            ✓ Fin du tour
                        </button>
                        <button className="sotc-btn sotc-btn-danger" style={{flex: 1, padding: '13px'}} onClick={undo} disabled={!isMyTurn}>
                            ↩ Annuler
                        </button>
                    </div>

                    {/* Card details */}
                    {selectedCard ? (
                        <CardParameters
                            card={selectedCard}
                            selectedParam={selectedParam}
                            setSelectedParam={setSelectedParam}
                            playCardCallback={playCard}
                            barricadeEdges={barricadeEdges}
                            setBarricadeEdges={setBarricadeEdges}
                            isPlayable={!selectedCard.isEffect && playableCardTypes(currentState, false).includes(selectedCard.type)}
                        />
                    ) : (
                        <div className="sotc-panel" style={{padding: '28px 20px', textAlign: 'center', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '12px'}}>
                            <Image source={require('./assets/images/cards/back.png')} style={{width: 80, height: 116, borderRadius: 6, opacity: 0.25}}/>
                            <p style={{color: '#484f58', fontSize: '13px', lineHeight: '1.7', margin: 0}}>
                                Cliquez sur une de vos cartes pour voir ses détails et la jouer.
                            </p>
                        </div>
                    )}

                    {/* Active effects (hidden when a hand card is selected) */}
                    {!(selectedCard && !selectedCard.isEffect) && effects.some(e => e.cardEnglishName) && (
                        <div className="sotc-panel" style={{padding: '12px 16px', display: 'flex', flexDirection: 'column', gap: '8px'}}>
                            <span style={{fontSize: '11px', fontWeight: '700', color: '#484f58', textTransform: 'uppercase', letterSpacing: '0.8px'}}>
                                Effets actifs
                            </span>
                            <div style={{display: 'flex', gap: '8px', flexWrap: 'wrap'}}>
                                {effects.filter(e => e.cardEnglishName).map((effect, idx) => {
                                    const isSelected = selectedCard && selectedCard.englishName === effect.cardEnglishName && selectedCard.isEffect;
                                    return (
                                        <Card
                                            key={idx}
                                            name={effect.cardEnglishName}
                                            showCard={() => showCard({
                                                englishName: effect.cardEnglishName,
                                                name: effect.cardName,
                                                description: effect.cardDescription,
                                                param: {},
                                                isEffect: true,
                                            })}
                                            isSelected={isSelected}
                                            isPlayable={true}
                                        />
                                    );
                                })}
                            </div>
                        </div>
                    )}

                    {/* Captured pieces */}
                    {capturedPieces.length > 0 && (() => {
                        const pieceSymbols = {P: '♟', N: '♞', B: '♝', R: '♜', Q: '♛', K: '♚', Kangaroo: '🦘', Crab: '🦀'};
                        const canSelect = selectedCard && selectedParam && !isBarricadeCard(selectedCard);
                        const renderPiece = (code, globalIndex) => {
                            const type = code.slice(1);
                            const color = code[0];
                            const paramValue = "captured:" + globalIndex;
                            const isSelected = canSelect && selectedCard.param[selectedParam] === paramValue;
                            return (
                                <span
                                    key={globalIndex}
                                    onClick={canSelect ? () => onCapturedPieceClick(globalIndex) : undefined}
                                    style={{
                                        fontSize: '20px',
                                        color: color === 'w' ? '#f0d9b5' : '#6a5a4a',
                                        lineHeight: 1,
                                        cursor: canSelect ? 'pointer' : 'default',
                                        background: isSelected ? 'rgba(212,168,67,0.25)' : 'transparent',
                                        borderRadius: '4px',
                                        padding: '2px 3px',
                                        transition: 'background 0.15s',
                                    }}>
                                    {pieceSymbols[type] || '?'}
                                </span>
                            );
                        };
                        const whiteIndices = capturedPieces.map((p, i) => ({code: p, index: i})).filter(e => e.code.startsWith('w'));
                        const blackIndices = capturedPieces.map((p, i) => ({code: p, index: i})).filter(e => e.code.startsWith('b'));
                        return (
                            <div className="sotc-panel" style={{padding: '12px 16px', display: 'flex', flexDirection: 'column', gap: '8px'}}>
                                <span style={{fontSize: '11px', fontWeight: '700', color: '#484f58', textTransform: 'uppercase', letterSpacing: '0.8px'}}>
                                    {canSelect ? 'Cliquez sur une pièce capturée' : 'Pièces capturées'}
                                </span>
                                {whiteIndices.length > 0 && (
                                    <div style={{display: 'flex', flexWrap: 'wrap', gap: '2px'}}>
                                        {whiteIndices.map(e => renderPiece(e.code, e.index))}
                                    </div>
                                )}
                                {blackIndices.length > 0 && (
                                    <div style={{display: 'flex', flexWrap: 'wrap', gap: '2px'}}>
                                        {blackIndices.map(e => renderPiece(e.code, e.index))}
                                    </div>
                                )}
                            </div>
                        );
                    })()}
                </aside>
            </main>
        </div>
    );
}
