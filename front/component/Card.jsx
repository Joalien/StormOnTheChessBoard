import {Image} from "react-native";

const IMAGES = {
    'ApartheidCard': require('../assets/images/Apartheid.png'),
    'BarricadeCard': require('../assets/images/Barricade.png'),
    'BombardCard': require('../assets/images/Bombard.png'),
    'BombingCardCard': require('../assets/images/Bombing.png'),
    'MadHorseDiseaseCard': require('../assets/images/MadHorseDisease.png'),
    'MadHouseCard': require('../assets/images/MadHouse.png'),
    'MagnetismCard': require('../assets/images/Magnetism.png'),
    'ManHoleCard': require('../assets/images/ManHole.png'),
    'MercyCard': require('../assets/images/Mercy.png'),
    'NeutralityCard': require('../assets/images/Neutrality.png'),
    'NuclearBombCard': require('../assets/images/NuclearBomb.png'),
    'PegasusCard': require('../assets/images/Pegasus.png'),
    'SelfDefenseCard': require('../assets/images/SelfDefense.png'),
    'VampirismCard': require('../assets/images/Vampirism.png'),
    'ZombiesCard': require('../assets/images/Zombies.png'),
}

export function Card({hidden, name, showCard}) {

    const style = {
        width: 100,
        height: 150,
    };

    const back = <Image source={require('../assets/images/back.png')} style={style}/>;
    const image = <Image source={IMAGES[name]} style={style} onClick={showCard}/>;

    const defaultImage = <div style={{...style, backgroundColor: "grey"}} onClick={showCard}>
        <h4 style={{wordWrap: "break-word"}}>{name}</h4>
    </div>;
    return (
        hidden ? back : IMAGES[name] ? image : defaultImage
    )
}