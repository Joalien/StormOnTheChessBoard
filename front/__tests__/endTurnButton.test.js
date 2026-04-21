import {endTurnButtonClassName, shouldPulseEndTurn} from '../utils/endTurnButton';

describe('shouldPulseEndTurn', () => {
    test('pulses after a move in MOVE_WITHOUT_CARD_PLAYED when it is my turn', () => {
        expect(shouldPulseEndTurn('MOVE_WITHOUT_CARD_PLAYED', true)).toBe(true);
    });

    test('pulses in END_OF_THE_TURN when it is my turn', () => {
        expect(shouldPulseEndTurn('END_OF_THE_TURN', true)).toBe(true);
    });

    test('does not pulse in BEGINNING_OF_THE_TURN', () => {
        expect(shouldPulseEndTurn('BEGINNING_OF_THE_TURN', true)).toBe(false);
    });

    test('does not pulse in BEFORE_MOVE', () => {
        expect(shouldPulseEndTurn('BEFORE_MOVE', true)).toBe(false);
    });

    test('does not pulse in PROMOTION_PENDING', () => {
        expect(shouldPulseEndTurn('PROMOTION_PENDING', true)).toBe(false);
    });

    test('does not pulse when it is not my turn, even if a move has been played', () => {
        expect(shouldPulseEndTurn('MOVE_WITHOUT_CARD_PLAYED', false)).toBe(false);
        expect(shouldPulseEndTurn('END_OF_THE_TURN', false)).toBe(false);
    });

    test('does not pulse when currentState is null (initial load)', () => {
        expect(shouldPulseEndTurn(null, true)).toBe(false);
    });
});

describe('endTurnButtonClassName', () => {
    test('returns the base classes without the ready modifier by default', () => {
        expect(endTurnButtonClassName('BEGINNING_OF_THE_TURN', true)).toBe('sotc-btn sotc-btn-end');
    });

    test('adds the ready modifier when a move has been played on my turn', () => {
        expect(endTurnButtonClassName('MOVE_WITHOUT_CARD_PLAYED', true))
            .toBe('sotc-btn sotc-btn-end sotc-btn-end--ready');
        expect(endTurnButtonClassName('END_OF_THE_TURN', true))
            .toBe('sotc-btn sotc-btn-end sotc-btn-end--ready');
    });

    test('omits the ready modifier when it is not my turn', () => {
        expect(endTurnButtonClassName('MOVE_WITHOUT_CARD_PLAYED', false)).toBe('sotc-btn sotc-btn-end');
    });
});
