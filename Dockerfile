FROM openjdk:8-jre
RUN mkdir /app \
    mkdir /roms
COPY ./build/libs/*.jar /app
WORKDIR /data
CMD ["java", "-jar", "/app/consolelifebot-0.1.0.jar"]
