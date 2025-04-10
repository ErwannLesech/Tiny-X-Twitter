# Étape 1 : Build avec Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /build

COPY ../pom.xml ../
COPY ../common ../common
COPY . .

RUN ../mvnw -am clean install -DskipTests

# Étape 2 : Image finale avec JRE uniquement
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /build/target/ ./target

EXPOSE 8080

CMD ["java", "-jar", "target/quarkus-app/quarkus-run.jar"]
