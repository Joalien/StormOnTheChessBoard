import {Image} from "react-native";

const IMAGES = {
    'ApartheidCard': require('../assets/images/cards/ApartheidCard.png'),
    'BarricadeCard': require('../assets/images/cards/BarricadeCard.png'),
    'BombardCard': require('../assets/images/cards/BombardCard.png'),
    'BombingCardCard': require('../assets/images/cards/BombingCard.png'),
    'MadHorseDiseaseCard': require('../assets/images/cards/MadHorseDiseaseCard.png'),
    'MadHouseCard': require('../assets/images/cards/MadHouseCard.png'),
    'MagnetismCard': require('../assets/images/cards/MagnetismCard.png'),
    'ManHoleCard': require('../assets/images/cards/ManHoleCard.png'),
    'MercyCard': require('../assets/images/cards/MercyCard.png'),
    'NeutralityCard': require('../assets/images/cards/NeutralityCard.png'),
    'NuclearBombCard': require('../assets/images/cards/NuclearBombCard.png'),
    'PegasusCard': require('../assets/images/cards/PegasusCard.png'),
    'SelfDefenseCard': require('../assets/images/cards/SelfDefenseCard.png'),
    'VampirismCard': require('../assets/images/cards/VampirismCard.png'),
    'ZombiesCard': require('../assets/images/cards/ZombiesCard.png'),
}

export function Card({hidden, name, showCard}) {

    const style = {
        width: 100,
        height: 150,
    };

    const back = <Image source={require('../assets/images/cards/back.png')} style={style}/>;
    const requireCard = require.context('../assets/images/cards', false, /\.png$/);
    const fileName = `./${name}.png`;
    const hasImage = requireCard.keys().includes(fileName);

    const image = hasImage ?
        <Image source={requireCard(fileName)} style={style} onClick={showCard}/> :
        null;

    const defaultImage = <div style={{...style, backgroundColor: "grey"}} onClick={showCard}>
        <h4 style={{wordWrap: "break-word"}}>{name}</h4>
    </div>;
    return (
        hidden ? back : (hasImage ? image : defaultImage)
    )
}