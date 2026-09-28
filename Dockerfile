FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn --batch-mode --no-transfer-progress package -DskipTests

FROM eclipse-temurin:25-jre

WORKDIR /app
RUN groupadd --system spring && useradd --system --gid spring spring
COPY --from=build --chown=spring:spring /workspace/target/demo-0.0.1-SNAPSHOT.jar app.jar
USER spring

EXPOSE 8080 9092
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
