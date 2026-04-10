package fr.kubys.card;

import fr.kubys.card.params.CardParam;

import java.util.List;

public class CardRegistry {

    private CardRegistry() {
    }

    public static List<Card<? extends CardParam>> createAllCards() {
        return List.of(
                new OhDarlingCard(),
                new PrivateJetCard(),
                new FrightCard(),
                new CannibalCard(),
                new ShotCard(),
                new RetaliationCard(),
                new BigBluesCard(),
                new StateVisitCard(),
                new BestFriendsCard(),
                new RaccoonCard(),
                new UrbanPlanningCard(),
                new SwapYourKnightsCard(),
                new SchizophreniaCard(),
                new CrazyTowerCard(),
                new CrazyHorseCard(),
                new CrazyKnightCard(),
                new BoxCard(),
                new AsylumCard(),
                new DisintegrationCard(),
                new CrabCard(),
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
