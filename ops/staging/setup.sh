#!/usr/bin/env bash
set -euo pipefail

# Idempotent setup for staging server
# Run as a user with sudo privileges on staging.kubys.fr
# Usage: ssh <initial-user>@staging.kubys.fr 'bash -s' < setup.sh

SOTC_USER="sotc"
REPO_URL="https://github.com/Joalien/StormOnTheChessBoard.git"
APP_DIR="/home/${SOTC_USER}/app-staging"

echo "=== Storm On The Chess Board - Staging Setup ==="

# --- 1. Create sotc user ---
if id "${SOTC_USER}" &>/dev/null; then
    echo "[ok] User ${SOTC_USER} already exists"
else
    echo "[..] Creating user ${SOTC_USER}"
    sudo useradd -m -s /bin/bash "${SOTC_USER}"
    echo "[ok] User ${SOTC_USER} created"
fi

# --- 2. Copy SSH authorized_keys from current user to sotc ---
CURRENT_KEYS="${HOME}/.ssh/authorized_keys"
SOTC_SSH_DIR="/home/${SOTC_USER}/.ssh"
SOTC_KEYS="${SOTC_SSH_DIR}/authorized_keys"

if [ -f "${CURRENT_KEYS}" ]; then
    sudo mkdir -p "${SOTC_SSH_DIR}"
    sudo cp "${CURRENT_KEYS}" "${SOTC_KEYS}"
    sudo chown -R "${SOTC_USER}:${SOTC_USER}" "${SOTC_SSH_DIR}"
    sudo chmod 700 "${SOTC_SSH_DIR}"
    sudo chmod 600 "${SOTC_KEYS}"
    echo "[ok] SSH keys copied to ${SOTC_USER}"
else
    echo "[!!] No ${CURRENT_KEYS} found - you'll need to set up SSH access to ${SOTC_USER} manually"
fi

# --- 3. Install Docker ---
if command -v docker &>/dev/null; then
    echo "[ok] Docker already installed: $(docker --version)"
else
    echo "[..] Installing Docker"
    curl -fsSL https://get.docker.com | sudo sh
    echo "[ok] Docker installed"
fi

# --- 4. Add sotc to docker group ---
if groups "${SOTC_USER}" | grep -q '\bdocker\b'; then
    echo "[ok] ${SOTC_USER} already in docker group"
else
    echo "[..] Adding ${SOTC_USER} to docker group"
    sudo usermod -aG docker "${SOTC_USER}"
    echo "[ok] ${SOTC_USER} added to docker group"
fi

# --- 5. Enable Docker on boot ---
sudo systemctl enable docker
echo "[ok] Docker enabled on boot"

# --- 6. Clone repo ---
if [ -d "${APP_DIR}/.git" ]; then
    echo "[ok] Repo already cloned at ${APP_DIR}"
else
    echo "[..] Cloning repo"
    sudo -u "${SOTC_USER}" git clone "${REPO_URL}" "${APP_DIR}"
    echo "[ok] Repo cloned to ${APP_DIR}"
fi

echo ""
echo "=== Staging setup complete ==="
echo "You can now deploy with: make deploy"
