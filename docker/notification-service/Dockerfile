# Самый простой Dockerfile - без Maven внутри
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN mkdir -p /app/config

# Копируем уже собранный JAR
COPY services/notification-service/target/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]

# docker build -f docker/notification-service/Dockerfile -t compotapp/hub-notification-service:1.0.0-SNAPSHOT .