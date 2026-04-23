import {useCallback} from 'react';
import {oppositeColor} from '../utils/boardUtils';

export function useGameActions({base, gameId, fetchGame, setSelectedCard, setSelectedParam, setCurrentPlayerColor, currentPlayerColor, selectedCard, setLegalMoves, showErrorMessage, myColor}) {

    async function endTurn() {
        const res = await fetch(base + gameId + "/endTurn", {method: 'POST'});
        if (res.ok) {
            setCurrentPlayerColor(oppositeColor(currentPlayerColor));
            setSelectedCard(null);
            await fetchGame();
        } else await showErrorMessage(res);
    }

    async function refetchAndMaybeAutoEndTurn() {
        const data = await fetchGame();
        if (!data) return;
        if (data.currentState !== 'END_OF_THE_TURN') return;
        if (data.gameResult && data.gameResult !== 'ONGOING') return;
        if (myColor && data.currentTurn !== myColor) return;
        await endTurn();
    }

    async function movePiece(sourceSquare, targetSquare) {
        setLegalMoves([]);
        const res = await fetch(base + gameId + "/move/" + sourceSquare + "/to/" + targetSquare, {method: 'POST'});
        if (res.ok) await refetchAndMaybeAutoEndTurn();
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
            await refetchAndMaybeAutoEndTurn();
        } else await showErrorMessage(res);
    }

    async function undo() {
        const res = await fetch(base + gameId + "/undo", {method: 'POST'});
        if (res.ok) {
            setSelectedCard(null);
            setSelectedParam(null);
            await fetchGame();
        } else await showErrorMessage(res);
    }

    async function promote(position, piece) {
        const res = await fetch(base + gameId + "/promote/" + position + "/" + piece, {method: 'POST'});
        if (res.ok) await refetchAndMaybeAutoEndTurn();
        else await showErrorMessage(res);
    }

    async function shuffle() {
        const res = await fetch(base + gameId + "/shuffle", {method: 'POST'});
        if (res.ok) {
            setSelectedCard(null);
            setSelectedParam(null);
            fetchGame();
        } else await showErrorMessage(res);
    }

    function selectPiece(square) {
        fetch(base + gameId + "/legalMoves/" + square)
            .then(res => res.json())
            .then(moves => setLegalMoves(moves))
            .catch(() => setLegalMoves([]));
    }

    function onPieceDragBegin(piece, sourceSquare) {
        selectPiece(sourceSquare);
    }

    function onPieceDragEnd() {
        setLegalMoves([]);
    }

    return {movePiece, playCard, endTurn, undo, promote, shuffle, selectPiece, onPieceDragBegin, onPieceDragEnd};
}
