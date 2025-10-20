let stylesInjected = false;

function injectStyles() {
    if (stylesInjected) return;
    stylesInjected = true;

    const style = document.createElement('style');
    style.textContent = `
        @keyframes attractShadow {
            0% {
                background: radial-gradient(circle, 
                    transparent 45%, 
                    rgba(255, 255, 255, 0.8) 20%, 
                    transparent 55%);
                background-size: 200% 200%;
                background-position: center;
            }
            100% {
                background: radial-gradient(circle, 
                    transparent 45%, 
                    rgba(255, 255, 255, 0.8) 50%, 
                    transparent 55%);
                background-size: 0% 0%;
                background-position: center;
            }
        }
    `;
    document.head.appendChild(style);
}

export const MagnetismEffect = {
    applyStyle: () => {
        injectStyles(); // Injecte le CSS la première fois
        return {
            animation: 'attractShadow 1.5s ease-in infinite'
        };
    }
};