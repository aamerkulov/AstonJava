FROM eclipse-temurin:24-jdk

WORKDIR /app

COPY target/discovery-service-*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]