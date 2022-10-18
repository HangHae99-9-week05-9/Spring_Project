FROM openjdk:17-alpine
COPY ./build/libs/intermediate-*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]