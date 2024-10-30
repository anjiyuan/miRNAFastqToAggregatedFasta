# Use an official Maven image to build the project
FROM maven:3.8.4-openjdk-11 AS build

# Set the working directory
WORKDIR /app

# Copy the project files to the container
COPY . .

# Build the project using Maven
RUN mvn clean install

# Use an official OpenJDK image to run the application
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Create the demoData directory and copy files
RUN mkdir -p /app/demoData

# Copy the built JAR file from the build stage
COPY --from=build /app/target/miRNAFastqToAggregatedFasta-1.0.jar /app/miRNAFastqToAggregatedFasta-1.0.jar
COPY --from=build /app/demoData /app/demoData

# Set the entry point for the container
ENTRYPOINT ["java", "-cp", "/app/miRNAFastqToAggregatedFasta-1.0.jar", "qut.miRNAFastqToAggregatedFasta.miRNAFastqToAggregatedFasta"]

# Specify default parameters (can be overridden)
CMD []