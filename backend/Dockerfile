####################################### Build stage #######################################
FROM maven:3.9-eclipse-temurin-17-alpine AS build-stage

COPY pom.xml /build/
COPY evaluation-workbench /build/evaluation-workbench/
COPY evaluation-workbench-web /build/evaluation-workbench-web/
COPY settings.xml /root/.m2/settings.xml
RUN rm -f /build/evaluation-workbench-web/src/main/resources/config/app.env
RUN rm -f /build/evaluation-workbench-web/src/main/resources/config/*-dev.yml
RUN rm -f /build/evaluation-workbench-web/src/main/resources/logging/*.xml
RUN rm -f /build/evaluation-workbench-web/src/main/resources/certificates/*.crt

WORKDIR /build/

RUN mvn dependency:go-offline
# Build project
RUN mvn -DskipTests clean package

######################################## Run Stage ########################################
FROM eclipse-temurin:17-jre-ubi9-minimal

ENV SERVER_PORT=8080
EXPOSE ${SERVER_PORT}

COPY --from=build-stage /build/evaluation-workbench-web/target/evaluation-workbench-web.jar /app/evaluation-workbench-web.jar

ENTRYPOINT ["java","-Dspring.config.additional-location=file:/config/","-Dspring.profiles.active=${PROFILE}","-Djava.security.egd=file:/dev/./urandom","-jar","/app/evaluation-workbench-web.jar"]