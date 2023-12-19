# Task Management System

## How to start application

Requirenments:
- `docker` installed
- `docker-compose` installed
- `java 21` installed

To run application on your machine run following commands:

```bash
./gradlew bootJar
cp build/libs/task-management-system-0.0.1-SNAPSHOT.jar src/main/docker
cd src/main/docker
docker-compose up
```

Go to http://localhost:8080/swagger-ui.html to see Swagger UI.

Go to http://localhost:8080/v3/api-docs.yaml to download Open API specification.