let stylesInjected = false;

function injectStyles() {
    if (stylesInjected) return;
    stylesInjected = true;

    const style = document.createElement('style');
    style.textContent = `
        @keyframes hideoutFade {
            0%,100%{opacity:0.7}50%{opacity:0.4}
        }
    `;
    document.head.appendChild(style);
}

export const HideoutEffect = {
    applyStyle: () => {
        injectStyles();
        return {
            backgroundImage: `url(${require('../../assets/images/effects/HideoutEffect.svg')})`,
            backgroundSize: 'cover',
            animation: 'hideoutFade 3s ease-in-out infinite'
        };
    }
};
