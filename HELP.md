# Getting Started

### Reference Documentation

version: '3'

services:
app:
image: 'abhisheksr01/helloworld:0.0.1' build:
context: . container_name: app depends_on:

- db environment:

# - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/compose-postgres

      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/postgres
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=secret
      - SPRING_JPA_HIBERNATE_DDL_AUTO=create-only

db:
image: 'postgres:14.5-alpine' container_name: db ports: ["5432:5432"]
environment:

- POSTGRES_USER=postgres
- POSTGRES_PASSWORD=secret

# services:

# postgres-db:

# image: postgres:14.5-alpine

# ports: ["5432:5432"]

# environment:

# POSTGRES_DB: postgres

# POSTGRES_PASSWORD: postgres

# POSTGRES_USER: secret

#

# app:

# image: abhisheksr01/helloworld:0.0.1

# command: quickstart

# privileged: true

# depends_on: [postgres-db]

# ports: ["8080:8080"]

# environment:

# CONCOURSE_POSTGRES_HOST: concourse-db

# CONCOURSE_POSTGRES_USER: concourse_user

# CONCOURSE_POSTGRES_PASSWORD: concourse_pass

# CONCOURSE_POSTGRES_DATABASE: concourse

# CONCOURSE_EXTERNAL_URL: http://127.0.0.1:8080

# CONCOURSE_ADD_LOCAL_USER: admin:admin

# CONCOURSE_MAIN_TEAM_LOCAL_USER: admin

# # instead of relying on the default "detect"

# CONCOURSE_WORKER_BAGGAGECLAIM_DRIVER: overlay

# CONCOURSE_CLIENT_SECRET: Y29uY291cnNlLXdlYgo=

# CONCOURSE_TSA_CLIENT_SECRET: Y29uY291cnNlLXdvcmtlcgo=

# CONCOURSE_X_FRAME_OPTIONS: allow

# CONCOURSE_CONTENT_SECURITY_POLICY: "*"

# CONCOURSE_CLUSTER_NAME: tutorial

# CONCOURSE_WORKER_CONTAINERD_DNS_SERVER: "8.8.8.8"

# CONCOURSE_WORKER_RUNTIME: "containerd"

For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.7.4/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.7.4/gradle-plugin/reference/html/#build-image)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.7.4/reference/htmlsingle/#data.sql.jpa-and-spring-data)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.7.4/reference/htmlsingle/#web)
* [Spring REST Docs](https://docs.spring.io/spring-restdocs/docs/current/reference/html5/)

### Guides

The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Additional Links

These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

