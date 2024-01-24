# Use OpenJDK 11 as the base image
FROM openjdk:17

LABEL maintainer="minutaurus"

ARG JAR_FILE=build/libs/sosik-intake-service-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} /sosik-intake-service-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","/sosik-intake-service-0.0.1-SNAPSHOT.jar"]

EXPOSE 9003

