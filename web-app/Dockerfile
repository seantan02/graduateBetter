# Use the official OpenJDK base image
FROM openjdk:17
WORKDIR /app

# Set the working directory in the container
WORKDIR /graduate-better/app

# Copy the JAR file into the container at /app
COPY target/graduate-better.jar /graduate-better/app/

# Expose the port that your application will run on
EXPOSE 8080
EXPOSE 5432

# Specify the command to run on container startup
CMD ["java", "-jar", "graduate-better.jar"]