# Use Amazon Corretto 21 as the base image
FROM amazoncorretto:21

# Create a volume for temporary files
VOLUME /tmp

# Copy the built JAR file into the Docker image as app.jar
COPY build/libs/*.jar app.jar

# Run the JAR file
ENTRYPOINT ["java","-jar","/app.jar"]
