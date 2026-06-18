FROM maven:3.9.8-eclipse-temurin-8 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:8-jre-alpine
RUN addgroup -S nekocafe && adduser -S nekocafe -G nekocafe
WORKDIR /app
COPY --from=build /app/target/nekocafe-lab3-1.0.0.jar app.jar
USER nekocafe
EXPOSE 8080
HEALTHCHECK --interval=15s --timeout=5s --retries=5 CMD wget -qO- http://localhost:8080/api/health || exit 1
ENTRYPOINT ["java","-jar","/app/app.jar"]
