# Étape 1 : Build avec Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build

COPY . .

RUN ./mvnw -pl social-service -am clean package -DskipTests

# Étape 2 : Image finale avec JRE uniquement
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /build/social-service/target/quarkus-app ./quarkus-app
COPY --from=build /build/social-service/target/*.jar ./

EXPOSE 8084

CMD ["java", "-Dquarkus.profile=prod", "-jar", "quarkus-app/quarkus-run.jar"]
