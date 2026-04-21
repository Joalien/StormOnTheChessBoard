test:
	mvn test
	cd front && npx jest

test-back:
	mvn test

test-front:
	cd front && npx jest

.env:
	printf "UID=%s\nGID=%s\n" $$(id -u) $$(id -g) > .env

notifier:
	@if ! lsof -i :8787 -sTCP:LISTEN >/dev/null 2>&1; then \
		echo "Starting notifier on :8787..."; \
		python3 notifier/server.py >/dev/null 2>&1 & \
	else \
		echo "Notifier already running on :8787"; \
	fi

back:
	@-pkill -f "spring-boot:run" 2>/dev/null
	@mvn -N install -q && mvn -pl domain,repository,matchmaking,ai install -DskipTests -q && mvn -pl controller spring-boot:run &
	@echo "Backend starting in background (port 9000)"

up: .env back
	docker compose -f ops/docker-compose.yml up -d --force-recreate sotc-frontend sotc-nginx

claude: up notifier
	docker compose -f ops/docker-compose.yml run --rm sotc-claude

claude-only: .env notifier
	docker compose -f ops/docker-compose.yml run --rm sotc-claude

down:
	docker compose -f ops/docker-compose.yml down
	@-pkill -f "python3 notifier/server.py" 2>/dev/null && echo "Notifier stopped" || true

.PHONY: test test-back test-front up back claude claude-only down notifier
