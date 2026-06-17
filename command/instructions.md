# 1	Установить Gitea (Git репозиторий)	Будет, где хранить код

# 1.1. Добавить Helm репозиторий Gitea
helm repo add gitea https://dl.gitea.com/charts/
helm repo update

# 1.2. Установить Gitea в неймспейс ci-cd
kubectl create namespace ci-cd
helm install gitea gitea/gitea --namespace ci-cd --set service.type=NodePort --set service.httpPort=30080 --set persistence.enabled=true --set persistence.size=2Gi
# Упрощенная конфигурация(или запустить манифест kubectl apply -f k8s/manifests/gitea/gitea-simple.yaml)
helm install gitea gitea/gitea --namespace ci-cd --set service.type=NodePort --set service.httpPort=30080 --set persistence.enabled=false --set postgresql.enabled=false --set postgresql-ha.enabled=false --set cache.enabled=false --set memcached.enabled=false --set valkey.enabled=false --set gitea.config.database.DB_TYPE=sqlite3 --set gitea.resources.requests.memory=128Mi --set gitea.resources.limits.memory=256Mi
# удаление
helm uninstall gitea -n ci-cd
# service.type=NodePort — доступ извне
# service.httpPort=30080 — порт для доступа
# persistence.enabled=true — данные сохраняются

# 1.3. Найти адрес доступа к Gitea
minikube service gitea-http -n ci-cd --url

# 2 Создать репозиторий и запушить код
# 2.1. Открыть Gitea в браузере(Перейди по URL из предыдущего шага)
# 2.2. Зарегистрировать первого пользователя
        Имя: admin
        Email: admin@local.com
        Пароль: придумай (запомни!)
# 2.3. Создать новый репозиторий
        Нажми "Create Repository"
        Название: payment-hub
        Нажми "Create Repository"
# 2.4. Залить код в Gitea
# Отправка существующего репозитория из командной строки
git remote add origin http://127.0.0.1:58578/admin123/payment-hub.git
git push -u origin main