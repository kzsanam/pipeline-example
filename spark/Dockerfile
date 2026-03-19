# -------- Build Stage --------
FROM sbtscala/scala-sbt:eclipse-temurin-21.0.8_9_1.12.5_2.13.18 AS build

ENV JAVA_OPTS="-Xmx3G -Xss2M"

WORKDIR /build

# Copy build files and sources
COPY build.sbt .
COPY project ./project
COPY src ./src

# Build fat JAR
RUN sbt clean assembly

# -------- Runtime Stage --------
FROM apache/spark:4.0.1-scala2.13-java21-r-ubuntu as runtime

USER root

WORKDIR /app

COPY --from=build /build/target/scala-2.13/*.jar /app/app.jar

COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh
ENTRYPOINT ["/app/entrypoint.sh"]
