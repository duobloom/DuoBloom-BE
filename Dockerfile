FROM bellsoft/liberica-openjdk-alpine:17
ARG JAR_FILE=DuoBloom/build/libs/DuoBloom-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
