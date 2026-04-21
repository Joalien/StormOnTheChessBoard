const MOVE_PLAYED_STATES = ['MOVE_WITHOUT_CARD_PLAYED', 'END_OF_THE_TURN'];

export function shouldPulseEndTurn(currentState, isMyTurn) {
    return isMyTurn && MOVE_PLAYED_STATES.includes(currentState);
}

export function endTurnButtonClassName(currentState, isMyTurn) {
    return `sotc-btn sotc-btn-end${shouldPulseEndTurn(currentState, isMyTurn) ? ' sotc-btn-end--ready' : ''}`;
}
