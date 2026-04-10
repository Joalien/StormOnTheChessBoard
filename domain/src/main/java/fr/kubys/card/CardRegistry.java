package fr.kubys.card;

import fr.kubys.card.params.CardParam;

import java.util.List;

public class CardRegistry {

    private CardRegistry() {
    }

    public static List<Card<? extends CardParam>> createAllCards() {
        return List.of(
                new BarricadeCard(),
                new MercyCard(),
                new SelfDefenseCard(),
                new VampirismCard(),
                new ManHoleCard(),
                new BlackHoleCard(),
                new BombingCard(),
                new ChargeCard(),
                new ApartheidCard(),
                new CourtlyLoveCard(),
                new KangarooCard(),
                new HomeCard(),
                new LightweightSquadCard(),
                new MagnetismCard(),
                new QuadrilleCard(),
                new ReflectedBishopCard(),
                new StableCard(),
                new CavalcadeCard(),
                new BombardCard(),
                new MadHorseDiseaseCard(),
                new NeutralityCard(),
                new MadHouseCard(),
                new NuclearBombCard(),
                new PegasusCard()
        );
    }
}
