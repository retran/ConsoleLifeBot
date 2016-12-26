FROM openjdk:8-jre
RUN mkdir /app \
    mkdir /roms
COPY bin /app/bin
COPY lib /app/lib
WORKDIR /app
CMD ["/app/bin/consolelifebot"]
