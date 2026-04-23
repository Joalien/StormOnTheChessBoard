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
        act(() => result.current.selectEffectByIndices([0]));
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

    // ── Card param assignment via left-click (onBoardSquareClick) ──

    test('left-click on square assigns param to selected card', () => {
        const {result} = setup();
        const card = {englishName: 'BombingCard', param: {position: null}, type: 'AFTER_TURN'};

        act(() => result.current.showCard(card));
        act(() => result.current.onBoardSquareClick('e4'));

        expect(result.current.selectedCard.param.position).toBe('e4');
        expect(result.current.selectedParam).toBeNull(); // all params set
    });

    test('left-click returns true when param was assigned', () => {
        const {result} = setup();
        const card = {englishName: 'BombingCard', param: {position: null}, type: 'AFTER_TURN'};
        act(() => result.current.showCard(card));

        let consumed;
        act(() => { consumed = result.current.onBoardSquareClick('e4'); });

        expect(consumed).toBe(true);
    });

    test('left-click returns false when no card is selected', () => {
        const {result} = setup();

        let consumed;
        act(() => { consumed = result.current.onBoardSquareClick('e4'); });

        expect(consumed).toBe(false);
    });

    test('left-click different square replaces the param', () => {
        const {result} = setup();
        const card = {englishName: 'BombingCard', param: {position: null, extra: null}, type: 'AFTER_TURN'};

        act(() => result.current.showCard(card));
        act(() => result.current.onBoardSquareClick('e4'));

        expect(result.current.selectedCard.param.position).toBe('e4');
        expect(result.current.selectedParam).toBe('extra');

        act(() => result.current.onBoardSquareClick('d5'));
        expect(result.current.selectedCard.param.extra).toBe('d5');
    });

    test('multi-param card advances to next unset param', () => {
        const {result} = setup();
        const card = {englishName: 'AsylumCard', param: {piece1: null, piece2: null}, type: 'AFTER_TURN'};

        act(() => result.current.showCard(card));
        act(() => result.current.onBoardSquareClick('d4'));

        expect(result.current.selectedCard.param.piece1).toBe('d4');
        expect(result.current.selectedParam).toBe('piece2');

        act(() => result.current.onBoardSquareClick('d5'));
        expect(result.current.selectedCard.param.piece2).toBe('d5');
        expect(result.current.selectedParam).toBeNull();
    });

    // ── Effect selection (via selectEffectByIndices, appelé depuis App.js au clic gauche) ──

    test('selectEffectByIndices selects the effect at given index', () => {
        const effects = [
            {cardEnglishName: 'MagnetismCard', cardName: 'Magnétisme', cardDescription: 'desc', positions: ['f6']},
        ];
        const {result} = setup(effects);

        act(() => result.current.selectEffectByIndices([0]));

        expect(result.current.selectedCard).not.toBeNull();
        expect(result.current.selectedCard.isEffect).toBe(true);
        expect(result.current.selectedCard.englishName).toBe('MagnetismCard');
        expect(result.current.selectedCard.effectIndices).toEqual([0]);
    });

    test('selectEffectByIndices with multiple indices selects all', () => {
        const effects = [
            {cardEnglishName: 'NeutralityCard', cardName: 'Neutralité', cardDescription: 'n', positions: ['b7']},
            {cardEnglishName: 'CrabCard', cardName: 'Crabe', cardDescription: 'c', positions: ['b7']},
        ];
        const {result} = setup(effects);

        act(() => result.current.selectEffectByIndices([0, 1]));

        expect(result.current.selectedCard.effectIndices).toEqual([0, 1]);
    });

    test('selectEffectByIndices same effect twice deselects it', () => {
        const effects = [
            {cardEnglishName: 'MagnetismCard', cardName: 'Magnétisme', cardDescription: 'd', positions: ['f6']},
        ];
        const {result} = setup(effects);

        act(() => result.current.selectEffectByIndices([0]));
        act(() => result.current.selectEffectByIndices([0]));

        expect(result.current.selectedCard).toBeNull();
    });

    // ── Right-click: annulation universelle ──

    test('right-click cancels card selection', () => {
        const {result} = setup();
        act(() => result.current.showCard({englishName: 'BombingCard', param: {position: null}, type: 'AFTER_TURN'}));

        act(() => result.current.onSquareRightClick('e4'));

        expect(result.current.selectedCard).toBeNull();
        expect(result.current.selectedParam).toBeNull();
    });

    test('right-click does nothing when nothing is selected', () => {
        const {result} = setup();
        act(() => result.current.onSquareRightClick('e4'));
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

    // ── Manhole scenario (1 effet sur 2 cases distantes) ──

    test('manhole: selectEffectByIndices sélectionne l\'effet depuis n\'importe quelle case', () => {
        const effects = [
            {cardEnglishName: 'ManHoleCard', cardName: 'Bouche d\'égout', cardDescription: 'Téléportation', positions: ['a1', 'h8']},
        ];
        const {result} = setup(effects);

        act(() => result.current.selectEffectByIndices([0]));

        expect(result.current.selectedCard.isEffect).toBe(true);
        expect(result.current.selectedCard.englishName).toBe('ManHoleCard');
        expect(result.current.selectedCard.effectIndices).toEqual([0]);
    });

    test('manhole: selectedEffectPositions contains both holes', () => {
        const effects = [
            {cardEnglishName: 'ManHoleCard', cardName: 'Bouche d\'égout', cardDescription: 'Téléportation', positions: ['a1', 'h8']},
        ];
        const {result} = setup(effects);

        act(() => result.current.selectEffectByIndices([0]));

        expect(result.current.selectedEffectPositions).toEqual(['a1', 'h8']);
    });

    // ── Neutral crab scenario (2 effets sur la même pièce) ──

    test('neutral crab: selectEffectByIndices [0,1] sélectionne les deux effets', () => {
        const effects = [
            {cardEnglishName: 'NeutralityCard', cardName: 'Neutralité', cardDescription: 'Pièce neutre', positions: ['b7']},
            {cardEnglishName: 'CrabCard', cardName: 'Crabe', cardDescription: 'Se déplace en diagonale', positions: ['b7']},
        ];
        const {result} = setup(effects);

        act(() => result.current.selectEffectByIndices([0, 1]));

        expect(result.current.selectedCard.isEffect).toBe(true);
        expect(result.current.selectedCard.effectIndices).toEqual([0, 1]);
        expect(result.current.selectedCard.englishName).toBe('NeutralityCard');
    });

    test('neutral crab: selectedEffectPositions contains the shared position once per effect', () => {
        const effects = [
            {cardEnglishName: 'NeutralityCard', cardName: 'Neutralité', cardDescription: 'n', positions: ['b7']},
            {cardEnglishName: 'CrabCard', cardName: 'Crabe', cardDescription: 'c', positions: ['b7']},
        ];
        const {result} = setup(effects);

        act(() => result.current.selectEffectByIndices([0, 1]));

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

        act(() => result.current.selectEffectByIndices([0, 1]));
        expect(result.current.selectedCard.effectIndices).toEqual([0, 1]);

        const card = {englishName: 'BombingCard', param: {position: null}, type: 'AFTER_TURN'};
        act(() => result.current.showCard(card));

        expect(result.current.selectedCard.englishName).toBe('BombingCard');
        expect(result.current.selectedCard.isEffect).toBeUndefined();
    });

    // ── Priorité clic gauche : paramètre carte > effet ──

    test('left-click assigns param when card is selected, even if effect is on that square', () => {
        const effects = [
            {cardEnglishName: 'MagnetismCard', cardName: 'M', cardDescription: '', positions: ['e4']},
        ];
        const {result} = setup(effects);
        const card = {englishName: 'BombingCard', param: {position: null}, type: 'AFTER_TURN'};

        act(() => result.current.showCard(card));
        act(() => result.current.onBoardSquareClick('e4'));

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

    // ── List params (Leapfrog / SerialKiller) ──

    test('list param: left-clicks accumulate positions in array', () => {
        const {result} = setup([], {state: 'BEGINNING_OF_THE_TURN'});
        const card = {englishName: 'LeapfrogCard', param: {pawn: null, positions: []}, type: 'REPLACE_TURN', listParams: ['positions']};

        act(() => result.current.showCard(card));
        expect(result.current.selectedParam).toBe('pawn');

        act(() => result.current.onBoardSquareClick('d2'));
        expect(result.current.selectedCard.param.pawn).toBe('d2');
        expect(result.current.selectedParam).toBe('positions');

        act(() => result.current.onBoardSquareClick('f4'));
        expect(result.current.selectedCard.param.positions).toEqual(['f4']);
        expect(result.current.selectedParam).toBe('positions');

        act(() => result.current.onBoardSquareClick('h6'));
        expect(result.current.selectedCard.param.positions).toEqual(['f4', 'h6']);
    });

    test('list param: left-clicking same square removes it', () => {
        const {result} = setup([], {state: 'BEGINNING_OF_THE_TURN'});
        const card = {englishName: 'LeapfrogCard', param: {pawn: null, positions: []}, type: 'REPLACE_TURN', listParams: ['positions']};

        act(() => result.current.showCard(card));
        act(() => result.current.onBoardSquareClick('d2'));
        act(() => result.current.onBoardSquareClick('f4'));
        act(() => result.current.onBoardSquareClick('h6'));
        act(() => result.current.onBoardSquareClick('f4'));

        expect(result.current.selectedCard.param.positions).toEqual(['h6']);
    });

    test('list param: selectedParam stays on list after scalar params filled', () => {
        const {result} = setup([], {state: 'BEGINNING_OF_THE_TURN'});
        const card = {englishName: 'LeapfrogCard', param: {pawn: null, positions: []}, type: 'REPLACE_TURN', listParams: ['positions']};

        act(() => result.current.showCard(card));
        act(() => result.current.onBoardSquareClick('d2'));

        expect(result.current.selectedParam).toBe('positions');

        act(() => result.current.onBoardSquareClick('f4'));
        expect(result.current.selectedParam).toBe('positions');
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

    test('opponent always gets ENEMY_TURN types', () => {
        const {result} = setup();
        expect(result.current.playableCardTypes('BEGINNING_OF_THE_TURN', true)).toEqual(['ENEMY_TURN_AFTER_MOVE', 'ENEMY_TURN_AFTER_CARD']);
    });

    test('en mode multijoueur, ENEMY_TURN_AFTER_MOVE jouable quand l\'adversaire a bougé', () => {
        const {result} = setup([], {myColor: 'white', currentPlayerColor: 'black', state: 'MOVE_WITHOUT_CARD_PLAYED'});
        expect(result.current.playableCardTypes('MOVE_WITHOUT_CARD_PLAYED', false)).toEqual(['ENEMY_TURN_AFTER_MOVE']);
    });

    test('en mode multijoueur, ENEMY_TURN_AFTER_CARD jouable en fin de tour adverse', () => {
        const {result} = setup([], {myColor: 'white', currentPlayerColor: 'black', state: 'END_OF_THE_TURN'});
        expect(result.current.playableCardTypes('END_OF_THE_TURN', false)).toEqual(['ENEMY_TURN_AFTER_MOVE', 'ENEMY_TURN_AFTER_CARD']);
    });

    test('en mode multijoueur, rien de jouable au début du tour adverse', () => {
        const {result} = setup([], {myColor: 'white', currentPlayerColor: 'black', state: 'BEGINNING_OF_THE_TURN'});
        expect(result.current.playableCardTypes('BEGINNING_OF_THE_TURN', false)).toEqual([]);
    });
});
