import {useCallback} from 'react';
import {oppositeColor} from '../utils/boardUtils';

export function useGameActions({base, gameId, fetchGame, setSelectedCard, setSelectedParam, setCurrentPlayerColor, currentPlayerColor, selectedCard, setLegalMoves, showErrorMessage}) {

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

    async function promote(position, piece) {
        const res = await fetch(base + gameId + "/promote/" + position + "/" + piece, {method: 'POST'});
        if (res.ok) fetchGame();
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

    function onPieceDragBegin(piece, sourceSquare) {
        fetch(base + gameId + "/legalMoves/" + sourceSquare)
            .then(res => res.json())
            .then(moves => setLegalMoves(moves))
            .catch(() => setLegalMoves([]));
    }

    function onPieceDragEnd() {
        setLegalMoves([]);
    }

    return {movePiece, playCard, endTurn, undo, promote, shuffle, onPieceDragBegin, onPieceDragEnd};
}
