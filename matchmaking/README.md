# Matchmaking

Module de matchmaking pour Storm on the Chess Board. Gère la file d'attente des joueurs et la création de parties multijoueur.

## Flux de création d'une partie

```
    Joueur 1 (navigateur)                    Serveur                     Joueur 2 (navigateur)
    =====================                    =======                     =====================

    POST /api/matchmaking/join ──────────►  queue.join()
                                            waitingToken = token1
    ◄─────────────────────────────────────  { status: "waiting",
                                              token: "token1" }

    WebSocket /ws/matchmaking/token1 ────►  sessions[token1] = ws1
    (connexion ouverte)

    GET /api/matchmaking/status/token1 ──►  isWaiting(token1) → true
    ◄─────────────────────────────────────  { status: "waiting" }
    (vérification anti race condition)
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

### Cas particulier : match immédiat

Si deux joueurs appellent `POST /join` quasi simultanément, le second reçoit directement une réponse `Matched` via HTTP (pas besoin de WebSocket).

### Annulation

Un joueur est retiré de la file dans 3 cas :
- **Bouton Annuler** : le front appelle `DELETE /api/matchmaking/{token}`
- **Fermeture d'onglet** : `beforeunload` envoie un `DELETE` avec `keepalive: true`
- **Déconnexion WebSocket** : `MatchmakingNotifier.afterConnectionClosed()` appelle `queue.cancel(token)` (le plus fiable)

## API

| Methode | Endpoint                        | Description                              |
|---------|---------------------------------|------------------------------------------|
| POST    | `/api/matchmaking/join`         | Rejoindre la file d'attente              |
| GET     | `/api/matchmaking/status/{token}` | Vérifier le statut d'un token          |
| DELETE  | `/api/matchmaking/{token}`      | Annuler et quitter la file               |
| GET     | `/api/matchmaking/stats`        | Nombre de joueurs en file et de matchs   |

## WebSocket

| Endpoint                        | Direction        | Description                          |
|---------------------------------|------------------|--------------------------------------|
| `/ws/matchmaking/{token}`       | Serveur → Client | Notification JSON quand match trouvé |
