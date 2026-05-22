# Самый простой Dockerfile - без Maven внутри
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN mkdir -p /app/config

# Копируем уже собранный JAR
COPY services/analytics-service/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

# docker build -f docker/analytics-service/Dockerfile -t compotapp/hub-analytics-service:1.0.0-SNAPSHOT .