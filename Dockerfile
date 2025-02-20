FROM amazoncorretto:21-alpine
RUN apk add curl
ARG JAR_FILE=build/libs/*.jar
EXPOSE 8080
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app.jar"]