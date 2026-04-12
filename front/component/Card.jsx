import {Image} from "react-native";
import cardImages from "./cardImages";

export function Card({hidden, name, showCard, isSelected, isPlayable = true, large = false}) {
    const hasImage = name in cardImages;
    const w = large ? 160 : 100;
    const h = large ? 232 : 145;
    const imgStyle = {width: w, height: h, borderRadius: 8};

    if (hidden) {
        return (
            <div className="sotc-card-hidden">
                <Image source={require('../assets/images/cards/back.png')} style={imgStyle}/>
            </div>
        );
    }

    const wrapperClass = `sotc-card-wrapper${isSelected ? ' selected' : ''}${!isPlayable ? ' disabled' : ''}`;

    if (hasImage) {
        return (
            <div className={wrapperClass} onClick={showCard}>
                <Image source={cardImages[name]} style={imgStyle}/>
            </div>
        );
    }

    return (
        <div className={wrapperClass} onClick={showCard} style={{
            width: w,
            height: h,
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
