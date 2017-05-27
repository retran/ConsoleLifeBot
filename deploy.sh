cd ~/consolelifebot
git clean -fdx
git pull
lein uberjar
docker stop clb || true
docker rm clb || true
docker build -t retran/consolelifebot:v0.1.0 .
docker run -e "TELEGRAM_BOT_API_TOKEN=$TELEGRAM_BOT_API_TOKEN" -d --name clb retran/consolelifebot:v0.1.0
docker images --no-trunc -aqf "dangling=true" | xargs docker rmi || true
