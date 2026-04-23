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
import {VictoryPopup} from "./component/VictoryPopup";
import {toast, ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {useCardSelection} from "./hooks/useCardSelection";
import {clusterSpotlights} from "./hooks/clusterPositions";
import {createFusedPieceComponent} from "./component/pieces/FusedPiece";
import {getRouteFromUrl, getPlayerColorFromUrl, squareToCoords as squareToCoordsUtil, oppositeColor} from "./utils/boardUtils";
import {useGameActions} from "./hooks/useGameActions";
import {resolveKeyAction} from "./utils/keyboardActions";
import {parsePresenceMessage, parseMatchmakingMessage} from "./utils/wsHandlers";
import {endTurnButtonClassName} from "./utils/endTurnButton";
import {DEV_TOOLS_ENABLED} from "./config/devConfig";
import {DevCardSearch} from "./component/dev/DevCardSearch";

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

  @keyframes sotc-end-turn-pulse {
    0%, 100% {
      box-shadow: 0 0 0 0 rgba(63,185,80,0);
      background: rgba(63,185,80,0.05);
    }
    50% {
      box-shadow: 0 0 16px 2px rgba(63,185,80,0.55);
      background: rgba(63,185,80,0.18);
    }
  }
  .sotc-btn-end.sotc-btn-end--ready {
    animation: sotc-end-turn-pulse 1.4s ease-in-out infinite;
    border-color: rgba(63,185,80,0.55);
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

  @keyframes sotc-check-blink {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.5; }
  }

  .sotc-check-warning {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 14px;
    border-radius: 8px;
    background: rgba(248,81,73,0.12);
    border: 1px solid rgba(248,81,73,0.4);
    color: #f85149;
    font-size: 13px;
    font-weight: 600;
    animation: sotc-check-blink 1.2s ease-in-out infinite;
  }

  .sotc-check-warning span.sotc-check-icon {
    font-size: 18px;
  }

  .sotc-btn-shuffle {
    border-color: rgba(56,189,248,0.3);
    color: #38bdf8;
  }
  .sotc-btn-shuffle:hover {
    background: rgba(56,189,248,0.1);
    border-color: rgba(56,189,248,0.5);
    box-shadow: 0 4px 12px rgba(56,189,248,0.2);
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
  .type-ENEMY_TURN_AFTER_MOVE  { background: rgba(248,81,73,0.12);  color: #f85149; border: 1px solid rgba(248,81,73,0.28); }
  .type-ENEMY_TURN_AFTER_CARD  { background: rgba(248,81,73,0.12);  color: #f85149; border: 1px solid rgba(248,81,73,0.28); }

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
    const text = await res.text();
    const isPlainText = text && !text.trimStart().startsWith('{') && !text.trimStart().startsWith('[');
    toast.error(isPlainText ? text : "Une erreur est survenue.");
}


export default function App() {
    const [game, setGame] = useState({});
    const [route, setRoute] = useState(getRouteFromUrl);
    const gameId = route.page === 'game' ? route.gameId : null;
    const [currentPlayerColor, setCurrentPlayerColor] = useState("white");
    const [whitePlayer, setWhitePlayer] = useState({cards: []});
    const [blackPlayer, setBlackPlayer] = useState({cards: []});
    const [effects, setEffects] = useState([]);
    const [pendingPromotions, setPendingPromotions] = useState([]);
    const [promotionSquare, setPromotionSquare] = useState(null);
    const [checkMateTargets, setCheckMateTargets] = useState([]);
    const [currentState, setCurrentState] = useState(null);
    const [capturedPieces, setCapturedPieces] = useState([]);
    const [isInCheck, setIsInCheck] = useState(false);
    const [gameResult, setGameResult] = useState('ONGOING');
    const [victoryDismissed, setVictoryDismissed] = useState(false);
    const [myColor, setMyColor] = useState(getPlayerColorFromUrl);
    const [legalMoves, setLegalMoves] = useState([]);

    const {
        selectedCard, setSelectedCard,
        selectedParam, setSelectedParam,
        barricadeEdges, setBarricadeEdges,
        selectedEffectPositions,
        showCard: showCardInner, clearSelection: clearCardSelection,
        selectEffectByIndices,
        onBoardSquareClick,
        onSquareRightClick,
        onBarricadeEdgeClick,
        onCapturedPieceClick,
        onEnumSelect,
        playableCardTypes,
        isSelectedCardPlayable,
        isBarricadeCard,
        firstUnsetParam,
    } = useCardSelection(effects, currentState, myColor, currentPlayerColor);
    const [selectedPiece, setSelectedPiece] = useState(null);

    function clearSelection() {
        clearCardSelection();
        setSelectedPiece(null);
        setLegalMoves([]);
    }

    function showCard(card) {
        showCardInner(card);
        setSelectedPiece(null);
        setLegalMoves([]);
    }
    const [windowWidth, setWindowWidth] = useState(typeof window !== 'undefined' ? window.innerWidth : 900);
    const [gameIds, setGameIds] = useState([]);
    const [expandedGameId, setExpandedGameId] = useState(null);
    const [matchmakingStats, setMatchmakingStats] = useState(null);
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
    const staticPieces = loadCustomPieces();

    const customPieces = useMemo(() => {
        const pieces = { ...staticPieces };
        Object.values(game).forEach(code => {
            if (code.includes('Fused') && !pieces[code]) {
                const component = createFusedPieceComponent(code);
                if (component) pieces[code] = component;
            }
        });
        return pieces;
    }, [game]);

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

    // Fetch initial data
    useEffect(() => {
        fetch(backendOrigin + '/api/chessboard')
            .then(res => res.json())
            .then(ids => setGameIds(ids.sort((a, b) => a - b)))
            .catch(() => {});
        fetch(backendOrigin + '/api/matchmaking/stats')
            .then(res => res.json())
            .then(stats => setMatchmakingStats(prev => ({...prev, ...stats})))
            .catch(() => {});
    }, []);

    // Presence WebSocket — real-time connected count + matchmaking stats
    useEffect(() => {
        const ws = new WebSocket(wsOrigin + '/ws/presence');
        ws.onmessage = (event) => {
            const data = parsePresenceMessage(event.data);
            if (data) setMatchmakingStats(prev => ({...prev, ...data}));
        };
        return () => ws.close();
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

    function navigateToGame(id, color = null, strategy = null) {
        setGameResult('ONGOING');
        setVictoryDismissed(false);
        if (id === null) {
            window.history.pushState({}, '', '/');
            setRoute({page: 'home'});
            setMyColor(null);
        } else {
            const params = new URLSearchParams();
            if (color) params.set('color', color);
            if (strategy) params.set('strategy', strategy);
            const qs = params.toString();
            const url = qs ? `/chessboard/${id}?${qs}` : `/chessboard/${id}`;
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
            const action = resolveKeyAction(e, {gameId, myColor, currentPlayerColor, selectedCard});
            if (!action) return;
            e.preventDefault();
            if (action === 'playCard') playCard();
            else if (action === 'endTurn') endTurn();
            else if (action === 'undo') undo();
            else if (action === 'shuffle') shuffle();
        }
        window.addEventListener('keydown', onKeyDown, true);
        return () => window.removeEventListener('keydown', onKeyDown, true);
    }, [gameId, myColor, currentPlayerColor, selectedCard]);

    function startNewGame() {
        fetch(backendOrigin + '/api/chessboard', {method: 'POST'})
            .then(res => res.text())
            .then(text => navigateToGame(parseInt(text, 10)))
            .catch(err => toast.error("Impossible de créer la partie : " + (err?.message ?? String(err))));
    }

    function playAgainstAi(strategy) {
        const query = strategy ? '?strategy=' + encodeURIComponent(strategy) : '';
        fetch(backendOrigin + '/api/chessboard/ai' + query, {method: 'POST'})
            .then(res => res.json())
            .then(data => navigateToGame(data.gameId, data.color, strategy))
            .catch(err => toast.error("Impossible de démarrer la partie contre l'IA : " + (err?.message ?? String(err))));
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
                ws.onmessage = (event) => {
                    const match = parseMatchmakingMessage(event.data);
                    if (match) {
                        ws.close();
                        matchmakingTokenRef.current = null;
                        notifyMatchFound();
                        navigateToGame(match.gameId, match.color);
                    }
                };
                // Check status once WS is open in case match happened during connection
                ws.onopen = () => {
                    fetch(backendOrigin + '/api/matchmaking/status/' + data.token)
                        .then(res => res.json())
                        .then(status => {
                            if (status.status === 'matched') {
                                ws.close();
                                matchmakingTokenRef.current = null;
                                notifyMatchFound();
                                navigateToGame(status.gameId, status.color);
                            }
                        })
                        .catch(() => {});
                };
            })
            .catch(err => {
                toast.error("Erreur: " + err);
                navigateToGame(null);
            });
    }

    function cancelMatchmaking() {
        if (matchmakingTokenRef.current) {
            fetch(backendOrigin + '/api/matchmaking/' + matchmakingTokenRef.current, {method: 'DELETE'}).catch(() => {});
            matchmakingTokenRef.current = null;
        }
        navigateToGame(null);
    }

    function fetchGame() {
        return fetch(base + gameId)
            .then(response => {
                if (!response.ok) {
                    setRoute({page: '404'});
                    window.history.replaceState({}, '', window.location.pathname);
                    return null;
                }
                return response.json();
            })
            .then(data => {
                if (!data) return null;
                setGame(data.pieces);
                setCurrentPlayerColor(data.currentTurn);
                setEffects(data.effects || []);
                setBlackPlayer(data.blackPlayer);
                setWhitePlayer(data.whitePlayer);
                setPendingPromotions(data.pendingPromotions || []);
                setCheckMateTargets(data.checkMateTargets || []);
                setCurrentState(data.currentState);
                setCapturedPieces(data.capturedPieces || []);
                setIsInCheck(data.isInCheck || false);
                setGameResult(data.gameResult || 'ONGOING');
                setPromotionSquare(null);
                return data;
            });
    }

    const {movePiece, playCard, endTurn, undo, promote, shuffle, selectPiece, onPieceDragBegin, onPieceDragEnd} = useGameActions({
        base, gameId, fetchGame, setSelectedCard, setSelectedParam,
        setCurrentPlayerColor, currentPlayerColor, selectedCard, setLegalMoves, showErrorMessage,
        myColor,
    });

    function isOwnPiece(square) {
        return isMyTurn && !!game[square] && game[square][0] === currentPlayerColor[0];
    }

    function onSquareClick(square) {
        // 1. Promotion
        if (pendingPromotions.includes(square)) {
            setPromotionSquare(promotionSquare === square ? null : square);
            return;
        }

        // 2. Card param assignment (clic gauche remplace l'ancien clic droit)
        if (onBoardSquareClick(square)) return;

        // 3. Déplacement par clic (pièce déjà sélectionnée)
        if (selectedPiece) {
            if (legalMoves.includes(square)) {
                movePiece(selectedPiece, square);
                setSelectedPiece(null);
                setLegalMoves([]);
            } else if (isOwnPiece(square)) {
                setSelectedPiece(square);
                selectPiece(square);
            } else {
                setSelectedPiece(null);
                setLegalMoves([]);
            }
            return;
        }

        // 4. Sélection de pièce
        if (isOwnPiece(square)) {
            setSelectedPiece(square);
            selectPiece(square);
            return;
        }

        // 5. Affichage d'un effet actif
        const matchingIndices = effects.reduce((acc, e, i) => {
            if (e.cardEnglishName && e.positions && e.positions.includes(square)) acc.push(i);
            return acc;
        }, []);
        if (matchingIndices.length > 0) {
            selectEffectByIndices(matchingIndices);
            return;
        }

        // 6. Désélection
        clearSelection();
    }

    function squareToCoords(square, orientation) {
        return squareToCoordsUtil(square, orientation, boardSize);
    }


    const isWhiteTurn = currentPlayerColor === "white";
    const isMyTurn = !myColor || myColor === currentPlayerColor;

    const victoryOutcome = (() => {
        if (gameResult === 'ONGOING' || victoryDismissed) return null;
        if (gameResult === 'DRAW') return 'draw';
        const winner = gameResult === 'WHITE_WINS' ? 'white' : 'black';
        if (!myColor) return 'win';
        return myColor === winner ? 'win' : 'lose';
    })();

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
    const selectedPieceHighlight = {boxShadow: "rgba(212, 168, 67, 0.95) 0px 0px 24px 4px inset"};
    const customSquareStyles = {
        ...customSquares(),
        ...(selectedPiece ? {[selectedPiece]: selectedPieceHighlight} : {}),
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
                <HomeScreen onPlaySolo={startNewGame} onMatchmaking={matchmaking} onPlayAi={playAgainstAi} stats={matchmakingStats} />
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

            {victoryOutcome && (
                <VictoryPopup
                    outcome={victoryOutcome}
                    onClose={() => setVictoryDismissed(true)}
                    onGoHome={() => navigateToGame(null)}
                />
            )}

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
                <div onClick={clearSelection} style={{
                    position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
                    background: 'rgba(0,0,0,0.7)',
                    zIndex: 50,
                }}/>
            )}

            {/* ── Main ── */}
            <main className="sotc-main" onClick={clearSelection} style={{
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
                    <div style={{position: 'relative', zIndex: selectedCard && selectedCard.isEffect ? 51 : undefined}} onClick={e => e.stopPropagation()}>
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
                                    const line = barricadeLines({positions: edge}, boardSize, currentPlayerColor)[0];
                                    return line && <line key={i} x1={line.x1} y1={line.y1} x2={line.x2} y2={line.y2} stroke="#3fb950" strokeWidth={6} strokeLinecap="round" />;
                                })}
                            </svg>
                        )}
                        {/* Barricade effect display (existing barricades on the board) */}
                        {effects.map((effect, globalIdx) => {
                            if (effect.name !== 'BarricadeEffect' || !effect.positions || effect.positions.length < 3) return null;
                            const clickable = !!effect.cardEnglishName;
                            return (
                                <svg key={`barricade-${globalIdx}`} style={{position: 'absolute', top: 0, left: 0, width: boardSize, height: boardSize, pointerEvents: 'none', zIndex: 5}}>
                                    {barricadeLines(effect, boardSize, currentPlayerColor).map((line, i) => (
                                        <g key={i} onContextMenu={clickable ? (e) => { e.preventDefault(); e.stopPropagation(); selectEffectByIndices([globalIdx]); } : undefined} style={{pointerEvents: clickable ? 'auto' : 'none'}}>
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
                                elements.push(
                                    <div key={`mag-center-${idx}-${pi}`} style={{
                                        position: 'absolute',
                                        left: cx, top: cy,
                                        width: sqSize, height: sqSize,
                                        pointerEvents: 'none',
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
                            return effect.positions.map((pos, pi) => {
                                const {x, y} = squareToCoords(pos, currentPlayerColor);
                                const style = effectConfig.applyStyle(pos, !!game[pos]);
                                return (
                                    <div key={`eff-${idx}-${pi}`} style={{
                                        position: 'absolute',
                                        left: x, top: y,
                                        width: sqSize, height: sqSize,
                                        pointerEvents: 'none',
                                        zIndex: 1,
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
                            const points = [...new Set(selectedEffectPositions)].map(sq => {
                                const {x, y} = squareToCoords(sq, orientation);
                                return {cx: x + sqSize / 2, cy: y + sqSize / 2};
                            });
                            const spots = clusterSpotlights(points, sqSize * 5, sqSize * 1.8);
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
                                            {spots.map((s, i) => (
                                                <circle key={i} cx={s.cx} cy={s.cy} r={s.r} fill="url(#spotGrad)"/>
                                            ))}
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
                    {/* Check warning */}
                    {isInCheck && isMyTurn && (
                        <div className="sotc-check-warning" title="Votre roi est en échec ! Jouez une carte pour parer l'échec ou annulez votre coup.">
                            <span className="sotc-check-icon">/!\</span>
                            <span>Votre roi est en échec !</span>
                        </div>
                    )}
                    {/* Action buttons */}
                    <div style={{display: 'flex', gap: '8px'}}>
                        <button className={endTurnButtonClassName(currentState, isMyTurn)} style={{flex: 1, padding: '10px 13px 8px', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '2px'}} onClick={endTurn} disabled={!isMyTurn}>
                            <span>✓ Fin du tour</span>
                            <span style={{fontSize: '9px', opacity: 0.5, fontWeight: 400}}>Espace</span>
                        </button>
                        <button className="sotc-btn sotc-btn-danger" style={{flex: 1, padding: '10px 13px 8px', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '2px'}} onClick={undo} disabled={!isMyTurn}>
                            <span>↩ Annuler</span>
                            <span style={{fontSize: '9px', opacity: 0.5, fontWeight: 400}}>Ctrl+Z</span>
                        </button>
                        <button className="sotc-btn sotc-btn-shuffle" style={{flex: 1, padding: '10px 13px 8px', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '2px'}} onClick={shuffle} disabled={!isMyTurn}>
                            <span>⟳ Mélanger</span>
                            <span style={{fontSize: '9px', opacity: 0.5, fontWeight: 400}}>Shift+S</span>
                        </button>
                    </div>

                    {/* Card details */}
                    {selectedCard && selectedCard.isEffect && selectedCard.effectIndices && selectedCard.effectIndices.length > 1 ? (
                        <div className="sotc-panel" style={{padding: '20px', display: 'flex', flexDirection: 'column', gap: '16px'}}>
                            <div style={{display: 'flex', gap: '10px', justifyContent: 'center'}}>
                                {selectedCard.effectIndices.map(i => {
                                    const eff = effects[i];
                                    if (!eff) return null;
                                    const hasImage = eff.cardEnglishName in (require('./component/cardImages').default);
                                    return hasImage ? (
                                        <div key={i} style={{borderRadius: '10px', overflow: 'hidden', boxShadow: '0 8px 28px rgba(0,0,0,0.55)', lineHeight: 0}}>
                                            <Image source={require('./component/cardImages').default[eff.cardEnglishName]} style={{width: 135, height: 195}}/>
                                        </div>
                                    ) : null;
                                })}
                            </div>
                            {selectedCard.effectIndices.map(i => {
                                const eff = effects[i];
                                if (!eff) return null;
                                return (
                                    <div key={i} style={{textAlign: 'center', display: 'flex', flexDirection: 'column', gap: '6px', alignItems: 'center'}}>
                                        <h3 style={{fontSize: '14px', fontWeight: '700', color: '#e6edf3', margin: 0}}>{eff.cardName}</h3>
                                        {eff.cardDescription && (
                                            <p style={{fontSize: '11px', color: '#8b949e', lineHeight: '1.6', margin: 0, padding: '8px 10px', background: 'rgba(255,255,255,0.025)', borderRadius: '8px', border: '1px solid rgba(255,255,255,0.05)'}}>
                                                {eff.cardDescription}
                                            </p>
                                        )}
                                    </div>
                                );
                            })}
                        </div>
                    ) : selectedCard ? (
                        <CardParameters
                            card={selectedCard}
                            selectedParam={selectedParam}
                            setSelectedParam={setSelectedParam}
                            playCardCallback={playCard}
                            barricadeEdges={barricadeEdges}
                            setBarricadeEdges={setBarricadeEdges}
                            isPlayable={!selectedCard.isEffect && playableCardTypes(currentState, false).includes(selectedCard.type)}
                            onEnumSelect={onEnumSelect}
                        />
                    ) : (
                        <div className="sotc-panel" style={{padding: '28px 20px', textAlign: 'center', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '12px'}}>
                            <Image source={require('./assets/images/cards/back.png')} style={{width: 80, height: 116, borderRadius: 6, opacity: 0.25}}/>
                            <p style={{color: '#484f58', fontSize: '13px', lineHeight: '1.7', margin: 0}}>
                                Cliquez sur une carte ou faites un clic droit sur une pièce pour voir ses effets.
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
                                {effects.map((effect, idx) => {
                                    if (!effect.cardEnglishName) return null;
                                    const isSelected = selectedCard && selectedCard.isEffect && selectedCard.effectIndices && selectedCard.effectIndices.includes(idx);
                                    return (
                                        <Card
                                            key={idx}
                                            name={effect.cardEnglishName}
                                            showCard={() => selectEffectByIndices([idx])}
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
                    {DEV_TOOLS_ENABLED && gameId && (
                        <DevCardSearch gameId={gameId} onReplaced={fetchGame}/>
                    )}
                </aside>
            </main>
        </div>
    );
}
