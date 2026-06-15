#Configuration 
:' 
make them executable: sudo chmod +x /usr/local/bin/deploy-frontend.sh
manual deployment: sudo /usr/local/bin/deploy-frontend.sh
'
#!/bin/bash

# ================= CONFIGURATION =================
REPO_URL="git@github.com:yourusername/your-repo.git"   # or https://github.com/...
REPO_DIR="/var/www/repo"
FRONTEND_DIR="frontend"          # the folder inside your repo containing the website
WEB_ROOT="/var/www/yourdomain.com/html"
BRANCH="main"
NGINX_USER="www-data"
# =================================================

set -euo pipefail

echo "=== Starting deployment at $(date) ==="

# 1. Ensure repo directory exists
sudo mkdir -p "$REPO_DIR"

# 2. Clone if it doesn't exist, otherwise pull
if [ ! -d "$REPO_DIR/.git" ]; then
    echo "Cloning repository..."
    sudo git clone --branch "$BRANCH" --single-branch "$REPO_URL" "$REPO_DIR"
else
    echo "Pulling latest changes..."
    cd "$REPO_DIR"
    sudo git fetch origin "$BRANCH"
    sudo git reset --hard "origin/$BRANCH"
    sudo git clean -fd
fi

# 3. Verify frontend folder exists
if [ ! -d "$REPO_DIR/$FRONTEND_DIR" ]; then
    echo "ERROR: Folder '$FRONTEND_DIR' not found in repo!"
    exit 1
fi

# 4. Sync only frontend folder to web root (rsync is safer than cp)
echo "Deploying to Nginx web root..."
sudo rsync -a --delete \
    "$REPO_DIR/$FRONTEND_DIR/" \
    "$WEB_ROOT/"

# 5. Fix permissions
echo "Setting permissions..."
sudo chown -R "$NGINX_USER:$NGINX_USER" "$WEB_ROOT"
sudo find "$WEB_ROOT" -type d -exec chmod 755 {} \;
sudo find "$WEB_ROOT" -type f -exec chmod 644 {} \;

# 6. Reload Nginx (only if config test passes)
echo "Reloading Nginx..."
sudo nginx -t && sudo systemctl reload nginx

echo "=== Deployment complete at $(date) ==="