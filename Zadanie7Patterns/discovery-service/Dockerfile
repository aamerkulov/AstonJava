FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/discovery-service-*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]