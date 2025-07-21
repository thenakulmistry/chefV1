# Stage 1: Build the application using Maven and JDK 21
# We use a specific version for reproducibility and name this stage 'build'
FROM maven:3.9.10-eclipse-temurin-21 AS build

# Set the working directory inside the build container
WORKDIR /app

# Copy the Maven wrapper and pom.xml to leverage Docker's layer caching.
# This allows us to only re-download dependencies if pom.xml changes.

COPY pom.xml .

# Download all dependencies into a separate layer.
RUN mvn dependency:go-offline

# Copy the rest of the application's source code
COPY src ./src

# Package the application. We skip tests as they are part of a separate CI/CD step.
RUN mvn clean package -DskipTests

# ---   

# Stage 2: Create the final, smaller production image
# We use a JRE (Java Runtime Environment) image which is much smaller than a full JDK.
FROM eclipse-temurin:21-jre-jammy

# Set the working directory for the final image
WORKDIR /app

# Copy the built JAR from the 'build' stage into our final image.
# Using a wildcard (*) makes this robust against version changes in pom.xml.
# We also rename it to a consistent 'app.jar'.
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Define the command to run your application when the container starts
# This now refers to the consistently named 'app.jar'.
ENTRYPOINT ["java", "-jar", "app.jar"]
