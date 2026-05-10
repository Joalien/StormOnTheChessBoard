import {renderHook, act} from '@testing-library/react';
import {useOpponentCardAnimations} from '../hooks/useOpponentCardAnimations';

function rectRef(rect) {
    return {current: {getBoundingClientRect: () => rect}};
}

describe('useOpponentCardAnimations', () => {
    const baseProps = {
        effects: [],
        myColor: 'white',
        currentPlayerColor: 'white',
        topPlayerRef: rectRef({left: 100, top: 0, width: 600, height: 100}),
        bottomPlayerRef: rectRef({left: 100, top: 600, width: 600, height: 100}),
        boardRef: rectRef({left: 200, top: 100, width: 400, height: 400}),
        activeEffectsPanelRef: rectRef({left: 800, top: 200, width: 320, height: 200}),
    };

    test('first render with non-empty history does not animate retroactively', () => {
        const history = [{index: 0, color: 'white', action: 'Start'}];
        const {result} = renderHook(({history}) => useOpponentCardAnimations({...baseProps, history}), {
            initialProps: {history},
        });
        expect(result.current.animations).toHaveLength(0);
    });

    test('opponent card play emits an animation', () => {
        const initial = [{index: 0, color: 'white', action: 'Start'}];
        const {result, rerender} = renderHook(({history}) => useOpponentCardAnimations({...baseProps, history}), {
            initialProps: {history: initial},
        });

        rerender({history: [
            ...initial,
            {index: 1, color: 'black', action: 'card', cardEnglishName: 'BombingCard'},
        ]});

        expect(result.current.animations).toHaveLength(1);
        expect(result.current.animations[0].cardEnglishName).toBe('BombingCard');
    });

    test('own card plays do not animate', () => {
        const initial = [{index: 0, color: 'white', action: 'Start'}];
        const {result, rerender} = renderHook(({history}) => useOpponentCardAnimations({...baseProps, history}), {
            initialProps: {history: initial},
        });

        rerender({history: [
            ...initial,
            {index: 1, color: 'white', action: 'card', cardEnglishName: 'BombingCard'},
        ]});

        expect(result.current.animations).toHaveLength(0);
    });

    test('non-card history entries do not animate', () => {
        const initial = [{index: 0, color: 'white', action: 'Start'}];
        const {result, rerender} = renderHook(({history}) => useOpponentCardAnimations({...baseProps, history}), {
            initialProps: {history: initial},
        });

        rerender({history: [
            ...initial,
            {index: 1, color: 'black', action: 'Move e7 to e5'},
        ]});

        expect(result.current.animations).toHaveLength(0);
    });

    test('effect target is the centre of the active-effects panel when card has a matching effect', () => {
        const initial = [{index: 0, color: 'white', action: 'Start'}];
        const effects = [{name: 'BombingEffect', cardEnglishName: 'BombingCard', positions: ['e4']}];
        const {result, rerender} = renderHook(({history, effects}) => useOpponentCardAnimations({...baseProps, history, effects}), {
            initialProps: {history: initial, effects: []},
        });

        rerender({history: [
            ...initial,
            {index: 1, color: 'black', action: 'card', cardEnglishName: 'BombingCard'},
        ], effects});

        // Panel is at left=800, top=200, w=320, h=200 → centre is (960, 300).
        expect(result.current.animations[0].effectTarget).toEqual({x: 960, y: 300});
    });

    test('effect target is null if the active-effects panel ref is unmounted', () => {
        const initial = [{index: 0, color: 'white', action: 'Start'}];
        const effects = [{name: 'BombingEffect', cardEnglishName: 'BombingCard', positions: ['e4']}];
        const noPanel = {...baseProps, activeEffectsPanelRef: {current: null}};
        const {result, rerender} = renderHook(({history, effects}) => useOpponentCardAnimations({...noPanel, history, effects}), {
            initialProps: {history: initial, effects: []},
        });

        rerender({history: [
            ...initial,
            {index: 1, color: 'black', action: 'card', cardEnglishName: 'BombingCard'},
        ], effects});

        expect(result.current.animations[0].effectTarget).toBeNull();
    });

    test('cards without an effect have a null effectTarget (will fade out)', () => {
        const initial = [{index: 0, color: 'white', action: 'Start'}];
        const {result, rerender} = renderHook(({history}) => useOpponentCardAnimations({...baseProps, history}), {
            initialProps: {history: initial},
        });

        rerender({history: [
            ...initial,
            {index: 1, color: 'black', action: 'card', cardEnglishName: 'PsychopathCard'},
        ]});

        expect(result.current.animations[0].effectTarget).toBeNull();
    });

    test('dismiss removes an animation by id', () => {
        const initial = [{index: 0, color: 'white', action: 'Start'}];
        const {result, rerender} = renderHook(({history}) => useOpponentCardAnimations({...baseProps, history}), {
            initialProps: {history: initial},
        });

        rerender({history: [
            ...initial,
            {index: 1, color: 'black', action: 'card', cardEnglishName: 'BombingCard'},
        ]});

        const id = result.current.animations[0].id;
        act(() => result.current.dismiss(id));
        expect(result.current.animations).toHaveLength(0);
    });
});
