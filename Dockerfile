FROM openjdk:11-jdk
EXPOSE 443
ARG JAR_FILE=/build/libs/devmatching-0.0.1-SNAPSHOT.jar
VOLUME ["/logs"]
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]