import {useEffect, useRef, useState} from 'react';

let nextId = 1;

/**
 * Watches {history} for new card-play entries and emits animation entries describing
 * the card to fly from the opponent's hand to the board centre, hold for several
 * seconds for readability, then either travel to the active-effects panel (if the card
 * created a persistent effect) or fade out (if it was a one-shot card).
 *
 * The DOM measurements (player hand origin, board centre, active-effects panel position)
 * are captured at detection time using the supplied refs.
 *
 * Returns: { animations: [...], dismiss: (id) => void }.
 */
export function useOpponentCardAnimations({history, effects, myColor, currentPlayerColor, topPlayerRef, bottomPlayerRef, boardRef, activeEffectsPanelRef}) {
    const [animations, setAnimations] = useState([]);
    const lastSeenIndexRef = useRef(-1);

    useEffect(() => {
        if (!history || history.length === 0) {
            lastSeenIndexRef.current = -1;
            return;
        }
        // First fetch: just sync the cursor without animating retroactive entries.
        if (lastSeenIndexRef.current === -1) {
            lastSeenIndexRef.current = history[history.length - 1].index;
            return;
        }

        const fresh = history.filter(e => e.index > lastSeenIndexRef.current);
        if (fresh.length === 0) return;
        lastSeenIndexRef.current = history[history.length - 1].index;

        const opponentColor = myColor ? oppositeOf(myColor) : oppositeOf(currentPlayerColor);
        const newAnimations = [];

        for (const entry of fresh) {
            if (!entry.cardEnglishName) continue; // not a card play
            if (entry.color !== opponentColor) continue; // we already see our own card play in real time

            const handRef = isHandFromTop(myColor, currentPlayerColor, entry.color) ? topPlayerRef : bottomPlayerRef;
            const fromRect = handRef.current?.getBoundingClientRect();
            const boardRect = boardRef.current?.getBoundingClientRect();
            if (!fromRect || !boardRect) continue;

            const effectTarget = locateEffectTarget(entry, effects, activeEffectsPanelRef);

            newAnimations.push({
                id: nextId++,
                cardEnglishName: entry.cardEnglishName,
                fromX: fromRect.left + fromRect.width / 2,
                fromY: fromRect.top + fromRect.height / 2,
                centerX: boardRect.left + boardRect.width / 2,
                centerY: boardRect.top + boardRect.height / 2,
                effectTarget,
            });
        }
        if (newAnimations.length > 0) {
            setAnimations(prev => [...prev, ...newAnimations]);
        }
    }, [history, effects, myColor, currentPlayerColor, topPlayerRef, bottomPlayerRef, boardRef, activeEffectsPanelRef]);

    function dismiss(id) {
        setAnimations(prev => prev.filter(a => a.id !== id));
    }

    return {animations, dismiss};
}

function oppositeOf(color) {
    return color === 'white' ? 'black' : 'white';
}

/**
 * In matchmaking (myColor set) the opponent always sits at the top.
 * In solo mode the layout follows currentPlayerColor: bottom = current player, so
 * the opponent's hand is at the top relative to the board layout.
 */
function isHandFromTop(myColor, currentPlayerColor, entryColor) {
    if (myColor) return entryColor !== myColor;
    return entryColor !== currentPlayerColor;
}

/**
 * If the freshly-played card created a new persistent effect, returns DOM coordinates
 * of the active-effects panel — that's where the small effect card now lives.
 * Otherwise null (the card will fade out instead of travelling).
 *
 * The panel ref may be null when no effects were active before the new one (the panel
 * mounts conditionally). In that case we still return null and fall back to fade out;
 * the next render will mount the panel for future animations.
 */
function locateEffectTarget(entry, effects, activeEffectsPanelRef) {
    const matching = effects.find(e => e.cardEnglishName === entry.cardEnglishName);
    if (!matching) return null;
    const rect = activeEffectsPanelRef?.current?.getBoundingClientRect();
    if (!rect) return null;
    return {
        x: rect.left + rect.width / 2,
        y: rect.top + rect.height / 2,
    };
}
