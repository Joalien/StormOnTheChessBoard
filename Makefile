.env:
	printf "UID=%s\nGID=%s\n" $$(id -u) $$(id -g) > .env

notifier:
	@if ! lsof -i :8787 -sTCP:LISTEN >/dev/null 2>&1; then \
		echo "Starting notifier on :8787..."; \
		python3 notifier/server.py & \
	else \
		echo "Notifier already running on :8787"; \
	fi

up: .env
	docker compose up -d --force-recreate sotc-backend sotc-frontend

claude: up notifier
	docker compose run --rm sotc-claude

claude-only: .env notifier
	docker compose run --rm sotc-claude

down:
	docker compose down
	@-pkill -f "python3 notifier/server.py" 2>/dev/null && echo "Notifier stopped" || true

.PHONY: up claude claude-only down notifier
