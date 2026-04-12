import {clusterSpotlights} from '../hooks/clusterPositions';

describe('clusterSpotlights', () => {
    const sqSize = 70; // boardSize 560 / 8
    const baseRadius = sqSize * 1.8;
    const threshold = sqSize * 5;

    function pt(file, rank) {
        // Simulate squareToCoords for white orientation
        return {cx: file * sqSize + sqSize / 2, cy: (7 - rank) * sqSize + sqSize / 2};
    }

    test('single position produces one cluster', () => {
        const spots = clusterSpotlights([pt(5, 5)], threshold, baseRadius); // f6
        expect(spots).toHaveLength(1);
    });

    test('barricade: 4 adjacent positions produce one cluster', () => {
        // e3, e4, f3, f4 (file 4, ranks 2-3, files 4-5)
        const points = [pt(4, 2), pt(4, 3), pt(5, 2), pt(5, 3)];
        const spots = clusterSpotlights(points, threshold, baseRadius);
        expect(spots).toHaveLength(1);
    });

    test('manhole: 2 distant positions (a1, h8) produce two clusters', () => {
        const points = [pt(0, 0), pt(7, 7)]; // a1 and h8
        const spots = clusterSpotlights(points, threshold, baseRadius);
        expect(spots).toHaveLength(2);
    });

    test('manhole: 2 positions 4 squares apart merge into one cluster', () => {
        const points = [pt(2, 2), pt(6, 2)]; // c3 and g3 (4 files apart)
        const spots = clusterSpotlights(points, threshold, baseRadius);
        expect(spots).toHaveLength(1);
    });

    test('manhole: 2 positions 4 squares apart diagonally merge into one cluster', () => {
        const points = [pt(2, 2), pt(5, 5)]; // c3 and f6 (~4.2 squares diagonal)
        const spots = clusterSpotlights(points, threshold, baseRadius);
        expect(spots).toHaveLength(1);
    });

    test('manhole: 2 positions 6 squares apart produce two clusters', () => {
        const points = [pt(0, 0), pt(6, 0)]; // a1 and g1 (6 files apart)
        const spots = clusterSpotlights(points, threshold, baseRadius);
        expect(spots).toHaveLength(2);
    });

    test('cluster radius covers all its points', () => {
        const points = [pt(4, 2), pt(4, 3), pt(5, 2), pt(5, 3)];
        const spots = clusterSpotlights(points, threshold, baseRadius);
        const spot = spots[0];
        points.forEach(p => {
            const dist = Math.hypot(p.cx - spot.cx, p.cy - spot.cy);
            expect(dist).toBeLessThan(spot.r);
        });
    });

    test('empty input returns empty', () => {
        expect(clusterSpotlights([], threshold, baseRadius)).toEqual([]);
    });

    test('chain clustering: A close to B, B close to C, A far from C still one cluster', () => {
        // 3 points in a line, each 3 squares apart (total span = 6, but chained)
        const points = [pt(0, 0), pt(3, 0), pt(6, 0)];
        const spots = clusterSpotlights(points, threshold, baseRadius);
        expect(spots).toHaveLength(1);
    });
});
