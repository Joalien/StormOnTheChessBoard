/**
 * Tests for barricade edge detection geometry.
 * nearestEdge is not exported, so we test it indirectly via the BarricadeSelectionOverlay behavior,
 * or we re-export it. For now, we test the exported squareCenter and edgeLine via barricadeLines.
 */
import {barricadeLines} from '../component/barricadeOverlay';

describe('barricadeLines geometry', () => {
    const boardSize = 560;
    const sqSize = 70;

    test('vertical wall between e4 and f4 is a vertical line', () => {
        const lines = barricadeLines({positions: ['e4', 'f4']}, boardSize, 'white');
        expect(lines).toHaveLength(1);
        const line = lines[0];
        // Vertical wall: x1 === x2
        expect(line.x1).toBeCloseTo(line.x2, 1);
        // Wall spans one square height
        expect(Math.abs(line.y1 - line.y2)).toBeCloseTo(sqSize, 1);
    });

    test('horizontal wall between d4 and d5 is a horizontal line', () => {
        const lines = barricadeLines({positions: ['d4', 'd5']}, boardSize, 'white');
        expect(lines).toHaveLength(1);
        const line = lines[0];
        // Horizontal wall: y1 === y2
        expect(line.y1).toBeCloseTo(line.y2, 1);
        // Wall spans one square width
        expect(Math.abs(line.x1 - line.x2)).toBeCloseTo(sqSize, 1);
    });

    test('straight barricade (4 positions) produces 2 lines', () => {
        const lines = barricadeLines({positions: ['e3', 'e4', 'f3', 'f4']}, boardSize, 'white');
        expect(lines).toHaveLength(2);
    });

    test('L-shaped barricade (3 unique positions) produces 2 lines', () => {
        // Shared corner: e4 appears in both edges
        const lines = barricadeLines({positions: ['e3', 'e4', 'e4', 'f4']}, boardSize, 'white');
        expect(lines).toHaveLength(2);
    });

    test('wall midpoint is between the two squares', () => {
        const lines = barricadeLines({positions: ['d4', 'e4']}, boardSize, 'white');
        const line = lines[0];
        const midX = (line.x1 + line.x2) / 2;
        const midY = (line.y1 + line.y2) / 2;
        // Midpoint should be at the border between d4 and e4
        const d4center = {x: 3 * sqSize + sqSize / 2, y: (7 - 3) * sqSize + sqSize / 2};
        const e4center = {x: 4 * sqSize + sqSize / 2, y: (7 - 3) * sqSize + sqSize / 2};
        const expectedMidX = (d4center.x + e4center.x) / 2;
        const expectedMidY = (d4center.y + e4center.y) / 2;
        expect(midX).toBeCloseTo(expectedMidX, 1);
        expect(midY).toBeCloseTo(expectedMidY, 1);
    });

    test('black orientation flips coordinates', () => {
        const whiteLines = barricadeLines({positions: ['a1', 'a2']}, boardSize, 'white');
        const blackLines = barricadeLines({positions: ['a1', 'a2']}, boardSize, 'black');
        // Different positions due to board flip
        expect(whiteLines[0].x1).not.toBeCloseTo(blackLines[0].x1, 0);
    });
});
