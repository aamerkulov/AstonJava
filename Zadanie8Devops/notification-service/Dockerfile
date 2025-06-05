FROM eclipse-temurin:24-jdk

WORKDIR /app

COPY target/notification-service-*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]