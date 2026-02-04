
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S omnipulse && adduser -S omnipulse -G omnipulse

WORKDIR /app
USER omnipulse:omnipulse

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]