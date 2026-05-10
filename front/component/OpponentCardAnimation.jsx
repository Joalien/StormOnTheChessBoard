import {useEffect, useRef, useState} from 'react';
import {Image} from 'react-native';
import cardImages from './cardImages';

const GROW_MS = 450;
const HOLD_MS = 3000;
const TRANSIT_MS = 700;
const FADE_MS = 600;

/**
 * Renders a single in-flight card animation. Three phases:
 *   1. grow: the card flies from {fromX, fromY} to the centre of the board, scaling up.
 *   2. holdCenter: the card stays full-size for {HOLD_MS}ms so the player can read it.
 *   3. travel: either translates to the active-effects panel target while shrinking
 *      (when {effectTarget} is set), or fades out at the centre otherwise.
 *   4. on completion the parent removes the entry from its queue.
 */
export function OpponentCardAnimation({entry, onDone}) {
    const [phase, setPhase] = useState('init'); // init → grow → holdCenter → travel → done
    const removalScheduledRef = useRef(false);
    // Stash onDone in a ref so the timeout effect below depends only on entry.id.
    // The parent re-renders frequently (e.g. every second from the player clocks),
    // which would otherwise restart the timeline at 'init' mid-animation and freeze
    // the card on screen.
    const onDoneRef = useRef(onDone);
    onDoneRef.current = onDone;
    const hasEffect = !!entry.effectTarget;

    useEffect(() => {
        const t1 = setTimeout(() => setPhase('grow'), 20);
        const t2 = setTimeout(() => setPhase('holdCenter'), 20 + GROW_MS);
        const t3 = setTimeout(() => setPhase('travel'), 20 + GROW_MS + HOLD_MS);
        const finalDelay = hasEffect ? TRANSIT_MS : FADE_MS;
        const t4 = setTimeout(() => {
            if (!removalScheduledRef.current) {
                removalScheduledRef.current = true;
                onDoneRef.current(entry.id);
            }
        }, 20 + GROW_MS + HOLD_MS + finalDelay);
        return () => {
            clearTimeout(t1); clearTimeout(t2); clearTimeout(t3); clearTimeout(t4);
        };
    }, [entry.id, hasEffect]);

    if (!(entry.cardEnglishName in cardImages)) return null;

    const cardW = 240;
    const cardH = 347;

    const isInit = phase === 'init';
    const isGrow = phase === 'grow';
    const isHold = phase === 'holdCenter';
    const isTravel = phase === 'travel';

    let translateX = entry.fromX;
    let translateY = entry.fromY;
    let scale = 0.4;
    let opacity = 0.85;
    let transition = `none`;

    if (isInit) {
        scale = 0.4;
    } else if (isGrow) {
        translateX = entry.centerX - cardW / 2;
        translateY = entry.centerY - cardH / 2;
        scale = 1;
        opacity = 1;
        transition = `transform ${GROW_MS}ms cubic-bezier(0.16, 1, 0.3, 1), opacity ${GROW_MS}ms ease-out`;
    } else if (isHold) {
        translateX = entry.centerX - cardW / 2;
        translateY = entry.centerY - cardH / 2;
        scale = 1;
        opacity = 1;
        transition = `none`;
    } else if (isTravel) {
        if (entry.effectTarget) {
            // Land at the centre of the active-effects panel, scaled down to the size
            // of the small effect cards rendered there (~100x145, our base is 240x347
            // so a scale of ~0.42 matches).
            translateX = entry.effectTarget.x - cardW / 2;
            translateY = entry.effectTarget.y - cardH / 2;
            scale = 0.42;
            opacity = 0;
            transition = `transform ${TRANSIT_MS}ms cubic-bezier(0.4, 0.0, 0.2, 1), opacity ${TRANSIT_MS}ms ease-in 200ms`;
        } else {
            translateX = entry.centerX - cardW / 2;
            translateY = entry.centerY - cardH / 2;
            scale = 1.05;
            opacity = 0;
            transition = `opacity ${FADE_MS}ms ease-out, transform ${FADE_MS}ms ease-out`;
        }
    }

    return (
        <div style={{
            position: 'fixed',
            left: 0,
            top: 0,
            width: cardW,
            height: cardH,
            transform: `translate(${translateX}px, ${translateY}px) scale(${scale})`,
            transformOrigin: 'top left',
            transition,
            pointerEvents: 'none',
            zIndex: 10000,
            opacity,
            borderRadius: 12,
            boxShadow: '0 24px 64px rgba(0,0,0,0.85), 0 0 0 1px rgba(212,168,67,0.4)',
            overflow: 'hidden',
            lineHeight: 0,
        }}>
            <Image source={cardImages[entry.cardEnglishName]} style={{width: cardW, height: cardH}}/>
        </div>
    );
}
