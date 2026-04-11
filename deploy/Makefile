REMOTE_HOST   = kubys.fr
REMOTE_USER   = sotc
REMOTE_DIR    = /home/sotc/app
COMPOSE_PROD  = docker compose -f docker-compose-prod.yml
SETUP_USER   ?= $(shell whoami)

setup:
	ssh $(SETUP_USER)@$(REMOTE_HOST) 'bash -s' < setup.sh

deploy:
	ssh $(REMOTE_USER)@$(REMOTE_HOST) "\
		cd $(REMOTE_DIR) \
		&& git fetch origin master \
		&& git reset --hard origin/master \
		&& $(COMPOSE_PROD) up --build -d"

logs:
	ssh $(REMOTE_USER)@$(REMOTE_HOST) "cd $(REMOTE_DIR) && $(COMPOSE_PROD) logs -f"

status:
	ssh $(REMOTE_USER)@$(REMOTE_HOST) "cd $(REMOTE_DIR) && $(COMPOSE_PROD) ps"

stop:
	ssh $(REMOTE_USER)@$(REMOTE_HOST) "cd $(REMOTE_DIR) && $(COMPOSE_PROD) down"

restart:
	ssh $(REMOTE_USER)@$(REMOTE_HOST) "cd $(REMOTE_DIR) && $(COMPOSE_PROD) restart"

.PHONY: setup deploy logs status stop restart
