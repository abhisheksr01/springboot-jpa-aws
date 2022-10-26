# springboot-jp

## Introduction

This is a simple Spring Boot + JPA + Postgres application to demonstrate a helloworld style application.

The application exposed 2 endpoints as below:

1. GET - http://localhost:8080/hello/{username}

```shell
1
```

```shell
export spring_profiles_active=prod
```

```shell
./gradlew bootRun
```

```shell
docker run --name postgres -e POSTGRES_PASSWORD=secret -d -p 5432:5432 postgres:15.0-alpine
```