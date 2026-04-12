import React from 'react';
import {render, screen, fireEvent} from '@testing-library/react';
import {CardParameters} from '../component/CardParameters';

describe('CardParameters', () => {
    const baseProps = {
        selectedParam: null,
        setSelectedParam: jest.fn(),
        playCardCallback: jest.fn(),
        barricadeEdges: [],
        setBarricadeEdges: jest.fn(),
    };

    test('renders card name', () => {
        render(<CardParameters {...baseProps} card={{englishName: 'TestCard', name: 'Test', param: {}, type: 'AFTER_TURN'}} />);
        expect(screen.getByText('Test')).toBeInTheDocument();
    });

    test('renders card description', () => {
        render(<CardParameters {...baseProps} card={{englishName: 'TestCard', name: 'Test', description: 'Some description', param: {}, type: 'AFTER_TURN'}} />);
        expect(screen.getByText('Some description')).toBeInTheDocument();
    });

    test('renders type badge', () => {
        render(<CardParameters {...baseProps} card={{englishName: 'TestCard', name: 'Test', param: {}, type: 'AFTER_TURN'}} isPlayable={true} />);
        expect(screen.getByText('Après le coup')).toBeInTheDocument();
    });

    test('play button enabled when all params set', () => {
        render(<CardParameters {...baseProps} card={{englishName: 'TestCard', name: 'Test', param: {position: 'e4'}, type: 'AFTER_TURN'}} isPlayable={true} />);
        const btn = screen.getByText('▶ Jouer la carte');
        expect(btn).not.toBeDisabled();
    });

    test('play button disabled when params missing', () => {
        render(<CardParameters {...baseProps} card={{englishName: 'TestCard', name: 'Test', param: {position: null}, type: 'AFTER_TURN'}} isPlayable={true} />);
        const btn = screen.getByText('Définissez tous les paramètres');
        expect(btn).toBeDisabled();
    });

    test('play button calls callback when clicked', () => {
        const playCard = jest.fn();
        render(<CardParameters {...baseProps} playCardCallback={playCard} card={{englishName: 'TestCard', name: 'Test', param: {position: 'e4'}, type: 'AFTER_TURN'}} isPlayable={true} />);
        fireEvent.click(screen.getByText('▶ Jouer la carte'));
        expect(playCard).toHaveBeenCalledTimes(1);
    });

    test('shows "cannot play" message when not playable', () => {
        render(<CardParameters {...baseProps} card={{englishName: 'TestCard', name: 'Test', param: {}, type: 'AFTER_TURN'}} isPlayable={false} />);
        expect(screen.getByText('Cette carte ne peut pas être jouée pour le moment')).toBeInTheDocument();
    });

    test('does not show play button for effects', () => {
        render(<CardParameters {...baseProps} card={{englishName: 'TestCard', name: 'Test', param: {}, type: 'AFTER_TURN', isEffect: true}} isPlayable={false} />);
        expect(screen.queryByText('▶ Jouer la carte')).not.toBeInTheDocument();
    });

    test('renders parameter rows when playable', () => {
        render(<CardParameters {...baseProps} card={{englishName: 'TestCard', name: 'Test', param: {position: null, piece: null}, type: 'AFTER_TURN'}} isPlayable={true} />);
        expect(screen.getByText('Position')).toBeInTheDocument();
        expect(screen.getByText('Pièce')).toBeInTheDocument();
    });
});
