# Dockerfile
FROM actdocker123/cbs-micro-service:alpine_custjava.8.322

# This copies to local fat jar inside the image
RUN mkdir -p /opt/Enfinity-AccountService

COPY ./API_DATA /opt/Enfinity-AccountService/API_DATA
COPY ./target/Enfinity-AccountService-1.0.jar /opt/Enfinity-AccountService

WORKDIR /opt/Enfinity-AccountService

# What to run when the container starts
CMD java -Dfile.encoding=UTF8 -jar Enfinity-AccountService-1.0.jar
