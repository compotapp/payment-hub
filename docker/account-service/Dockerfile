# Самый простой Dockerfile - без Maven внутри
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Копируем уже собранный JAR
COPY services/account-service/target/*.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]

# docker build -f docker/account-service/Dockerfile -t compotapp/hub-account-service:1.0.0-SNAPSHOT .