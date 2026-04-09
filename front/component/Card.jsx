import {Image} from "react-native";

const requireCard = require.context('../assets/images/cards', false, /\.png$/);

export function Card({hidden, name, showCard, isSelected}) {
    const fileName = `./${name}.png`;
    const hasImage = requireCard.keys().includes(fileName);
    const imgStyle = {width: 80, height: 116, borderRadius: 7};

    if (hidden) {
        return (
            <div className="sotc-card-hidden">
                <Image source={require('../assets/images/cards/back.png')} style={imgStyle}/>
            </div>
        );
    }

    const wrapperClass = `sotc-card-wrapper${isSelected ? ' selected' : ''}`;

    if (hasImage) {
        return (
            <div className={wrapperClass} onClick={showCard}>
                <Image source={requireCard(fileName)} style={imgStyle}/>
            </div>
        );
    }

    return (
        <div className={wrapperClass} onClick={showCard} style={{
            width: 80,
            height: 116,
            background: 'rgba(255,255,255,0.04)',
            border: '1px solid rgba(255,255,255,0.1)',
            borderRadius: 7,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: 8,
        }}>
            <span style={{fontSize: 10, color: '#8b949e', textAlign: 'center', wordBreak: 'break-word', fontWeight: 500, lineHeight: 1.4}}>
                {name}
            </span>
        </div>
    );
}
