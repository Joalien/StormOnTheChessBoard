import React from 'react';
import {render, screen, fireEvent} from '@testing-library/react';
import {HomeScreen} from '../component/HomeScreen';

describe('HomeScreen', () => {
    const noop = () => {};

    test('renders the three primary buttons by default', () => {
        render(<HomeScreen onPlaySolo={noop} onMatchmaking={noop} onPlayAi={noop} />);
        expect(screen.getByText('Jouer !')).toBeInTheDocument();
        expect(screen.getByText('Jouer contre une IA')).toBeInTheDocument();
        expect(screen.getByText("S'entraîner")).toBeInTheDocument();
    });

    test('clicking matchmaking button calls onMatchmaking', () => {
        const onMatchmaking = jest.fn();
        render(<HomeScreen onPlaySolo={noop} onMatchmaking={onMatchmaking} onPlayAi={noop} />);
        fireEvent.click(screen.getByText('Jouer !'));
        expect(onMatchmaking).toHaveBeenCalledTimes(1);
    });

    test('clicking solo button calls onPlaySolo', () => {
        const onPlaySolo = jest.fn();
        render(<HomeScreen onPlaySolo={onPlaySolo} onMatchmaking={noop} onPlayAi={noop} />);
        fireEvent.click(screen.getByText("S'entraîner"));
        expect(onPlaySolo).toHaveBeenCalledTimes(1);
    });

    test('strategy popup is hidden by default', () => {
        render(<HomeScreen onPlaySolo={noop} onMatchmaking={noop} onPlayAi={noop} />);
        expect(screen.queryByLabelText(/Stockfish/)).not.toBeInTheDocument();
        expect(screen.queryByLabelText(/Heuristique matérielle/)).not.toBeInTheDocument();
        expect(screen.queryByLabelText(/Aléatoire/)).not.toBeInTheDocument();
    });

    test('clicking AI button opens the strategy popup without calling onPlayAi yet', () => {
        const onPlayAi = jest.fn();
        render(<HomeScreen onPlaySolo={noop} onMatchmaking={noop} onPlayAi={onPlayAi} />);

        fireEvent.click(screen.getByText('Jouer contre une IA'));

        expect(onPlayAi).not.toHaveBeenCalled();
        expect(screen.getByLabelText(/Stockfish/)).toBeInTheDocument();
        expect(screen.getByLabelText(/Heuristique matérielle/)).toBeInTheDocument();
        expect(screen.getByLabelText(/Aléatoire/)).toBeInTheDocument();
        // the main AI button stays visible (popup floats above it)
        expect(screen.getByText('Jouer contre une IA')).toBeInTheDocument();
    });

    test('picking the random strategy forwards "random"', () => {
        const onPlayAi = jest.fn();
        render(<HomeScreen onPlaySolo={noop} onMatchmaking={noop} onPlayAi={onPlayAi} />);

        fireEvent.click(screen.getByText('Jouer contre une IA'));
        fireEvent.click(screen.getByLabelText(/Aléatoire/));

        expect(onPlayAi).toHaveBeenCalledTimes(1);
        expect(onPlayAi).toHaveBeenCalledWith('random');
    });

    test('picking the material strategy forwards "material"', () => {
        const onPlayAi = jest.fn();
        render(<HomeScreen onPlaySolo={noop} onMatchmaking={noop} onPlayAi={onPlayAi} />);

        fireEvent.click(screen.getByText('Jouer contre une IA'));
        fireEvent.click(screen.getByLabelText(/Heuristique matérielle/));

        expect(onPlayAi).toHaveBeenCalledWith('material');
    });

    test('picking the stockfish strategy forwards "stockfish"', () => {
        const onPlayAi = jest.fn();
        render(<HomeScreen onPlaySolo={noop} onMatchmaking={noop} onPlayAi={onPlayAi} />);

        fireEvent.click(screen.getByText('Jouer contre une IA'));
        fireEvent.click(screen.getByLabelText(/Stockfish/));

        expect(onPlayAi).toHaveBeenCalledWith('stockfish');
    });

    test('clicking the AI button again closes the strategy popup', () => {
        render(<HomeScreen onPlaySolo={noop} onMatchmaking={noop} onPlayAi={noop} />);
        const aiButton = screen.getByText('Jouer contre une IA');

        fireEvent.click(aiButton);
        expect(screen.getByLabelText(/Stockfish/)).toBeInTheDocument();

        fireEvent.click(aiButton);
        expect(screen.queryByLabelText(/Stockfish/)).not.toBeInTheDocument();
    });
});
