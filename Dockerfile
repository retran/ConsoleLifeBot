FROM openjdk:8-jre
RUN mkdir /app
COPY bin /app/bin
COPY lib /app/lib
WORKDIR /app
CMD ["/app/bin/consolelifebot"]
