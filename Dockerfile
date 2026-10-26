FROM eclipse-temurin:21-jre

RUN useradd --system --uid 10001 app
WORKDIR /app

COPY target/flat2java-spring-demo-*.jar /app/app.jar
COPY samples/ /app/samples/

EXPOSE 8080
USER 10001
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
