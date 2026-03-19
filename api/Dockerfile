FROM sbtscala/scala-sbt:eclipse-temurin-21.0.8_9_1.12.5_2.13.18 AS build

WORKDIR /build
COPY . .
RUN sbt clean assembly

FROM eclipse-temurin:21.0.10_7-jre-noble
WORKDIR /opt/app
COPY --from=build /build/target/scala-2.13/*.jar app.jar

EXPOSE 58080
CMD ["java", "-jar", "app.jar"]
