FROM openjdk:17-alpine

WORKDIR /opt/server
COPY ./target/identity-service-0.0.1-SNAPSHOT.jar server.jar

EXPOSE 8080
EXPOSE 8083

ENTRYPOINT ["java", "-jar", "server.jar"]