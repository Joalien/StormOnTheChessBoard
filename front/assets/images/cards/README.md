# Card Images

## Structure

```
cards/
  back.png                  # Card back (shown for opponent's hand)
  implemented/              # Cards with backend logic in domain/src/main/java/fr/kubys/card/
  not-yet-implemented/      # Card art ready, backend not yet coded
```

## `implemented/` (14 cards)

These cards have a matching `*Card.java` class in the backend. The frontend loads images from this folder via `require.context`.

| Image | Backend class |
|-------|--------------|
| ApartheidCard.png | ApartheidCard |
| BlackHoleCard.png | BlackHoleCard |
| BombingCard.png | BombingCard |
| CavalcadeCard.png | CavalcadeCard |
| ChargeCard.png | ChargeCard |
| CourtlyLoveCard.png | CourtlyLoveCard |
| HomeCard.png | HomeCard |
| KangarooCard.png | KangarooCard |
| LightweightSquadCard.png | LightweightSquadCard |
| MagnetismCard.png | MagnetismCard |
| ManHoleCard.png | ManHoleCard |
| QuadrilleCard.png | QuadrilleCard |
| ReflectedBishopCard.png | ReflectedBishopCard |
| StableCard.png | StableCard |

## `not-yet-implemented/` (11 cards)

Card art is available but the backend logic has not been written yet. When implementing a new card, move its image from here to `implemented/`.

| Image | Future card |
|-------|------------|
| BarricadeCard.png | Barricade |
| BombardCard.png | Bombard |
| MadHorseDiseaseCard.png | Mad Horse Disease |
| MadHouseCard.png | Mad House |
| MercyCard.png | Mercy |
| NeutralityCard.png | Neutrality |
| NuclearBombCard.png | Nuclear Bomb |
| PegasusCard.png | Pegasus |
| SelfDefenseCard.png | Self Defense |
| VampirismCard.png | Vampirism |
| ZombiesCard.png | Zombies |

## Naming convention

Image files must be named `{CardClassName}.png` (PascalCase, matching the Java class name exactly) to be picked up by the frontend's `require.context`.
