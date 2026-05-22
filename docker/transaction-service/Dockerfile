# Самый простой Dockerfile - без Maven внутри
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Копируем уже собранный JAR
COPY services/transaction-service/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]

# docker build -f docker/transaction-service/Dockerfile -t compotapp/hub-transaction-service:1.0.0-SNAPSHOT .