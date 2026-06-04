# Остановить всё
docker-compose down
docker-compose -f docker/payment-hub/docker-compose.yaml down

# Запустить заново
docker-compose -f docker/payment-hub/docker-compose.yaml up -d