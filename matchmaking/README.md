# Matchmaking

Module de matchmaking pour Storm on the Chess Board. Gere la file d'attente des joueurs et la creation de parties multijoueur.

## Flux de creation d'une partie

```
    Joueur 1 (navigateur)                    Serveur                     Joueur 2 (navigateur)
    =====================                    =======                     =====================

    WebSocket /ws/presence ──────────────►  presenceNotifier (connexion)
    (ouvert au chargement de la page)

    POST /api/matchmaking/join ──────────►  queue.join()
                                            waitingToken = token1
    ◄─────────────────────────────────────  { status: "waiting",
                                              token: "token1" }

    WS send: {"matchmaking":"token1"} ──►  matchmakingSessions[token1] = ws

    GET /api/matchmaking/status/token1 ──►  isWaiting(token1) → true
    ◄─────────────────────────────────────  { status: "waiting" }
    (verification anti race condition)
                                                                         POST /api/matchmaking/join ──────►
                                                                         queue.join()
                                                                           match = (token1, token2)
                                                                           waitingToken = null
                                                                         ensureGameCreated()
                                            POST /api/chessboard ──►       → gameId = 42
                                            notifyMatch(token1, ...)
    ◄── WS: { status: "matched",           notifyMatch(token2, ...)
              gameId: 42,                                                ◄── HTTP: { status: "matched",
              color: "white" }                                                       gameId: 42,
                                                                                     color: "black" }
    notifyMatchFound()                                                   notifyMatchFound()
    navigateToGame(42, "white")                                          navigateToGame(42, "black")

    ══════════════════════════════════════════════════════════════════════════════════════
                                Les deux joueurs sont sur /chessboard/42
    ══════════════════════════════════════════════════════════════════════════════════════
```

### Cas particulier : match immediat

Si deux joueurs appellent `POST /join` quasi simultanement, le second recoit directement une reponse `Matched` via HTTP (pas besoin de WebSocket).

### Annulation

Un joueur est retire de la file dans 3 cas :
- **Bouton Annuler** : le front appelle `DELETE /api/matchmaking/{token}`
- **Fermeture d'onglet** : `beforeunload` envoie un `DELETE` avec `keepalive: true`
- **Deconnexion WebSocket** : `PresenceNotifier.afterConnectionClosed()` appelle `queue.cancel(token)` (le plus fiable)

## Architecture

- `MatchmakingQueue` (domain) — file d'attente pure Java, Spring bean
- `MatchmakingController` (matchmaking) — endpoints REST
- `MatchNotifier` (matchmaking) — interface pour notifier les joueurs
- `PresenceNotifier` (controller) — implemente `MatchNotifier`, gere le WebSocket `/ws/presence`

## API

| Methode | Endpoint                          | Description                            |
|---------|-----------------------------------|----------------------------------------|
| POST    | `/api/matchmaking/join`           | Rejoindre la file d'attente            |
| GET     | `/api/matchmaking/status/{token}` | Verifier le statut d'un token          |
| DELETE  | `/api/matchmaking/{token}`        | Annuler et quitter la file             |
| GET     | `/api/matchmaking/stats`          | Nombre de joueurs en file et de matchs |

## WebSocket

Les notifications de matchmaking passent par le WebSocket `/ws/presence` (unifie avec le tracking de presence).

| Direction        | Message                              | Description                          |
|------------------|--------------------------------------|--------------------------------------|
| Client → Serveur | `{"matchmaking":"token"}`            | Enregistrer un token pour les notifs |
| Serveur → Client | `{"status":"matched","gameId":N,...}` | Match trouve                         |
