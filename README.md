# StormOnTheChessBoard

## Controller

Nothing to report, simple SpringBoot REST controller.

## Repository

For now, it is a simple in memory fake database.  
It uses the Command Query Responsibility Segregation aka. CQRS principle.
It is deliberately over-engineering and not opportune, but it provides an easy way to undo moves.

## Model

Dependency graph of package

```mermaid
graph TD

GAME-->API
GAME-->PLAYER
GAME-->BOARD
GAME-->CARD
PLAYER-->CARD
CARD-->BOARD
BOARD-->PIECE
PIECE-->CORE 
```

## State Machine

```mermaid
stateDiagram-v2
    [*] --> BEGINNING_OF_THE_TURN: Début du tour

    BEGINNING_OF_THE_TURN --> BEFORE_MOVE: Jouer carte BEFORE_TURN
    BEGINNING_OF_THE_TURN --> END_OF_THE_TURN: Jouer carte REPLACE_TURN
    BEGINNING_OF_THE_TURN --> MOVE_WITHOUT_CARD_PLAYED: Déplacer une pièce

    BEFORE_MOVE --> END_OF_THE_TURN: Déplacer une pièce
    BEFORE_MOVE --> PROMOTION_PENDING: (si promotion détectée)

    MOVE_WITHOUT_CARD_PLAYED --> END_OF_THE_TURN: Jouer carte AFTER_TURN
    MOVE_WITHOUT_CARD_PLAYED --> END_OF_THE_TURN: Passer (pass)
    MOVE_WITHOUT_CARD_PLAYED --> PROMOTION_PENDING: (si promotion détectée)

    END_OF_THE_TURN --> [*]: Fin du tour (pass)

    PROMOTION_PENDING --> PROMOTION_PENDING: promote(pos, pièce)\n[promotions restantes]
    PROMOTION_PENDING --> BEFORE_MOVE: promote(pos, pièce)\n[dernière promotion depuis BEFORE_MOVE]
    PROMOTION_PENDING --> END_OF_THE_TURN: promote(pos, pièce)\n[dernière promotion depuis END_OF_THE_TURN]
    PROMOTION_PENDING --> MOVE_WITHOUT_CARD_PLAYED: promote(pos, pièce)\n[dernière promotion depuis MOVE_WITHOUT_CARD_PLAYED]
    PROMOTION_PENDING --> END_OF_THE_TURN: Passer (valide la Dame par défaut)
```

Voir [TurnStateMachine.md](domain/src/main/java/fr/kubys/game/TurnStateMachine.md) pour la description détaillée des états.