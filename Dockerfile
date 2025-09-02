# Base image with Maven and Java installed for the build stage
# Using Maven with JDK 17 for compilation target
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy all source code from the current directory on the host to /app in the container
COPY . .

# Build the WAR file using Maven, skipping tests
RUN mvn clean package -DskipTests

# Use Tomcat 9.0.106 with JDK 17
# Tomcat 9.x works well with JDK 8-17 and javax.servlet APIs
FROM tomcat:9.0.106-jdk17-temurin

# Remove default web applications that come with Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Set environment variables for runtime (these should be overridden in production)
ENV CLOUDINARY_CLOUD_NAME=""
ENV CLOUDINARY_API_KEY=""
ENV CLOUDINARY_API_SECRET=""
ENV DB_URL=""
ENV DB_USERNAME=""
ENV DB_PASSWORD=""
ENV SECRET_KEY=""

# Copy the WAR file from the 'build' stage and rename it to ROOT.war
# Naming it ROOT.war makes the application accessible at the root context path (e.g., your-domain.com:8080/)
COPY --from=build /app/target/JamesThewWebAPI.war /usr/local/tomcat/webapps/ROOT.war

# Create a startup script that configures Tomcat port dynamically
RUN echo '#!/bin/bash\n\
export CATALINA_PORT=${PORT:-8080}\n\
sed -i "s/port=\"8080\"/port=\"$CATALINA_PORT\"/g" /usr/local/tomcat/conf/server.xml\n\
exec catalina.sh run' > /usr/local/tomcat/bin/start.sh && \
chmod +x /usr/local/tomcat/bin/start.sh

# Expose port (Render will set PORT environment variable)
EXPOSE 8080

# Command to start Tomcat with dynamic port configuration
CMD ["/usr/local/tomcat/bin/start.sh"]