import {barricadeLines} from '../component/barricadeOverlay';

describe('barricadeLines', () => {
    const boardSize = 560;

    test('returns 2 lines for 4 ordered positions', () => {
        const effect = {positions: ['e3', 'e4', 'f3', 'f4']};
        const lines = barricadeLines(effect, boardSize, 'white');
        expect(lines).toHaveLength(2);
    });

    test('returns 1 line for 2 positions', () => {
        const effect = {positions: ['d4', 'd5']};
        const lines = barricadeLines(effect, boardSize, 'white');
        expect(lines).toHaveLength(1);
    });

    test('returns empty array when no positions', () => {
        expect(barricadeLines({positions: []}, boardSize, 'white')).toEqual([]);
        expect(barricadeLines({positions: null}, boardSize, 'white')).toEqual([]);
        expect(barricadeLines({}, boardSize, 'white')).toEqual([]);
    });

    test('line is perpendicular to the edge between two squares', () => {
        // e4 and f4 are horizontally adjacent -> wall is vertical between them
        const effect = {positions: ['e4', 'f4']};
        const lines = barricadeLines(effect, boardSize, 'white');
        const line = lines[0];
        // For a vertical wall, x1 === x2 (vertical line)
        expect(line.x1).toBeCloseTo(line.x2, 0);
    });

    test('positions are taken as consecutive pairs, not all combinations', () => {
        // [a1, a2, h7, h8] should give 2 lines: a1-a2 and h7-h8
        // NOT a1-h7, a1-h8, a2-h7, a2-h8
        const effect = {positions: ['a1', 'a2', 'h7', 'h8']};
        const lines = barricadeLines(effect, boardSize, 'white');
        expect(lines).toHaveLength(2);
    });

    test('respects board orientation', () => {
        const effect = {positions: ['a1', 'a2']};
        const whiteLines = barricadeLines(effect, boardSize, 'white');
        const blackLines = barricadeLines(effect, boardSize, 'black');
        // Lines should be at different positions for different orientations
        expect(whiteLines[0].y1).not.toBeCloseTo(blackLines[0].y1, 0);
    });
});
