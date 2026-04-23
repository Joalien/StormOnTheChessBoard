let stylesInjected = false;

function injectStyles() {
    if (stylesInjected) return;
    stylesInjected = true;

    const style = document.createElement('style');
    style.textContent = `
        @keyframes funnelWobble {
            0%,100%{transform:rotate(-4deg)}50%{transform:rotate(4deg)}
        }
    `;
    document.head.appendChild(style);
}

export const MaddeningSquareEffect = {
    applyStyle: (pos, hasPiece) => {
        injectStyles();
        if (hasPiece) {
            return {
                backgroundImage: `url(${require('../../assets/images/effects/MaddeningSquareEffect.svg')})`,
                backgroundSize: 'cover',
                animation: 'funnelWobble 1.8s ease-in-out infinite',
                zIndex: 3,
            };
        }
        return {
            backgroundImage: `url(${require('../../assets/images/effects/MaddeningSquareHut.svg')})`,
            backgroundSize: 'cover',
        };
    }
};
