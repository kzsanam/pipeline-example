FROM --platform=amd64 \
    sbtscala/scala-sbt:eclipse-temurin-alpine-25.0.1_8_1.12.5_3.8.2 AS build

ENV PATH="/opt/sbt/bin:${PATH}"

WORKDIR /app

# Copy project files
COPY build.sbt ./
COPY project/ ./project
COPY src/ ./src
RUN ls -l /app/project/

# Build fat JAR
RUN sbt clean assembly

# Stage 2: Runtime
FROM --platform=amd64 \
    sbtscala/scala-sbt:eclipse-temurin-alpine-25.0.1_8_1.12.5_3.8.2

WORKDIR /opt/app

# Copy fat JAR from builder
COPY --from=build /app/target/scala-*/*.jar ./job.jar

# Flink entrypoint (standalone job)
ENTRYPOINT ["java","-jar","job.jar"]