FROM eclipse-temurin:24-jdk

WORKDIR /app

COPY target/user-service-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]