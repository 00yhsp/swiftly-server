# Build stage
FROM gradle:9.2.1-jdk21 AS build
WORKDIR /home/gradle/src

# Copy only gradle files first for caching dependencies
COPY --chown=gradle:gradle gradlew .
COPY --chown=gradle:gradle gradle gradle
COPY --chown=gradle:gradle build.gradle.kts settings.gradle.kts gradle.properties ./

# Download dependencies (this layer will be cached)
RUN ./gradlew build -x test --no-daemon || true

# Copy source code and build
COPY --chown=gradle:gradle src src
RUN ./gradlew bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Non-root user for security
RUN addgroup -S swiftly && adduser -S swiftly -G swiftly
USER swiftly

COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-default}", "app.jar"]
