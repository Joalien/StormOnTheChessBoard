# Machine à États des Tours

Ce document décrit le fonctionnement de la machine à états qui gère le déroulement d'un tour de jeu dans Storm On The Chess Board.

## Diagramme des États

```mermaid
stateDiagram-v2
    [*] --> BEGINNING_OF_THE_TURN: Début du tour

    BEGINNING_OF_THE_TURN --> ENEMY_REACTION: Déplacer une pièce
    BEGINNING_OF_THE_TURN --> ENEMY_REACTION: Jouer carte BEFORE_TURN
    BEGINNING_OF_THE_TURN --> ENEMY_REACTION: Jouer carte REPLACE_TURN

    ENEMY_REACTION --> MOVE_WITHOUT_CARD_PLAYED: Passer / Jouer carte ENEMY_TURN\n(retour après mouvement)
    ENEMY_REACTION --> BEFORE_MOVE: Passer / Jouer carte ENEMY_TURN\n(retour après carte BEFORE_TURN)
    ENEMY_REACTION --> END_OF_THE_TURN: Passer / Jouer carte ENEMY_TURN\n(retour après carte REPLACE_TURN)

    BEFORE_MOVE --> ENEMY_REACTION: Déplacer une pièce

    MOVE_WITHOUT_CARD_PLAYED --> ENEMY_REACTION: Jouer carte AFTER_TURN
    MOVE_WITHOUT_CARD_PLAYED --> END_OF_THE_TURN: Passer (pass)

    END_OF_THE_TURN --> [*]: Fin du tour (pass)

    PROMOTION_PENDING --> PROMOTION_PENDING: promote(pos, pièce)\n[promotions restantes]
    PROMOTION_PENDING --> ENEMY_REACTION: promote(pos, pièce)\n[dernière promotion]
    PROMOTION_PENDING --> END_OF_THE_TURN: Passer (valide la Dame par défaut)

    note right of ENEMY_REACTION
        État intercalé par transitionToState().
        Le joueur adverse est temporairement swappé.
        Il peut jouer une carte ENEMY_TURN ou passer.
        Max 1 carte ENEMY_TURN par tour.
        Skippé si déjà jouée ce tour.
    end note
```

## Description des États

### 1. BEGINNING_OF_THE_TURN (Début de tour)
État initial de chaque tour. Le joueur a trois options :
- **Jouer une carte BEFORE_TURN** → Transition vers `BEFORE_MOVE`
- **Jouer une carte REPLACE_TURN** → Transition directe vers `END_OF_THE_TURN` (le tour se termine)
- **Déplacer une pièce** → Transition vers `MOVE_WITHOUT_CARD_PLAYED`

❌ Impossible de passer son tour dans cet état.

### 2. BEFORE_MOVE (Carte avant mouvement jouée)
Le joueur a déjà joué une carte BEFORE_TURN. Il doit maintenant :
- **Déplacer une pièce** → Transition vers `END_OF_THE_TURN`

❌ Impossible de jouer une autre carte (exception `CardAlreadyPlayedException`)
❌ Impossible de passer son tour

### 3. MOVE_WITHOUT_CARD_PLAYED (Mouvement sans carte)
Le joueur a déplacé une pièce sans avoir joué de carte avant. Il peut :
- **Jouer une carte AFTER_TURN** → Transition vers `END_OF_THE_TURN`
- **Passer** → Transition vers `END_OF_THE_TURN`

❌ Impossible de déplacer une autre pièce (exception `AlreadyMovedException`)
❌ Impossible de jouer une carte qui n'est pas AFTER_TURN

### 4. END_OF_THE_TURN (Fin de tour)
État terminal du tour. Toutes les actions sont verrouillées :

❌ Impossible de déplacer une pièce (exception `AlreadyMovedException`)
❌ Impossible de jouer une carte (exception `CardAlreadyPlayedException`)
✅ Passer est autorisé → échange de joueur, retour à `BEGINNING_OF_THE_TURN`

### 5. ENEMY_REACTION (Réaction adverse)
État intercalé automatiquement par `transitionToState()` après chaque coup ou carte joué(e), si aucune carte `ENEMY_TURN` n'a encore été jouée ce tour. Le joueur courant est temporairement swappé vers l'adversaire.

- **Jouer une carte ENEMY_TURN** → joue la carte, swap back, retourne à l'état cible mémorisé
- **Passer** → swap back, retourne à l'état cible mémorisé

❌ Impossible de déplacer une pièce
❌ Impossible de jouer une carte qui n'est pas `ENEMY_TURN`

> **Note :** maximum une carte `ENEMY_TURN` peut être jouée par tour. Si déjà jouée, l'état est skippé automatiquement.

### 6. PROMOTION_PENDING (Promotion en attente)
État intercalé automatiquement par `transitionToState()` lorsqu'un déplacement ou une carte a promu un ou plusieurs pions en Dame. Le pion est **déjà remplacé par une Dame** sur le plateau ; le joueur peut choisir de la remplacer par une autre pièce.

- **`promote(position, pièce)`** → remplace la Dame à cette position par la pièce choisie. Si c'est la dernière promotion en attente, retourne à l'état cible mémorisé (`BEFORE_MOVE`, `MOVE_WITHOUT_CARD_PLAYED` ou `END_OF_THE_TURN`).
- **Passer** → valide toutes les Dames par défaut et retourne à l'état cible mémorisé.

❌ Impossible de déplacer une pièce
❌ Impossible de jouer une carte

> **Note :** une carte jouée par le joueur actif peut provoquer la promotion d'un pion adverse (ex. Quadrille). Dans ce cas, c'est toujours le joueur actif qui choisit la pièce de promotion.

## Types de Cartes

| Type | Description | Quand jouer |
|------|-------------|-------------|
| **BEFORE_TURN** | Carte jouée avant le déplacement | État `BEGINNING_OF_THE_TURN` |
| **REPLACE_TURN** | Remplace complètement le tour (pas de déplacement) | État `BEGINNING_OF_THE_TURN` |
| **AFTER_TURN** | Carte jouée après le déplacement | État `MOVE_WITHOUT_CARD_PLAYED` |
| **ENEMY_TURN** | Carte jouée par l'adversaire en réaction à un coup ou une carte | État `ENEMY_REACTION` (max 1 par tour) |

## Implémentation

- **Interface** : `TurnState` (sealed interface)
- **Implémentations** :
  - `BeginningOfTheTurnState` (domain/src/main/java/fr/kubys/game/BeginningOfTheTurnState.java:7)
  - `BeforeMoveCardPlayedState` (domain/src/main/java/fr/kubys/game/BeforeMoveCardPlayedState.java:8)
  - `MoveWithoutCardPlayedState` (domain/src/main/java/fr/kubys/game/MoveWithoutCardPlayedState.java:9)
  - `EndOfTheTurnState` (domain/src/main/java/fr/kubys/game/EndOfTheTurnState.java:9)
  - `EnemyReactionState` (domain/src/main/java/fr/kubys/game/EnemyReactionState.java:8)
  - `PromotionPendingState` (domain/src/main/java/fr/kubys/game/PromotionPendingState.java:7)
- **Enum** : `StateEnum` (domain/src/main/java/fr/kubys/game/StateEnum.java:3)
- **Contrôleur** : `GameStateController` — `transitionToState()` intercepte les promotions de façon transparente
