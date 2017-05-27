FROM openjdk:8-jre
RUN mkdir /app
COPY *.edn /app/
COPY target/uberjar/consolelifebot-0.1.0-SNAPSHOT-standalone.jar /app/
WORKDIR /app
CMD ["java", "-jar", "/app/consolelifebot-0.1.0-SNAPSHOT-standalone.jar"]

