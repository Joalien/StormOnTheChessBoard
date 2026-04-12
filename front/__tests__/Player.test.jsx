import React from 'react';
import {render, screen, fireEvent} from '@testing-library/react';
import {Player} from '../component/Player';

describe('Player', () => {
    const cards = [
        {englishName: 'Card1', type: 'BEFORE_TURN'},
        {englishName: 'Card2', type: 'AFTER_TURN'},
        {englishName: 'Card3', type: 'REPLACE_TURN'},
    ];

    test('renders all cards', () => {
        render(<Player player={{cards}} showCard={() => {}} color="white" />);
        expect(screen.getByText('Card1')).toBeInTheDocument();
        expect(screen.getByText('Card2')).toBeInTheDocument();
        expect(screen.getByText('Card3')).toBeInTheDocument();
    });

    test('shows card count', () => {
        render(<Player player={{cards}} showCard={() => {}} color="white" hiddenCards={false} />);
        expect(screen.getByText('3 cartes')).toBeInTheDocument();
    });

    test('shows singular when 1 card', () => {
        render(<Player player={{cards: [cards[0]]}} showCard={() => {}} color="white" hiddenCards={false} />);
        expect(screen.getByText('1 carte')).toBeInTheDocument();
    });

    test('shows "Aucune carte" when empty', () => {
        render(<Player player={{cards: []}} showCard={() => {}} color="white" />);
        expect(screen.getByText('Aucune carte')).toBeInTheDocument();
    });

    test('calls showCard when a card is clicked', () => {
        const showCard = jest.fn();
        render(<Player player={{cards}} showCard={showCard} color="white" />);
        fireEvent.click(screen.getByText('Card1'));
        expect(showCard).toHaveBeenCalledWith(cards[0]);
    });

    test('marks selected card', () => {
        const {container} = render(
            <Player player={{cards}} showCard={() => {}} color="white"
                    selectedCard={{englishName: 'Card2'}} hiddenCards={false} />
        );
        const selected = container.querySelectorAll('.sotc-card-wrapper.selected');
        expect(selected).toHaveLength(1);
    });

    test('marks unplayable cards as disabled', () => {
        const {container} = render(
            <Player player={{cards}} showCard={() => {}} color="white"
                    playableTypes={['BEFORE_TURN']} />
        );
        const disabled = container.querySelectorAll('.sotc-card-wrapper.disabled');
        expect(disabled).toHaveLength(2); // Card2 (AFTER_TURN) and Card3 (REPLACE_TURN)
    });

    test('displays player label Blancs for white', () => {
        render(<Player player={{cards: []}} showCard={() => {}} color="white" />);
        expect(screen.getByText('Blancs')).toBeInTheDocument();
    });

    test('displays player label Noirs for black', () => {
        render(<Player player={{cards: []}} showCard={() => {}} color="black" />);
        expect(screen.getByText('Noirs')).toBeInTheDocument();
    });
});
