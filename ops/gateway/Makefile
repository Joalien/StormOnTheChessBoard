REMOTE_HOST   = kubys.fr
REMOTE_USER   = sotc
REMOTE_DIR    = /home/sotc/gateway
SETUP_USER   ?= $(shell whoami)

deploy:
	scp docker-compose.yml nginx.conf $(REMOTE_USER)@$(REMOTE_HOST):$(REMOTE_DIR)/
	ssh $(REMOTE_USER)@$(REMOTE_HOST) "cd $(REMOTE_DIR) && docker compose up -d"

logs:
	ssh $(REMOTE_USER)@$(REMOTE_HOST) "cd $(REMOTE_DIR) && docker compose logs -f"

status:
	ssh $(REMOTE_USER)@$(REMOTE_HOST) "cd $(REMOTE_DIR) && docker compose ps"

stop:
	ssh $(REMOTE_USER)@$(REMOTE_HOST) "cd $(REMOTE_DIR) && docker compose down"

restart:
	ssh $(REMOTE_USER)@$(REMOTE_HOST) "cd $(REMOTE_DIR) && docker compose restart"

.PHONY: deploy logs status stop restart
