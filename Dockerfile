FROM openjdk:8-jre-alpine

RUN mkdir /app

WORKDIR /app

ADD ./target/artist-0.0.1-SNAPSHOT.jar /app

EXPOSE 8084

CMD ["java", "-jar", "artist-0.0.1-SNAPSHOT.jar"]
