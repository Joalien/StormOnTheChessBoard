# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Storm On The Chess Board is a full-stack chess variant game where special cards modify standard chess rules. Java 20 backend with Spring Boot 3.1.0, React/Expo frontend.

## Build & Run Commands

### Backend (Maven)
```bash
mvn clean install                    # Full build
mvn test                             # Unit tests only
mvn verify                           # Unit + integration tests
mvn -pl controller spring-boot:run   # Run backend (port 9000)
```

### Running a Single Test
```bash
mvn -pl domain test -Dtest=KingTest              # Single test class
mvn -pl domain test -Dtest=KingTest#testMethod   # Single test method
```

Test naming: `*Test` = unit tests (no board setup), `*IT` = integration tests (full board, run with `mvn test`).

### Frontend (Expo/React)
```bash
cd front
npm install
npm start          # Expo dev server
npm run web        # Run in browser
```

### Frontend Tests (Jest + React Testing Library)
```bash
cd front && npx jest                    # Run all frontend tests
cd front && npx jest --watch            # Watch mode (re-run on change)
cd front && npx jest Card              # Run tests matching "Card"
cd front && npx jest useCardSelection  # Run tests matching "useCardSelection"
```

### All Tests
```bash
make test          # Backend + frontend tests
make test-back     # Backend only
make test-front    # Frontend only
```

## Architecture

### Module Structure
```
domain/       # Core game logic (no Spring dependencies)
repository/   # CQRS command pattern + in-memory storage
controller/   # REST API + Spring Boot application
front/        # React/Expo frontend
```

### Key Architectural Patterns

**CQRS / Event Sourcing (Repository Layer)**
- Commands stored in `repository/src/main/java/fr/kubys/command/`
- All game actions are commands: `PlayMoveCommand`, `PlayCardWithImmutableParamCommand`, `EndTurnCommand`
- The store holds a `List<Command>` per game ID. **Every read and write replays all commands from scratch** (`computeChessBoard`). Undo simply removes the last command from the list before replaying.
- `CardParametersMapper` in `repository` maps JSON card parameter payloads to typed `CardParam` objects.

**State Machine (Game Flow)**
- Located in `domain/src/main/java/fr/kubys/game/`
- Each state implements `TurnState` sealed interface with `tryToMove()`, `tryToPlayCard()`, `tryToPass()`

```
                         ┌─────────────────────────────────────────────────────────┐
                         │             BEGINNING_OF_THE_TURN                       │
                         │  tryToMove  → MOVE_WITHOUT_CARD_PLAYED                  │
                         │  tryToPlay BEFORE_TURN card  → BEFORE_MOVE              │
                         │  tryToPlay REPLACE_TURN card → END_OF_THE_TURN          │
                         │  tryToPass → ✗ IllegalState                             │
                         └──────────────────┬──────────────────┬───────────────────┘
                                            │                  │
                              tryToMove     │                  │ tryToPlay
                                            ▼                  ▼
               ┌────────────────────────────────┐   ┌──────────────────────────────┐
               │    MOVE_WITHOUT_CARD_PLAYED    │   │      BEFORE_MOVE             │
               │  tryToMove  → ✗                │   │  tryToMove  → END_OF_THE_TURN│
               │  tryToPlay AFTER_TURN card     │   │  tryToPlay  → ✗              │
               │             → END_OF_THE_TURN  │   │  tryToPass  → ok             │
               │  tryToPass  → ok               │   └──────────────┬───────────────┘
               └───────────────┬────────────────┘                  │
                               │ tryToPlay / tryToPass             │ tryToMove
                               ▼                                   ▼
                         ┌─────────────────────────────────────────────────────────┐
                         │                  END_OF_THE_TURN                        │
                         │  tryToMove  → ✗ AlreadyMoved                            │
                         │  tryToPlay  → ✗ CardAlreadyPlayed                       │
                         │  tryToPass  → ok                                        │
                         └──────────────────────────┬──────────────────────────────┘
                                                    │ tryToPass
                                                    ▼
                                         swap player → BEGINNING_OF_THE_TURN

─────────────────────────────────────────────────────────────────────────────────────
  PROMOTION_PENDING  (intercepted transparently by transitionToState())
─────────────────────────────────────────────────────────────────────────────────────
  Any state that calls transitionToState(X) after a move/card triggers auto-promotion.
  If ChessBoard.drainPromotedPositions() is non-empty, the machine enters:

               ┌──────────────────────────────────────────────────────┐
               │                 PROMOTION_PENDING                    │
               │  promote(pos, piece) → resolves one promotion        │
               │    when last promotion resolved → returns to X       │
               │  tryToPass → clears all pending (Queens kept)        │
               │              then returns to X via tryToPass chain   │
               │  tryToMove  → ✗ IllegalState                        │
               │  tryToPlay  → ✗ IllegalState                        │
               └──────────────────────────────────────────────────────┘
```

**Domain Model**
- `GameStateController` orchestrates game, implements `ChessBoardService`
- `ChessBoard` manages 64 squares and piece placement
- Pieces in `domain/src/main/java/fr/kubys/piece/` with movement rules
- Cards in `domain/src/main/java/fr/kubys/card/` with parameter types

### Package Dependency Graph

```
GAME → API, PLAYER, BOARD, CARD
PLAYER → CARD
CARD → BOARD
BOARD → PIECE
PIECE → CORE
```

`domain` has no Spring dependencies; `repository` depends on `domain`; `controller` depends on `repository`.

### Card System
13 cards with typed parameters (`PositionCardParam`, `PieceCardParam`, `NoCardParam`, etc.). Each card subclass implements three template methods called in order by `Card.playOn()`:
1. `validInput(board, param)` — validate parameters, throw if invalid
2. `doesNotCreateCheck(board, param)` — return false to abort if card would put own king in check
3. `doAction(board, param)` — apply the card effect

`CardType` enum controls when a card is playable: `BEFORE_TURN` (before moving), `REPLACE_TURN` (replaces the move), `AFTER_TURN` (after moving), `ENEMY_TURN` (on opponent's turn).

### Effect System
Cards that have persistent board effects add an `Effect` subclass to `ChessBoard.effects`. Effects receive four hooks:
- `beforeMoveHook` / `afterMoveHook` — react to piece movement
- `afterRemovingPieceHook` — react to piece capture
- `allowToMove(piece, pos)` — grant extra movement rights (e.g. Kangaroo jump)

### Check Detection
`ChessBoard.doesMovingPieceCheckOurOwnKing()` uses `fakeSquare()`/`unfakeSquare()` to temporarily overlay the board state without physically moving pieces, then checks if the king is under attack. `FakePieceDecorator` wraps pieces in fake squares and is skipped by the check-detection recursion.

### Matchmaking
- Domain logic in `matchmaking/src/main/java/fr/kubys/matchmaking/` (pure Java, no Spring)
- `MatchmakingQueue` is a Spring bean, pairs players FIFO. `MatchResult` holds token-to-color mapping + lazy game ID.
- REST endpoints in `MatchmakingController`: `POST /matchmaking/join`, `GET /matchmaking/status/{token}`, `DELETE /matchmaking/{token}`
- `MatchNotifier` interface (in matchmaking module) implemented by `PresenceNotifier` (in controller module)
- Frontend: "Jouer !" → join → sends `{"matchmaking":"token"}` on presence WebSocket → match notification arrives on same WS → redirect to `/{gameId}?color=white|black`
- `myColor` URL param locks board orientation and restricts interactions to your turn only

### WebSocket (Real-time Updates)
- Config in `controller/src/main/java/fr/kubys/websocket/`
- `PresenceNotifier` handles `/ws/presence` — opened on page load by every client. Dual role:
  1. Tracks connected users count (exposed via `GET /api/chessboard/connected`)
  2. Handles matchmaking notifications: client sends `{"matchmaking":"token"}`, server sends back `{status, gameId, color}` when match found. On disconnect, cancels any pending matchmaking token.
- `GameNotifier` handles `/ws/game/{gameId}` — sends `"refresh"` to all connected clients after any game action (move, card, endTurn, undo, promote). Frontend re-fetches game state via HTTP on message.
- Nginx routes `/ws/` with `proxy_http_version 1.1` + `Upgrade`/`Connection` headers.

## REST API

Base: `http://localhost:9000/chessboard/`

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/` | Create new game |
| GET | `/{gameId}` | Get board state |
| POST | `/{gameId}/move/{from}/to/{to}` | Move piece |
| POST | `/{gameId}/card/{cardName}` | Play card |
| POST | `/{gameId}/endTurn` | End turn |
| POST | `/{gameId}/undo` | Undo last action |
| POST | `/matchmaking/join` | Join matchmaking queue |
| GET | `/matchmaking/status/{token}` | Poll match status (fallback) |
| DELETE | `/matchmaking/{token}` | Cancel matchmaking |
| GET | `/matchmaking/stats` | Queue size + active match count |

## WebSocket Endpoints

| Endpoint | Direction | Purpose |
|----------|-----------|---------|
| `/ws/presence` | Bidirectional | Presence tracking + matchmaking notifications |
| `/ws/game/{gameId}` | Server → Client | `"refresh"` after any game state change |

## Known Issues

From TODO.md:
- Black hole should prevent Kangaroo checks
- Can move piece into check if end-of-turn card would block it
- Quadrille can't rotate kings (tries to remove them from board)
