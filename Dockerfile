# Base image with Maven and Java installed
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy all source code
COPY . .

# Build the WAR file
RUN mvn clean package -DskipTests

# Use Tomcat for running the application
FROM tomcat:9.0-jdk17

# Remove default web applications from Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the WAR file from the build stage and rename it to ROOT.war
COPY --from=build /app/target/JamesThewWebAPI.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]