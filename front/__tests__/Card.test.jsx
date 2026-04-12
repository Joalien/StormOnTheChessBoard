import React from 'react';
import {render, screen, fireEvent} from '@testing-library/react';
import {Card} from '../component/Card';

describe('Card', () => {
    test('renders card name when no image available', () => {
        render(<Card name="UnknownCard" showCard={() => {}} />);
        expect(screen.getByText('UnknownCard')).toBeInTheDocument();
    });

    test('calls showCard when clicked', () => {
        const showCard = jest.fn();
        render(<Card name="TestCard" showCard={showCard} />);
        fireEvent.click(screen.getByText('TestCard'));
        expect(showCard).toHaveBeenCalledTimes(1);
    });

    test('does not call showCard when disabled', () => {
        const showCard = jest.fn();
        render(<Card name="TestCard" showCard={showCard} isPlayable={false} />);
        fireEvent.click(screen.getByText('TestCard'));
        // Card is still clickable (to show details) but has disabled styling
        expect(showCard).toHaveBeenCalledTimes(1);
    });

    test('applies selected class when isSelected', () => {
        const {container} = render(<Card name="TestCard" showCard={() => {}} isSelected={true} />);
        expect(container.querySelector('.sotc-card-wrapper.selected')).toBeInTheDocument();
    });

    test('applies disabled class when not playable', () => {
        const {container} = render(<Card name="TestCard" showCard={() => {}} isPlayable={false} />);
        expect(container.querySelector('.sotc-card-wrapper.disabled')).toBeInTheDocument();
    });

    test('renders larger card when large prop is set', () => {
        const {container} = render(<Card name="TestCard" showCard={() => {}} large={true} />);
        const wrapper = container.querySelector('.sotc-card-wrapper');
        expect(wrapper.style.width).toBe('160px');
        expect(wrapper.style.height).toBe('232px');
    });

    test('renders default size card without large prop', () => {
        const {container} = render(<Card name="TestCard" showCard={() => {}} />);
        const wrapper = container.querySelector('.sotc-card-wrapper');
        expect(wrapper.style.width).toBe('100px');
        expect(wrapper.style.height).toBe('145px');
    });

    test('renders hidden card back when hidden', () => {
        const {container} = render(<Card hidden={true} name="TestCard" showCard={() => {}} />);
        expect(container.querySelector('.sotc-card-hidden')).toBeInTheDocument();
        expect(container.querySelector('.sotc-card-wrapper')).not.toBeInTheDocument();
    });
});
