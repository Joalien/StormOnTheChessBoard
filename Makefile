.env:
	printf "UID=%s\nGID=%s\n" $$(id -u) $$(id -g) > .env

up: .env
	docker compose up -d --force-recreate sotc-backend sotc-frontend

claude: up
	docker compose run --rm sotc-claude

claude-only: .env
	docker compose run --rm sotc-claude

down:
	docker compose down

.PHONY: up claude claude-only down
