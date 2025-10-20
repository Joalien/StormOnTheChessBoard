import {Image} from "react-native";

const IMAGES = {
    'ApartheidCard': require('../assets/images/cards/Apartheid.png'),
    'BarricadeCard': require('../assets/images/cards/Barricade.png'),
    'BombardCard': require('../assets/images/cards/Bombard.png'),
    'BombingCardCard': require('../assets/images/cards/Bombing.png'),
    'MadHorseDiseaseCard': require('../assets/images/cards/MadHorseDisease.png'),
    'MadHouseCard': require('../assets/images/cards/MadHouse.png'),
    'MagnetismCard': require('../assets/images/cards/Magnetism.png'),
    'ManHoleCard': require('../assets/images/cards/ManHole.png'),
    'MercyCard': require('../assets/images/cards/Mercy.png'),
    'NeutralityCard': require('../assets/images/cards/Neutrality.png'),
    'NuclearBombCard': require('../assets/images/cards/NuclearBomb.png'),
    'PegasusCard': require('../assets/images/cards/Pegasus.png'),
    'SelfDefenseCard': require('../assets/images/cards/SelfDefense.png'),
    'VampirismCard': require('../assets/images/cards/Vampirism.png'),
    'ZombiesCard': require('../assets/images/cards/Zombies.png'),
}

export function Card({hidden, name, showCard}) {

    const style = {
        width: 100,
        height: 150,
    };

    const back = <Image source={require('../assets/images/cards/back.png')} style={style}/>;
    const image = <Image source={IMAGES[name]} style={style} onClick={showCard}/>;

    const defaultImage = <div style={{...style, backgroundColor: "grey"}} onClick={showCard}>
        <h4 style={{wordWrap: "break-word"}}>{name}</h4>
    </div>;
    return (
        hidden ? back : IMAGES[name] ? image : defaultImage
    )
}