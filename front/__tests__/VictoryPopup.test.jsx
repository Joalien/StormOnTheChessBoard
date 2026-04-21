import React from 'react';
import {render, screen, fireEvent} from '@testing-library/react';
import {VictoryPopup} from '../component/VictoryPopup';

describe('VictoryPopup', () => {
    test('renders the win title with "gagné" for the "win" outcome', () => {
        render(<VictoryPopup outcome="win" onClose={() => {}} onGoHome={() => {}} />);

        expect(screen.getByText(/gagné/i)).toBeInTheDocument();
        expect(screen.getByText('Voir la partie')).toBeInTheDocument();
        expect(screen.getByText(/Retour à l'accueil/)).toBeInTheDocument();
    });

    test('renders a lose title for the "lose" outcome', () => {
        render(<VictoryPopup outcome="lose" onClose={() => {}} onGoHome={() => {}} />);

        expect(screen.getByText(/perdu/i)).toBeInTheDocument();
    });

    test('renders a draw title for the "draw" outcome', () => {
        render(<VictoryPopup outcome="draw" onClose={() => {}} onGoHome={() => {}} />);

        expect(screen.getByText(/Match nul/i)).toBeInTheDocument();
    });

    test('clicking "Voir la partie" fires onClose', () => {
        const onClose = jest.fn();
        render(<VictoryPopup outcome="win" onClose={onClose} onGoHome={() => {}} />);

        fireEvent.click(screen.getByText('Voir la partie'));

        expect(onClose).toHaveBeenCalledTimes(1);
    });

    test('clicking "Retour à l\'accueil" fires onGoHome', () => {
        const onGoHome = jest.fn();
        render(<VictoryPopup outcome="win" onClose={() => {}} onGoHome={onGoHome} />);

        fireEvent.click(screen.getByText(/Retour à l'accueil/));

        expect(onGoHome).toHaveBeenCalledTimes(1);
    });

    test('hides the popup after "Voir la partie" is clicked', () => {
        render(<VictoryPopup outcome="win" onClose={() => {}} onGoHome={() => {}} />);
        expect(screen.getByText(/gagné/i)).toBeInTheDocument();

        fireEvent.click(screen.getByText('Voir la partie'));

        expect(screen.queryByText(/gagné/i)).not.toBeInTheDocument();
    });
});
