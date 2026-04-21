# Architecture - Storm On The Chess Board

## Vue d'ensemble

L'application tourne sur un Raspberry Pi 5, avec deux environnements (prod et staging) sur la meme machine, derriere une gateway nginx commune.

## Schema d'architecture

```
                         Internet
                            |
                         :80 (HTTP)
                            |
                   ┌────────────────────┐
                   │      Gateway       │
                   │   (nginx:alpine)   │
                   │                    │
                   │  kubys.fr ─────────┼──► localhost:8080
                   │  staging.kubys.fr ─┼──► localhost:8081
                   └────────────────────┘
                        |             |
            ┌───────────┘             └───────────┐
            ▼                                     ▼
   ┌─────────────────────┐              ┌─────────────────────┐
   │   Prod (:8080)      │              │  Staging (:8081)    │
   │   COMPOSE=sotc      │              │  COMPOSE=sotc-stg   │
   │   branch: master    │              │  branch: staging    │
   │                     │              │                     │
   │  ┌───────────────┐  │              │  ┌───────────────┐  │
   │  │    nginx       │  │              │  │    nginx       │  │
   │  │  :8080 interne │  │              │  │  :8080 interne │  │
   │  │                │  │              │  │                │  │
   │  │  /api/  ──►────┼──┼──┐          │  │  /api/  ──►────┼──┼──┐
   │  │  /ws/   ──►────┼──┼──┤          │  │  /ws/   ──►────┼──┼──┤
   │  │  /*     ──► SPA│  │  │          │  │  /*     ──► SPA│  │  │
   │  └───────────────┘  │  │          │  └───────────────┘  │  │
   │                     │  │          │                     │  │
   │  ┌───────────────┐  │  │          │  ┌───────────────┐  │  │
   │  │   backend     │◄─┼──┘          │  │   backend     │◄─┼──┘
   │  │  Java 21      │  │              │  │  Java 21      │  │
   │  │  Spring Boot  │  │              │  │  Spring Boot  │  │
   │  │  :9000        │  │              │  │  :9000        │  │
   │  └───────────────┘  │              │  └───────────────┘  │
   └─────────────────────┘              └─────────────────────┘
```

## Composants

### Gateway (`ops/gateway/`)

- **Image** : `nginx:alpine`
- **Port** : 80 (seul point d'entree HTTP)
- **Role** : reverse proxy basé sur le `server_name`
- **Config** : `ops/gateway/nginx.conf`

### Environnement applicatif (`ops/docker-compose-prod.yml`)

Chaque environnement (prod et staging) lance le meme `ops/docker-compose-prod.yml` avec des variables differentes :

| | Prod | Staging |
|---|---|---|
| **COMPOSE_PROJECT_NAME** | `sotc` | `sotc-staging` |
| **SOTC_PORT** | 8080 | 8081 |
| **Branche git** | `master` | `staging` |
| **Repertoire** | `/home/sotc/app` | `/home/sotc/app-staging` |
| **Domaine** | `kubys.fr` | `staging.kubys.fr` |

#### Nginx (frontend)

- Build multi-stage : `ops/Dockerfile.frontend`
- Compile le frontend React/Expo en SPA statique
- Sert les fichiers statiques + proxy vers le backend
- Port interne : 8080

#### Backend

- Build multi-stage : `ops/Dockerfile.backend`
- Java 21 + Spring Boot
- Port interne : 9000
- API REST + WebSocket

## Deploiement

Depuis la machine locale :

```bash
cd ops/prod && make deploy      # Deploie master sur kubys.fr
cd ops/staging && make deploy   # Deploie staging sur staging.kubys.fr
```

Le `make deploy` :
1. SSH vers `sotc@kubys.fr`
2. `git fetch` + `git reset --hard` sur la branche cible
3. `docker compose down` + `up --build -d`

## Fichiers

```
ops/
├── ARCHITECTURE.md          # Ce fichier
├── Dockerfile.backend       # Build multi-stage backend Java
├── Dockerfile.frontend      # Build multi-stage frontend + nginx
├── Dockerfile.claude        # Image Claude Code
├── docker-compose.yml       # Dev local (backend hot-reload)
├── docker-compose-prod.yml  # Prod/staging (images buildées)
├── nginx/
│   ├── dev.conf             # Config nginx dev (proxy vers services locaux)
│   └── prod.conf            # Config nginx prod (SPA + proxy backend)
├── gateway/
│   ├── docker-compose.yml   # Gateway nginx (port 80)
│   ├── Makefile             # make up/down/logs
│   └── nginx.conf           # Routage par domaine
├── prod/
│   ├── Makefile             # make deploy/logs/status/stop
│   └── setup.sh             # Setup initial du Pi
└── staging/
    ├── Makefile             # make deploy/logs/status/stop
    └── setup.sh             # Setup initial staging
```
