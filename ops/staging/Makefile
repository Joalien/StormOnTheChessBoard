REMOTE_HOST   = kubys.fr
REMOTE_USER   = sotc
REMOTE_DIR    = /home/sotc/app-staging
COMPOSE       = COMPOSE_PROJECT_NAME=sotc-staging SOTC_PORT=8081 docker compose -f ops/docker-compose-prod.yml
SETUP_USER   ?= $(shell whoami)

setup:
	ssh $(SETUP_USER)@$(REMOTE_HOST) 'bash -s' < setup.sh

deploy:
	ssh $(REMOTE_USER)@$(REMOTE_HOST) "\
		cd $(REMOTE_DIR) \
		&& git fetch origin staging \
		&& git reset --hard origin/staging \
		&& $(COMPOSE) down --remove-orphans \
		&& $(COMPOSE) up --build -d"

logs:
	ssh $(REMOTE_USER)@$(REMOTE_HOST) "cd $(REMOTE_DIR) && $(COMPOSE) logs -f"

status:
	ssh $(REMOTE_USER)@$(REMOTE_HOST) "cd $(REMOTE_DIR) && $(COMPOSE) ps"

stop:
	ssh $(REMOTE_USER)@$(REMOTE_HOST) "cd $(REMOTE_DIR) && $(COMPOSE) down"

restart:
	ssh $(REMOTE_USER)@$(REMOTE_HOST) "cd $(REMOTE_DIR) && $(COMPOSE) restart"

.PHONY: setup deploy logs status stop restart
