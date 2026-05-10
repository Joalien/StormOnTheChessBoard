import React from 'react';
import {render, screen} from '@testing-library/react';
import {HistoryPanel} from '../component/HistoryPanel';

describe('HistoryPanel', () => {
    test('renders nothing when history is empty', () => {
        const {container} = render(<HistoryPanel history={[]}/>);
        expect(container.firstChild).toBeNull();
    });

    test('renders nothing when history is undefined', () => {
        const {container} = render(<HistoryPanel history={undefined}/>);
        expect(container.firstChild).toBeNull();
    });

    test('renders an entry per command', () => {
        const history = [
            {index: 0, color: 'system', action: 'Start game', instant: '2026-05-10T08:00:00Z'},
            {index: 1, color: 'white', action: 'Move e2 to e4', instant: '2026-05-10T08:00:01Z'},
            {index: 2, color: 'white', action: 'Pass turn', instant: '2026-05-10T08:00:02Z'},
            {index: 3, color: 'black', action: 'Move e7 to e5', instant: '2026-05-10T08:00:03Z'},
        ];
        render(<HistoryPanel history={history}/>);
        expect(screen.getByText('Start game')).toBeInTheDocument();
        expect(screen.getByText('Move e2 to e4')).toBeInTheDocument();
        expect(screen.getByText('Pass turn')).toBeInTheDocument();
        expect(screen.getByText('Move e7 to e5')).toBeInTheDocument();
    });

    test('shows the section title', () => {
        const history = [{index: 0, color: 'white', action: 'Move e2 to e4', instant: '2026-05-10T08:00:00Z'}];
        render(<HistoryPanel history={history}/>);
        expect(screen.getByText('Historique')).toBeInTheDocument();
    });

    test('renders entries with their index', () => {
        const history = [
            {index: 0, color: 'system', action: 'Start game', instant: '2026-05-10T08:00:00Z'},
            {index: 5, color: 'black', action: 'Play card Attentat with parameters {position=e4}', instant: '2026-05-10T08:00:05Z'},
        ];
        render(<HistoryPanel history={history}/>);
        expect(screen.getByText('0')).toBeInTheDocument();
        expect(screen.getByText('5')).toBeInTheDocument();
    });
});
