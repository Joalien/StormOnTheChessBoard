import {useState, useMemo, useCallback} from 'react';

function isBarricadeCard(card) {
    return card && card.englishName === 'BarricadeCard';
}

function isListParam(card, key) {
    return card && card.listParams && card.listParams.includes(key);
}

function firstUnsetParam(card) {
    if (!card || !card.param) return null;
    if (isBarricadeCard(card)) {
        if (card.param.from1 === null) return 'from1';
        if (card.param.to1 === null) return 'to1';
        if (card.param.from2 === null) return 'from2';
        if (card.param.to2 === null) return 'to2';
        return null;
    }
    // Scalar params that are still null
    const unsetScalar = Object.keys(card.param).find(k => !isListParam(card, k) && card.param[k] === null);
    if (unsetScalar) return unsetScalar;
    // If all scalars are set, select the first list param (always accepts more clicks)
    return Object.keys(card.param).find(k => isListParam(card, k)) || null;
}

export function useCardSelection(effects, currentState, myColor, currentPlayerColor) {
    const [selectedCard, setSelectedCard] = useState(null);
    const [selectedParam, setSelectedParam] = useState(null);
    const [barricadeEdges, setBarricadeEdges] = useState([]);

    function playableCardTypes(state, isOpponent) {
        if (isOpponent) return ['ENEMY_TURN_AFTER_MOVE', 'ENEMY_TURN_AFTER_CARD'];
        if (myColor && myColor !== currentPlayerColor) return [];
        const map = {
            'BEGINNING_OF_THE_TURN': ['BEFORE_TURN', 'REPLACE_TURN'],
            'BEFORE_MOVE': [],
            'MOVE_WITHOUT_CARD_PLAYED': ['AFTER_TURN'],
            'END_OF_THE_TURN': [],
            'PROMOTION_PENDING': [],
        };
        return map[state] || [];
    }

    function isSelectedCardPlayable() {
        return selectedCard && playableCardTypes(currentState, false).includes(selectedCard.type);
    }

    function showCard(card) {
        if (selectedCard && !selectedCard.isEffect && selectedCard.englishName === card.englishName) return;
        setSelectedCard(card);
        setSelectedParam(firstUnsetParam(card));
        setBarricadeEdges([]);
    }

    function clearSelection() {
        if (selectedCard) {
            setSelectedCard(null);
            setSelectedParam(null);
            setBarricadeEdges([]);
        }
    }

    function selectEffectByIndices(indices) {
        if (!indices.length) return;
        const alreadySelected = selectedCard && selectedCard.isEffect &&
            selectedCard.effectIndices && selectedCard.effectIndices.length === indices.length &&
            selectedCard.effectIndices.every((v, i) => v === indices[i]);
        if (alreadySelected) {
            setSelectedCard(null);
            setSelectedParam(null);
        } else {
            const effect = effects[indices[0]];
            setSelectedCard({
                englishName: effect.cardEnglishName,
                name: effect.cardName,
                description: effect.cardDescription,
                param: {},
                isEffect: true,
                effectIndices: indices,
            });
            setSelectedParam(null);
            setBarricadeEdges([]);
        }
    }

    function onEnumSelect(key, value) {
        if (!selectedCard) return;
        const newParam = {...selectedCard.param, [key]: value};
        const updated = {...selectedCard, param: newParam};
        setSelectedCard(updated);
        setSelectedParam(firstUnsetParam(updated));
    }

    function onSquareRightClick(square) {
        if (selectedCard && selectedParam && isSelectedCardPlayable() && !isBarricadeCard(selectedCard)
            && !(selectedCard.enumOptions && selectedCard.enumOptions[selectedParam])) {
            const newParam = {...selectedCard.param};
            if (isListParam(selectedCard, selectedParam)) {
                const list = [...(newParam[selectedParam] || [])];
                const idx = list.indexOf(square);
                if (idx >= 0) list.splice(idx, 1);
                else list.push(square);
                newParam[selectedParam] = list;
            } else {
                if (newParam[selectedParam] === square) newParam[selectedParam] = null;
                else newParam[selectedParam] = square;
            }
            const updated = {...selectedCard, param: newParam};
            setSelectedCard(updated);
            setSelectedParam(firstUnsetParam(updated));
            return;
        }
        const matchingIndices = effects.reduce((acc, e, i) => {
            if (e.cardEnglishName && e.positions && e.positions.includes(square)) acc.push(i);
            return acc;
        }, []);
        if (matchingIndices.length > 0) {
            selectEffectByIndices(matchingIndices);
        }
    }

    function onBarricadeEdgeClick(edge) {
        if (barricadeEdges.length >= 2) return;
        const newEdges = [...barricadeEdges, edge];
        setBarricadeEdges(newEdges);
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

    const selectedEffectPositions = selectedCard && selectedCard.isEffect && selectedCard.effectIndices
        ? selectedCard.effectIndices.flatMap(i => {
            const e = effects[i];
            if (!e || !e.positions) return [];
            return [...e.positions];
        })
        : [];

    return {
        selectedCard, setSelectedCard,
        selectedParam, setSelectedParam,
        barricadeEdges, setBarricadeEdges,
        selectedEffectPositions,
        showCard,
        clearSelection,
        selectEffectByIndices,
        onSquareRightClick,
        onBarricadeEdgeClick,
        onCapturedPieceClick,
        onEnumSelect,
        playableCardTypes,
        isSelectedCardPlayable,
        isBarricadeCard,
        firstUnsetParam,
    };
}
