FROM openjdk:17-jdk-slim

# Install PulseAudio
RUN apt-get update && apt-get install -y \
    pulseaudio \
    && apt-get clean

# Install CUPS and other utilities
RUN apt-get update && apt-get install -y cups libcups2-dev
# Add a user for running the application, optional but recommended
RUN useradd -m -d /app appuser

# Switch to the app directory and copy the Spring Boot JAR
WORKDIR /app
COPY target/*.jar app.jar

# Make port 8083 available to the world outside this container
EXPOSE 8083
ENV PULSE_SERVER=host.docker.internal
# Run the Spring Boot application as the non-root user
ENTRYPOINT ["java", "-jar", "/app/app.jar"]