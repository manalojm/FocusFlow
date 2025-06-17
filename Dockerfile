# Stage 1: Build the Java application
# We use a Maven image that includes a full JDK (Java Development Kit)
# This stage has all the tools needed to compile your Java code and build your JAR file.
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set the working directory inside this build container
WORKDIR /app

# Copy the Maven project file (pom.xml) first.
# This helps Docker cache dependencies. If only your Java code changes,
# Docker won't need to re-download all your project's libraries.
COPY pom.xml .

# If you're using Gradle, uncomment the lines below and comment out the Maven-specific lines:
# COPY build.gradle .
# COPY settings.gradle .

# Download project dependencies. This also leverages Docker's build cache.
# If dependencies haven't changed, this step will be skipped in subsequent builds.
RUN mvn dependency:go-offline
# If using Gradle: RUN gradle dependencies

# Copy the rest of your application's source code into the container
COPY app/src ./src

# Build the Java application into a JAR file.
# -DskipTests is used to skip running tests during the Docker build,
# as tests are usually run in a separate CI/CD step.
RUN mvn clean package -DskipTests
# If using Gradle: RUN gradle build -x test

# Stage 2: Create the final, lightweight runtime image
# We use a JRE (Java Runtime Environment) image here, which is much smaller than a JDK.
# It only contains what's needed to run Java applications, not compile them.
# The 'alpine' version is even smaller, based on a minimal Linux distribution.
FROM eclipse-temurin:17-jre-alpine

# Set the working directory for the runtime container
WORKDIR /app

# Copy the built JAR file from the 'build' stage into the final image.
# This is the key to multi-stage builds: we only bring over the necessary artifact.
# Adjust the path '/app/target/*.jar' if your build system outputs the JAR elsewhere.
# For example, Gradle typically puts it in /app/build/libs/*.jar
COPY --from=build /app/target/*.jar app.jar
# If using Gradle: COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port your Java application listens on.
# For Spring Boot applications, 8080 is the default.
# Make sure this matches the port your Java app is configured to use!
EXPOSE 8080

# Define the command to run your Java application when the container starts.
# This will execute your JAR file.
ENTRYPOINT ["java", "-jar", "app.jar"]
