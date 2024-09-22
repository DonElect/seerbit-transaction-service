FROM eclipse-temurin:17

EXPOSE 2024

WORKDIR /app

COPY target/transaction-service-1.0.1.jar transaction-service.jar

ENTRYPOINT ["java", "-jar","/transaction-service.jar"]
