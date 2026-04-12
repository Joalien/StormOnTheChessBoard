/**
 * Groups board positions into clusters based on proximity.
 * Positions within `threshold` pixels of any member of a cluster are merged into it.
 *
 * @param {Array<{cx: number, cy: number}>} points - center coordinates of each position
 * @param {number} threshold - max distance (px) to be considered part of the same cluster
 * @returns {Array<{cx: number, cy: number, r: number}>} one spotlight per cluster (center + radius)
 */
export function clusterSpotlights(points, threshold, baseRadius) {
    const clusters = [];
    const used = new Set();
    for (let i = 0; i < points.length; i++) {
        if (used.has(i)) continue;
        const cluster = [points[i]];
        used.add(i);
        // Keep scanning: newly added points can pull in more neighbors
        let changed = true;
        while (changed) {
            changed = false;
            for (let j = 0; j < points.length; j++) {
                if (used.has(j)) continue;
                if (cluster.some(c => Math.hypot(c.cx - points[j].cx, c.cy - points[j].cy) <= threshold)) {
                    cluster.push(points[j]);
                    used.add(j);
                    changed = true;
                }
            }
        }
        const avgX = cluster.reduce((s, c) => s + c.cx, 0) / cluster.length;
        const avgY = cluster.reduce((s, c) => s + c.cy, 0) / cluster.length;
        const maxDist = Math.max(0, ...cluster.map(c => Math.hypot(c.cx - avgX, c.cy - avgY)));
        clusters.push({cx: avgX, cy: avgY, r: maxDist + baseRadius});
    }
    return clusters;
}
