# Самый простой Dockerfile - без Maven внутри
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Копируем уже собранный JAR
COPY services/saga-orchestrator/target/*.jar app.jar

EXPOSE 8084
EXPOSE 9091

ENTRYPOINT ["java", "-jar", "app.jar"]

# docker build -f docker/saga-orchestrator/Dockerfile -t compotapp/hub-saga-orchestrator:1.0.0-SNAPSHOT .