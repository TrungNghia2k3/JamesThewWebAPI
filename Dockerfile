# Base image with Maven and Java installed for the build stage
# Using Maven with JDK 17 for better compatibility
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy all source code from the current directory on the host to /app in the container
COPY . .

# Build the WAR file using Maven, skipping tests
RUN mvn clean package -DskipTests

# Use Tomcat 9.0.106 with JDK 17 for better compatibility
# Tomcat 9.x works well with JDK 8-17 and javax.servlet APIs
FROM tomcat:9.0.106-jdk17-temurin

# Remove default web applications that come with Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the WAR file from the 'build' stage and rename it to ROOT.war
# Naming it ROOT.war makes the application accessible at the root context path (e.g., your-domain.com:8080/)
COPY --from=build /app/target/JamesThewWebAPI.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080, which is the default port Tomcat listens on
EXPOSE 8080

# Command to start Tomcat when the container launches
CMD ["catalina.sh", "run"]