import {renderHook, act} from '@testing-library/react';
import {useCardSelection} from '../hooks/useCardSelection';

describe('useCardSelection', () => {
    const noEffects = [];
    const state = 'MOVE_WITHOUT_CARD_PLAYED'; // AFTER_TURN cards are playable
    const myColor = null;
    const currentPlayerColor = 'white';

    function setup(effects = noEffects, overrides = {}) {
        return renderHook(() => useCardSelection(
            effects,
            overrides.state ?? state,
            overrides.myColor ?? myColor,
            overrides.currentPlayerColor ?? currentPlayerColor,
        ));
    }

    // ── Card selection ──

    test('selecting a card sets selectedCard and selectedParam', () => {
        const {result} = setup();
        const card = {englishName: 'BombingCard', name: 'Attentat', param: {position: null}, type: 'AFTER_TURN'};

        act(() => result.current.showCard(card));

        expect(result.current.selectedCard).toEqual(card);
        expect(result.current.selectedParam).toBe('position');
    });

    test('selecting the same card again does nothing (same reference)', () => {
        const {result} = setup();
        const card = {englishName: 'BombingCard', name: 'Attentat', param: {position: null}, type: 'AFTER_TURN'};

        act(() => result.current.showCard(card));
        act(() => result.current.showCard(card));

        expect(result.current.selectedCard).toEqual(card);
        expect(result.current.selectedParam).toBe('position');
    });

    test('selecting the same card again does nothing (different reference, same englishName)', () => {
        const {result} = setup();
        const card1 = {englishName: 'BombingCard', name: 'Attentat', param: {position: null}, type: 'AFTER_TURN'};
        const card2 = {englishName: 'BombingCard', name: 'Attentat', param: {position: null}, type: 'AFTER_TURN'};

        act(() => result.current.showCard(card1));
        act(() => result.current.showCard(card2));

        expect(result.current.selectedCard).toEqual(card1);
    });

    test('selecting a card when an effect is selected replaces the selection', () => {
        const effects = [
            {cardEnglishName: 'MagnetismCard', cardName: 'Magnétisme', cardDescription: 'd', positions: ['f6']},
        ];
        const {result} = setup(effects);

        // Select an effect first
        act(() => result.current.onSquareRightClick('f6'));
        expect(result.current.selectedCard.isEffect).toBe(true);

        // Click on a hand card
        const card = {englishName: 'BombingCard', param: {position: null}, type: 'AFTER_TURN'};
        act(() => result.current.showCard(card));

        expect(result.current.selectedCard).toEqual(card);
        expect(result.current.selectedCard.isEffect).toBeUndefined();
    });

    test('selecting a card when another card is selected replaces the selection', () => {
        const {result} = setup();
        const card1 = {englishName: 'Card1', param: {a: null}};
        const card2 = {englishName: 'Card2', param: {b: null}};

        act(() => result.current.showCard(card1));
        act(() => result.current.showCard(card2));

        expect(result.current.selectedCard).toEqual(card2);
        expect(result.current.selectedParam).toBe('b');
    });

    test('clearSelection resets everything', () => {
        const {result} = setup();
        act(() => result.current.showCard({englishName: 'Card1', param: {a: null}}));
        act(() => result.current.clearSelection());

        expect(result.current.selectedCard).toBeNull();
        expect(result.current.selectedParam).toBeNull();
        expect(result.current.barricadeEdges).toEqual([]);
    });

    test('clearSelection does nothing when nothing is selected', () => {
        const {result} = setup();
        act(() => result.current.clearSelection());
        expect(result.current.selectedCard).toBeNull();
    });

    // ── Card param assignment via right-click ──

    test('right-click on square assigns param to selected card', () => {
        const {result} = setup();
        const card = {englishName: 'BombingCard', param: {position: null}, type: 'AFTER_TURN'};

        act(() => result.current.showCard(card));
        act(() => result.current.onSquareRightClick('e4'));

        expect(result.current.selectedCard.param.position).toBe('e4');
        expect(result.current.selectedParam).toBeNull(); // all params set
    });

    test('right-click different square replaces the param', () => {
        const {result} = setup();
        const card = {englishName: 'BombingCard', param: {position: null, extra: null}, type: 'AFTER_TURN'};

        act(() => result.current.showCard(card));
        act(() => result.current.onSquareRightClick('e4'));

        expect(result.current.selectedCard.param.position).toBe('e4');
        expect(result.current.selectedParam).toBe('extra');

        // Right-click on same square while param is 'extra' assigns extra
        act(() => result.current.onSquareRightClick('d5'));
        expect(result.current.selectedCard.param.extra).toBe('d5');
    });

    test('multi-param card advances to next unset param', () => {
        const {result} = setup();
        const card = {englishName: 'InfiltrationCard', param: {piece1: null, piece2: null}, type: 'AFTER_TURN'};

        act(() => result.current.showCard(card));
        act(() => result.current.onSquareRightClick('d4'));

        expect(result.current.selectedCard.param.piece1).toBe('d4');
        expect(result.current.selectedParam).toBe('piece2');

        act(() => result.current.onSquareRightClick('d5'));
        expect(result.current.selectedCard.param.piece2).toBe('d5');
        expect(result.current.selectedParam).toBeNull();
    });

    // ── Effect selection ──

    test('right-click on effect position selects the effect', () => {
        const effects = [
            {cardEnglishName: 'MagnetismCard', cardName: 'Magnétisme', cardDescription: 'desc', positions: ['f6']},
        ];
        const {result} = setup(effects);

        act(() => result.current.onSquareRightClick('f6'));

        expect(result.current.selectedCard).not.toBeNull();
        expect(result.current.selectedCard.isEffect).toBe(true);
        expect(result.current.selectedCard.englishName).toBe('MagnetismCard');
        expect(result.current.selectedCard.effectIndices).toEqual([0]);
    });

    test('right-click on square with multiple effects selects all', () => {
        const effects = [
            {cardEnglishName: 'NeutralityCard', cardName: 'Neutralité', cardDescription: 'n', positions: ['b7']},
            {cardEnglishName: 'CrabCard', cardName: 'Crabe', cardDescription: 'c', positions: ['b7']},
        ];
        const {result} = setup(effects);

        act(() => result.current.onSquareRightClick('b7'));

        expect(result.current.selectedCard.effectIndices).toEqual([0, 1]);
    });

    test('right-clicking selected effect again deselects it', () => {
        const effects = [
            {cardEnglishName: 'MagnetismCard', cardName: 'Magnétisme', cardDescription: 'd', positions: ['f6']},
        ];
        const {result} = setup(effects);

        act(() => result.current.onSquareRightClick('f6'));
        act(() => result.current.onSquareRightClick('f6'));

        expect(result.current.selectedCard).toBeNull();
    });

    test('selectEffectByIndices selects specific effect', () => {
        const effects = [
            {cardEnglishName: 'NeutralityCard', cardName: 'Neutralité', cardDescription: 'n', positions: ['e6']},
            {cardEnglishName: 'CrabCard', cardName: 'Crabe', cardDescription: 'c', positions: ['b7']},
        ];
        const {result} = setup(effects);

        act(() => result.current.selectEffectByIndices([1]));

        expect(result.current.selectedCard.englishName).toBe('CrabCard');
        expect(result.current.selectedCard.effectIndices).toEqual([1]);
    });

    test('selectedEffectPositions returns positions of selected effects', () => {
        const effects = [
            {cardEnglishName: 'NeutralityCard', cardName: 'N', cardDescription: '', positions: ['e6']},
            {cardEnglishName: 'CrabCard', cardName: 'C', cardDescription: '', positions: ['b7']},
        ];
        const {result} = setup(effects);

        act(() => result.current.selectEffectByIndices([0, 1]));

        expect(result.current.selectedEffectPositions).toEqual(expect.arrayContaining(['e6', 'b7']));
        expect(result.current.selectedEffectPositions).toHaveLength(2);
    });

    test('selectedEffectPositions is empty when no effect selected', () => {
        const {result} = setup();
        expect(result.current.selectedEffectPositions).toEqual([]);
    });

    // ── Neutral crab scenario (2 effects on same piece) ──

    test('neutral crab: right-click selects both NeutralityCard and CrabCard effects', () => {
        const effects = [
            {cardEnglishName: 'NeutralityCard', cardName: 'Neutralité', cardDescription: 'Pièce neutre', positions: ['b7']},
            {cardEnglishName: 'CrabCard', cardName: 'Crabe', cardDescription: 'Se déplace en diagonale', positions: ['b7']},
        ];
        const {result} = setup(effects);

        act(() => result.current.onSquareRightClick('b7'));

        expect(result.current.selectedCard.isEffect).toBe(true);
        expect(result.current.selectedCard.effectIndices).toEqual([0, 1]);
        // First effect's card info is used as primary
        expect(result.current.selectedCard.englishName).toBe('NeutralityCard');
    });

    test('neutral crab: selectedEffectPositions contains the shared position once', () => {
        const effects = [
            {cardEnglishName: 'NeutralityCard', cardName: 'Neutralité', cardDescription: 'n', positions: ['b7']},
            {cardEnglishName: 'CrabCard', cardName: 'Crabe', cardDescription: 'c', positions: ['b7']},
        ];
        const {result} = setup(effects);

        act(() => result.current.onSquareRightClick('b7'));

        // b7 appears twice (once per effect), both are valid for spotlight
        expect(result.current.selectedEffectPositions).toEqual(['b7', 'b7']);
    });

    test('neutral crab: clicking individual CrabCard effect from panel selects only crab', () => {
        const effects = [
            {cardEnglishName: 'NeutralityCard', cardName: 'Neutralité', cardDescription: 'n', positions: ['b7']},
            {cardEnglishName: 'CrabCard', cardName: 'Crabe', cardDescription: 'c', positions: ['b7']},
        ];
        const {result} = setup(effects);

        act(() => result.current.selectEffectByIndices([1]));

        expect(result.current.selectedCard.englishName).toBe('CrabCard');
        expect(result.current.selectedCard.effectIndices).toEqual([1]);
    });

    test('neutral crab: selecting hand card after effect replaces selection', () => {
        const effects = [
            {cardEnglishName: 'NeutralityCard', cardName: 'Neutralité', cardDescription: 'n', positions: ['b7']},
            {cardEnglishName: 'CrabCard', cardName: 'Crabe', cardDescription: 'c', positions: ['b7']},
        ];
        const {result} = setup(effects);

        // Select both effects via right-click
        act(() => result.current.onSquareRightClick('b7'));
        expect(result.current.selectedCard.effectIndices).toEqual([0, 1]);

        // Click a hand card
        const card = {englishName: 'BombingCard', param: {position: null}, type: 'AFTER_TURN'};
        act(() => result.current.showCard(card));

        expect(result.current.selectedCard.englishName).toBe('BombingCard');
        expect(result.current.selectedCard.isEffect).toBeUndefined();
    });

    // ── Right-click priority: card param > effect ──

    test('right-click assigns param when card is selected, even if effect is on that square', () => {
        const effects = [
            {cardEnglishName: 'MagnetismCard', cardName: 'M', cardDescription: '', positions: ['e4']},
        ];
        const {result} = setup(effects);
        const card = {englishName: 'BombingCard', param: {position: null}, type: 'AFTER_TURN'};

        act(() => result.current.showCard(card));
        act(() => result.current.onSquareRightClick('e4'));

        // Should assign param, not select the effect
        expect(result.current.selectedCard.param.position).toBe('e4');
        expect(result.current.selectedCard.isEffect).toBeUndefined();
    });

    // ── Captured piece click ──

    test('clicking captured piece assigns param', () => {
        const {result} = setup();
        const card = {englishName: 'AmbitionCard', param: {piece: null}, type: 'AFTER_TURN'};

        act(() => result.current.showCard(card));
        act(() => result.current.onCapturedPieceClick(2));

        expect(result.current.selectedCard.param.piece).toBe('captured:2');
    });

    // ── Barricade ──

    test('barricade edge clicks fill params in order', () => {
        const {result} = setup();
        const card = {
            englishName: 'BarricadeCard', type: 'AFTER_TURN',
            param: {from1: null, to1: null, from2: null, to2: null},
        };

        act(() => result.current.showCard(card));
        act(() => result.current.onBarricadeEdgeClick(['e3', 'e4']));

        expect(result.current.selectedCard.param.from1).toBe('e3');
        expect(result.current.selectedCard.param.to1).toBe('e4');
        expect(result.current.barricadeEdges).toEqual([['e3', 'e4']]);

        act(() => result.current.onBarricadeEdgeClick(['f3', 'f4']));

        expect(result.current.selectedCard.param.from2).toBe('f3');
        expect(result.current.selectedCard.param.to2).toBe('f4');
        expect(result.current.barricadeEdges).toHaveLength(2);
    });

    test('barricade ignores third edge click', () => {
        const {result} = setup();
        const card = {
            englishName: 'BarricadeCard', type: 'AFTER_TURN',
            param: {from1: null, to1: null, from2: null, to2: null},
        };

        act(() => result.current.showCard(card));
        act(() => result.current.onBarricadeEdgeClick(['e3', 'e4']));
        act(() => result.current.onBarricadeEdgeClick(['f3', 'f4']));
        act(() => result.current.onBarricadeEdgeClick(['g3', 'g4']));

        expect(result.current.barricadeEdges).toHaveLength(2);
    });

    // ── Playable card types ──

    test('BEGINNING_OF_THE_TURN allows BEFORE_TURN and REPLACE_TURN', () => {
        const {result} = setup([], {state: 'BEGINNING_OF_THE_TURN'});
        expect(result.current.playableCardTypes('BEGINNING_OF_THE_TURN', false)).toEqual(['BEFORE_TURN', 'REPLACE_TURN']);
    });

    test('MOVE_WITHOUT_CARD_PLAYED allows AFTER_TURN', () => {
        const {result} = setup();
        expect(result.current.playableCardTypes('MOVE_WITHOUT_CARD_PLAYED', false)).toEqual(['AFTER_TURN']);
    });

    test('opponent always gets ENEMY_TURN', () => {
        const {result} = setup();
        expect(result.current.playableCardTypes('BEGINNING_OF_THE_TURN', true)).toEqual(['ENEMY_TURN']);
    });
});
