FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN apk add --no-cache maven \
    && mvn clean package -DskipTests \
    && apk del maven

COPY target/*.jar app.jar

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]