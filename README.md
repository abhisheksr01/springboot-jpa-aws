# springboot-jpa

## Table of Content

---

- [Introduction](##introduction)
- [Prerequisites](##prerequisites)
- [Architecture](##architecture)
- [Installation and Getting Started](##installation-and-Getting-Started)
- [Development Practice](##development-practice)

## Introduction

This is a simple Spring Boot + JPA + Postgres application to demonstrate a helloworld style application.

Developers can use this repository as a template to build their own Microservice by adding or removing dependencies as
per requirement.

The application exposes 2 endpoints as below:

1. PUT - `/hello/<username> { “dateOfBirth”: “YYYY-MM-DD” }`

   Saves/updates the given user’s name and date of birth in the database.

2. GET - `/hello/<username>`

   Returns hello birthday message for the given user.

   Response Examples:
   A. If username’s birthday is in N days:
    ```json
      { “message”: “Hello, <username>! Your birthday is in N day(s)”}
    ```

   B. If username’s birthday is today:
    ```json
      { “message”: “Hello, <username>! Happy birthday!” }
    ```

In the below sections I will try to explain each integration we have made and how to use.

## Prerequisites

- You must have [Java](https://www.oracle.com/technetwork/java/javaee/documentation/ee8-install-guide-3894351.html)
  installed (min version 8 - the app is locally tested on JDK 8).
- Docker Desktop Installed and Running
- Free CircleCI account (OPTIONAL - unless you would like to see CICD in action)
- AWS CLI & EKS Cluster (OPTIONAL - unless you would like to deploy application to kubernetes)

## Architecture

### Microservice Structure

We are following Classic Microservice "Separation of Concerns" pattern having Controller <--> Service <--> Repository
layers.

The three different takes the responsibilities as below:

- Controller: Controller layer allows access and handles requests coming from the client.<br/>
  Then invoke a business class to process business-related tasks and then finally respond.<br/>
  Additionally Controller may take responsibility of validating the incoming request Payload thus ensuring that any
  invalid or malicious data do not pass this layer.

- Service: The business logic is implemented within this layer, thus keeping the logic separate and secure from the
  controller layer. This layer may further call a Connector or Repository/DAO layer to get the Data to process and act
  accordingly.

- Repository: The only responsibility of this layer is to fetch amd update the data which is required by the Service layer to
  perform the business logic to serve the request.<br/>

This provides us clear separation of implementation, single responsibility, high readability and extensibility.

For example if we want to migrate from JPA to another framework the impact will be limited to the Repository layer.

![](doc-resources/images/microservice-structure.jpg)

### Continuous Integration, Delivery and Deployment

**Continuous Integration**: It's a software development practise where members of a team integrate their work frequently, usually each person integrates at least daily - leading to multiple integrations per day.<br/>
Each integration is verified by an automated build (including test) to detect integration errors as quickly as possible.

Continuous Integration is a key step to digital transformation.

**Continuous Delivery**: It's software engineering approach in which teams produce software in short cycles, ensuring that the software can be reliably released at any time and, when releasing the software, doing so **manually**.

**Continuous Deployment**: It means that every change goes through the pipeline and **automatically** gets put into production, resulting in many production deployments every day.<br/>
To do Continuous Deployment you must be doing Continuous Delivery.

Pictorial representation of the above two approaches:
![](doc-resources/images/continuous-delivery-deployment.png)

Reference :

- https://martinfowler.com/articles/continuousIntegration.html
- https://martinfowler.com/bliki/ContinuousDelivery.html
- https://dzone.com/articles/continuous-delivery-vs-continuous-deployment-an-ov

Now let us look at the key building blocks for achieving CI/CD.

![](doc-resources/images/circleci-cicd.jpg)

### AWS System Architecture
![](doc-resources/images/system-diagram.png)



[//]: # (### Installation and Getting Started)

[//]: # ()
[//]: # (Let us get started by Cloning or [Downloading]&#40;https://github.com/abhisheksr01/springboot-jpa/archive/refs/heads/main.zip&#41; repository in your local workstation.)

[//]: # ()
[//]: # (```shell)

[//]: # (git clone https://github.com/abhisheksr01/springboot-jpa.git)

[//]: # (```)

[//]: # ()
[//]: # (Once cloned/downloaded import the project in your favourite IDE &#40;IntelliJ, Eclipse etc&#41;.)

[//]: # ()
[//]: # (We are using [Gradle Wrapper]&#40;https://docs.gradle.org/current/userguide/gradle_wrapper.html&#41; for dependency management)

[//]: # (so that you do not need to explicitly configure Gradle.)

[//]: # ()
[//]: # (From the terminal/command prompt execute below command to download dependencies specified in the [build.gradle]&#40;build.gradle&#41; and build the java code without running tests.)

[//]: # ()
[//]: # (```bash)

[//]: # (./gradlew clean build -x test)

[//]: # (```)

[//]: # ()
[//]: # (You can visualise the codebase with the help of below diagram.)

[//]: # ()
[//]: # (![Visualization of the codebase]&#40;./diagram.svg&#41;)

[//]: # ()
[//]: # (<a propertyName = "MSStructure"></a>)
### Development Practice

At the core of the Cloud Native Practices in Software Engineering lies the Behavior Driven Development(BDD) and
Test-Driven Development (TDD).<br/>

While developing the code I followed BDD first approach where I wrote a failing feature/acceptance criteria thus driving
our development through behavior and then followed by Test Driven Development.<br/>

A feature is not considered as developed until all the Unit Tests (TDD) and feature (BDD) passes.

![](doc-resources/images/bdd-tdd-cycle.png)

Example:

```shell
curl --location --request GET 'http://localhost:8080/hello/abhishek' \
--header 'Authorization: Basic YWJoaXNoZWs6cmFqcHV0' \
--header 'Cookie: JSESSIONID=B2D8EAF5217287C2AD889127852E8DA0'

```

**Note: Texts highlighted in light blue colour are clickable hyperlinks for additional references.**

```shell
export spring_profiles_active=prod
```

```shell
./gradlew bootRun
```

```shell
docker run --name postgres -e POSTGRES_PASSWORD=secret -d -p 5432:5432 postgres:15.0-alpine
```