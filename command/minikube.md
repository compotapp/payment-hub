minikube start --cpus 2 --memory 4g --driver docker --profile pbs
minikube start --cpus 2 --memory 6g --nodes 2 --disk-size 40g --driver docker 

minikube start --profile pbs
minikube stop --profile pbs

minikube dashboard --profile pbs

minikube delete --profile pbs --all
minikube delete --all

minikube addons enable ingress --profile pbs
kubectl get pods -n ingress-nginx

# Включить metrics-server (для HPA)
minikube addons enable metrics-server

# Проверить, что всё включилось
minikube addons list

minikube image load pbs-catalog-service --profile pbs

minikube addons enable ingress --profile pbs

minikube tunnel --profile pbs
http://127.0.0.1/books

minikube ip


kubectl create namespace payment-hub
kubectl create namespace ci-cd
kubectl get namespaces

# Чтобы собирать образы прямо в Minikube (без внешнего реестра).
eval $(minikube -p minikube docker-env)
# Важно: Это нужно выполнять в каждом новом терминале, где ты будешь собирать образы.




