import {useState} from "react";

const DEFAULT_BOARD_SIZE = 560;

/**
 * Compute pixel coords for the wall line of an edge between two adjacent squares.
 */
function edgeLine(sq1, sq2, orientation, size = DEFAULT_BOARD_SIZE) {
    const c1 = squareCenter(sq1, orientation, size);
    const c2 = squareCenter(sq2, orientation, size);
    const midX = (c1.x + c2.x) / 2;
    const midY = (c1.y + c2.y) / 2;
    const dx = c2.x - c1.x;
    const dy = c2.y - c1.y;
    // Perpendicular, length = one square
    return {
        x1: midX - dy * 0.5,
        y1: midY + dx * 0.5,
        x2: midX + dy * 0.5,
        y2: midY - dx * 0.5,
    };
}

function squareCenter(square, orientation, size = DEFAULT_BOARD_SIZE) {
    const sq = size / 8;
    const file = square.charCodeAt(0) - 97;
    const rank = parseInt(square[1]) - 1;
    if (orientation === 'white') {
        return {x: file * sq + sq / 2, y: (7 - rank) * sq + sq / 2};
    } else {
        return {x: (7 - file) * sq + sq / 2, y: rank * sq + sq / 2};
    }
}

/**
 * Convert pixel coordinates to the nearest edge (pair of adjacent squares).
 * Returns null if too far from any edge.
 */
function nearestEdge(px, py, orientation, size = DEFAULT_BOARD_SIZE) {
    const sq = size / 8;
    // Convert pixel to board-relative file/rank (fractional)
    let fFrac, rFrac;
    if (orientation === 'white') {
        fFrac = px / sq; // 0..8
        rFrac = (size - py) / sq; // 0..8 (rank 1 at bottom)
    } else {
        fFrac = (size - px) / sq;
        rFrac = py / sq;
    }

    // Find closest horizontal edge (between rows) and vertical edge (between files)
    // Horizontal edge: at rFrac close to an integer boundary (1..7)
    const nearestRow = Math.round(rFrac);
    const rowDist = Math.abs(rFrac - nearestRow);
    // Vertical edge: at fFrac close to an integer boundary (1..7)
    const nearestFile = Math.round(fFrac);
    const fileDist = Math.abs(fFrac - nearestFile);

    const THRESHOLD = 0.3; // must be within 30% of a square from the edge

    let edgeType, edgeIdx, squareIdx;

    if (rowDist < fileDist && rowDist < THRESHOLD && nearestRow >= 1 && nearestRow <= 7) {
        // Closest to a horizontal edge (between row nearestRow and nearestRow+1)
        edgeType = 'h';
        edgeIdx = nearestRow; // row boundary
        squareIdx = Math.floor(fFrac);
        if (squareIdx < 0) squareIdx = 0;
        if (squareIdx > 7) squareIdx = 7;
    } else if (fileDist < THRESHOLD && nearestFile >= 1 && nearestFile <= 7) {
        // Closest to a vertical edge (between file nearestFile-1 and nearestFile)
        edgeType = 'v';
        edgeIdx = nearestFile;
        squareIdx = Math.floor(rFrac);
        if (squareIdx < 0) squareIdx = 0;
        if (squareIdx > 7) squareIdx = 7;
    } else {
        return null;
    }

    // Convert to position pair
    if (edgeType === 'h') {
        // Horizontal edge between row edgeIdx (1-based) and row edgeIdx+1
        const file = String.fromCharCode(97 + squareIdx);
        const sq1 = file + edgeIdx;       // lower row
        const sq2 = file + (edgeIdx + 1); // upper row
        return [sq1, sq2];
    } else {
        // Vertical edge between file edgeIdx-1 and file edgeIdx
        const file1 = String.fromCharCode(97 + edgeIdx - 1);
        const file2 = String.fromCharCode(97 + edgeIdx);
        const row = squareIdx + 1;
        return [file1 + row, file2 + row];
    }
}

/**
 * Renders barricade wall lines for existing effects on the board.
 * Positions are ordered as [from1, to1, from2, to2] — taken as consecutive pairs.
 */
export function barricadeLines(effect, boardWidth, orientation) {
    const positions = effect.positions;
    if (!positions || positions.length < 2) return [];
    const lines = [];
    for (let i = 0; i + 1 < positions.length; i += 2) {
        lines.push(edgeLine(positions[i], positions[i + 1], orientation, boardWidth));
    }
    return lines;
}

/**
 * Interactive overlay for selecting barricade edges.
 * Shows when a BarricadeCard is selected and edges need to be picked.
 */
export function BarricadeSelectionOverlay({orientation, selectedEdges, onEdgeClick, boardSize = DEFAULT_BOARD_SIZE}) {
    const [hoveredEdge, setHoveredEdge] = useState(null);

    function handleMouseMove(e) {
        const rect = e.currentTarget.getBoundingClientRect();
        const px = e.clientX - rect.left;
        const py = e.clientY - rect.top;
        const edge = nearestEdge(px, py, orientation, boardSize);
        setHoveredEdge(edge);
    }

    function handleMouseLeave() {
        setHoveredEdge(null);
    }

    function handleClick() {
        if (hoveredEdge) {
            onEdgeClick(hoveredEdge);
        }
    }

    // Collect all lines to draw: selected edges + hovered edge
    const lines = [];

    selectedEdges.forEach((edge, i) => {
        const line = edgeLine(edge[0], edge[1], orientation, boardSize);
        lines.push({...line, color: '#3fb950', width: 6, key: `sel-${i}`});
    });

    if (hoveredEdge) {
        const alreadySelected = selectedEdges.some(
            e => (e[0] === hoveredEdge[0] && e[1] === hoveredEdge[1]) ||
                 (e[0] === hoveredEdge[1] && e[1] === hoveredEdge[0])
        );
        if (!alreadySelected) {
            const line = edgeLine(hoveredEdge[0], hoveredEdge[1], orientation, boardSize);
            lines.push({...line, color: 'rgba(212, 168, 67, 0.7)', width: 6, key: 'hover'});
        }
    }

    return (
        <svg
            style={{
                position: 'absolute', top: 0, left: 0,
                width: boardSize, height: boardSize,
                zIndex: 10,
                cursor: hoveredEdge ? 'pointer' : 'default',
            }}
            onMouseMove={handleMouseMove}
            onMouseLeave={handleMouseLeave}
            onClick={handleClick}
        >
            {lines.map(l => (
                <line key={l.key}
                    x1={l.x1} y1={l.y1} x2={l.x2} y2={l.y2}
                    stroke={l.color} strokeWidth={l.width} strokeLinecap="round"
                />
            ))}
        </svg>
    );
}
