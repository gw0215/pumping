FROM eclipse-temurin:17-jdk
WORKDIR /app
ARG JAR_FILE=build/libs/pumping-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app.jar"]
