# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk

# Set the working directory
WORKDIR /app

# Copy the built jar file (make sure to build it first)
COPY target/*.jar app.jar

# Expose port 8080 (or your app's port)
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"] 