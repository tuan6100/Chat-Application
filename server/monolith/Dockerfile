FROM openjdk:23-jdk-slim AS build

WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew bootJar


FROM openjdk:23-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]
