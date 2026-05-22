# Это удалит все остановленные контейнеры, неиспользуемые образы и сети
docker system prune -a 

# Удаление всех томов
docker volume prune

# Скачает образ не большого приложения запустит и выведет echo
docker run busybox echo "Hello world"

# Запустить образ
docker run <образ>, docker run busybox

# Запустить образ с версией
docker run <образ>:<тег>, docker run busybox:1.0.0

# Запустить образ с именем и портми
docker run --name <имя контейнера> -p 8080:8080 -d<будет работать в фоновом режиме> <имя образа для запуска>
docker run --name k8s-demo-container-name -p 8080:8080 -d k8s-demo

# Создать образ из Dockrerfile
docker build -f example/part2/Dockerfile -t k8s-demo .

# Список всех локально хранящихся образов
docker images

# Список всех запущенных контейнеро
docker ps

# Для просмотра дополнительных сведений
docker inspect <имя контейнера>
docker inspect k8s-demo-container-name

# Запуск оболочки внутри существующего контейнера
docker exec -it <имя контейнера> bash
docker exec -it k8s-demo-container-name bash

# Остановка и удаление контейнера
docker stop k8s-demo-container-name bash
docker rm k8s-demo-container-name bash

# Тегирование образа дополнительным тегом
docker tag k8s-demo compotapp/k8s-demo

# Передача образа в хранилище docker hub
docker push compotapp/k8s-demo

# Логи По имени контейнера
docker logs pbs-catalog-service

# Логи По ID контейнера
docker logs <container-id>

# Логи С непрерывным отслеживанием (как tail -f)
docker logs -f pbs-catalog-service