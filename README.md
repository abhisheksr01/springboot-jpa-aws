# springboot-jpa

## Table of Content

- [Introduction](#introduction)
- [Architecture](#architecture)
    - [Microservice Structure](#microservice-structure)
    - [Continuous Integration, Delivery and Deployment](#continuous-integration-delivery-and-deployment)
    - [AWS System Architecture](#aws-system-architecture)
- [Application](#application)
    - [Prerequisites](#prerequisites)
    - [Installation and Getting Started](#installation-and-getting-started)
    - [Development Practice](#development-practice)
    - [Automated Tests](#automated-tests)
    - [Manual Testing or Starting App locally](#manual-testing-or-starting-app-locally)
- [Assumptions and Considerations](#assumptions-and-considerations)

## Introduction

This is a simple Spring Boot + JPA + Postgres application to demonstrate a helloworld style application.

Developers can use this repository as a template to build their own Microservice by adding or removing dependencies as
per requirement.

The application exposes 2 endpoints as below:

1. PUT - `/hello/<username> { “dateOfBirth”: “YYYY-MM-DD” }`

   Saves/updates the given user’s name and date of birth in the database and returns 204 status code.

2. GET - `/hello/<username>`

   Returns hello birthday message for the given user and status code 200.

   Response Examples:
   A. If username’s birthday is in N days:
    ```json
      {
        "message": "Hello, <username>! Your birthday is in N day(s)"
      }
    ```

   B. If username’s birthday is today:
    ```json
      {
         "message": "Hello, <username>! Happy birthday!"
      }
    ```

In the following sections we will see the architecture, how to test and use this application.

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

- Repository: The only responsibility of this layer is to fetch amd update the data which is required by the Service
  layer to perform the business logic to serve the request.<br/>

This provides us clear separation of implementation, single responsibility, high readability and extensibility.

For example if we want to migrate from JPA to another framework the impact will be limited to the Repository layer.

![](doc-resources/images/microservice-structure.jpg)

### Continuous Integration, Delivery and Deployment

**Continuous Integration**: It's a software development practise where members of a team integrate their work
frequently, usually each person integrates at least daily - leading to multiple integrations per day.<br/>
Each integration is verified by an automated build (including test) to detect integration errors as quickly as possible.

Continuous Integration is a key step to digital transformation.

**Continuous Delivery**: It's software engineering approach in which teams produce software in short cycles, ensuring
that the software can be reliably released at any time and, when releasing the software, doing so **manually**.

**Continuous Deployment**: It means that every change goes through the pipeline and **automatically** gets put into
production, resulting in many production deployments every day.<br/>
To do Continuous Deployment you must be doing Continuous Delivery.

Pictorial representation of the above two CD approaches:
![](doc-resources/images/continuous-delivery-deployment.png)

Reference :

- https://martinfowler.com/articles/continuousIntegration.html
- https://martinfowler.com/bliki/ContinuousDelivery.html
- https://dzone.com/articles/continuous-delivery-vs-continuous-deployment-an-ov

> Note: 
> In a real world scenario we will have at least development, preproduction/qa and production environments and the changes are well tested before progressing to the stage/job.
> For simplicity and ease of understanding we are only deploying to a single environment.


1. We are using [CircleCI](https://circleci.com/) to achieve the principles of CICD and [config file](.circleci/config.yml) is stored in `.circleci/config.yml`.

    [Click here](https://circleci.com/docs/getting-started/?section=getting-started&utm_source=google&utm_medium=sem&utm_campaign=sem-google-dg--emea-en-dsa-maxConv-auth-nb&utm_term=g_-_c__dsa_&utm_content=&gclid=Cj0KCQjwteOaBhDuARIsADBqRei3HV1lRtcmhlgkF5rp8UTiYoTg18oKmDv13m5I_HKUSnhVpj1tNnIaAiJhEALw_wcB) and follow the instructions to setup the pipeline.

2. Once pipeline is configured and developer pushes his changes to the GitHub repository, GH will send an event to trigger the pipeline.

![](doc-resources/images/circleci-cicd.jpg)

3. The pipeline can be virtually split into below sections:

- <b>Build:</b> Pipeline pulls the latest code. The job installs all the dependencies, build and compile the code to output an executable Jar. We cache the workspace to optimise the pipeline execution.

- <b>Quality & Security Gates:</b> These jobs are executed in parallel to speed up the CICD process and reduce feedback time. We execute our automated tests, scan the code and dependencies for vulnerabilities and execute Mutation tests.

- <b>Docker Lint Build Push:</b> Once all the quality and security jobs are executed successfully this job is triggered. The responsibility of this jon is to lint the Dockerfile, build a docker image, scan the docker image for vulnerabilities and if no vulnerabilities are identified push to Docker hub.

- <b>Approval Job:</b> This job acts as a manual gateway to ensure what changes get deployed to the AWS EKS Cluster.

- <b>Deploy app to k8s</b>: This job uses the [kubernetes helm chart files](./kubernetes/helm-chart) to use the docker image push in the previous job and deploy it to AWS EKS Cluster.

- <b>Post Deployment Jobs</b>: Once the application is deployed successfully we execute Acceptance test to ensure the application works as expected, Penetration test, load test and health check (is optional as acceptance test covers this aspect)

<b>Ensuring High Availability No Downtime</b>

- To ensure high availability and no downtime we are spinning 3 replicas of the pods.
- This can be further improvised by provisioning the pods in different k8s worker nodes(virtual machines)
- Provisioning the pods in 2 different cluster and load balancing the traffic using an external load balancer (Idle for production environment).

<b>All the credentials are securely stored in the CircleCI and pulled appropriately in the pipeline config</b>

### AWS System Architecture

In this section we will discuss how the request flows when a customer accesses our API using a URL as can be seen in the below picture

For the sake of simplicity the diagram has abstracted low level internal working of kubernetes.

![](doc-resources/images/system-diagram.png)

1. Client makes a request: The client can be anything from a app, browser or another microservice.
2. Public Load Balancer: The public application load balancer intercepts the request and redirect it to the AWS EKS Cluster.
3. Ingress Load Balancer: This load balancer redirects the request to one of the pods where our application container is running. As our application is stateless hence the subsequent request can be served by any other pod in th pool.
4. Application Pod: The request is first validated and then processed. In our case the client has requested for some information hence the application inside the pod will make a call to the postgres DB to retrieve the user details.
   After retrieving the data from DB the response is sent with appropriate information.
5. Postgres DB: Here the data is persisted.

Provisioning of the infrastructure is typically managed by a separate team (Platform or Site Reliability Engineering team).
They use Infrastructure as code such as Terraform, Ansible, AWS CDK etc for the same.

## Application

In this section we will discuss how to use test, use the application and development practices that we have followed.

### Prerequisites

- You must have [Java](https://www.oracle.com/technetwork/java/javaee/documentation/ee8-install-guide-3894351.html)
  installed (min version 8 - the app is locally tested on JDK 8).
- Docker Desktop Installed and Running
- Free CircleCI account (OPTIONAL - unless you would like to see CICD in action)
- AWS CLI & EKS Cluster (OPTIONAL - unless you would like to deploy application to kubernetes)

### Installation and Getting Started

Let us get started by Cloning or [Downloading](https://github.com/abhisheksr01/springboot-jpa/archive/refs/heads/main.zip) repository in your local workstation.
```shell
git clone https://github.com/abhisheksr01/springboot-jpa.git
```

Once cloned/downloaded import the project in your favourite IDE (IntelliJ, Eclipse etc).

We are using [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) for dependency management  so that you do not need to explicitly configure Gradle.

From the terminal/command prompt execute below command to download dependencies specified in the [build.gradle](build.gradle) and build the java code without running tests.

```shell
./gradlew clean build -x test
```
You can visualise the codebase with the help of below diagram.

![Visualization of the codebase](./diagram.svg)

<a propertyName = "MSStructure"></a>

### Development Practice

At the core of the Cloud Native Practices in Software Engineering lies the Behavior Driven Development(BDD) and
Test-Driven Development (TDD).<br/>

While developing the code I followed BDD first approach where I wrote a failing feature/acceptance criteria thus driving
our development through behavior and then followed by Test Driven Development.<br/>

A feature is not considered as developed until all the Unit Tests (TDD) and feature (BDD) passes.

![](doc-resources/images/bdd-tdd-cycle.png)

Additionally, we have used multiple utilities/practices to optimise our development strategy as below:

- Jacoco test code coverage
- Map Struct
- Mutation Testing
- Lombok
- Dependency Vulnerability Scanning
- Snyk


### Automated Tests

We are using Junit5 and Gherkins Cucumber for writing automated Unit and Cucumber based E2E tests respectively.

We are using [TestContainers](https://www.testcontainers.org/) for simulating the Database while executing automated tests.

Ensure you have docker running as testcontainers uses it for DB simulation.

[Click here to open helloworld.feature](src/test/resources/feature/helloworld.feature) file to see what E2E tests we are executing.

Execute below command to run all the tests (Unit and Cucumber):
```shell
./gradlew test
```
Once tests are executed you can see reports under `springboot-jpa/build/reports` directory.

We can individually execute `e2e` test by executing below command:
```shell
./gradlew test -Pe2e=true
```
Similarly execute below command to run only unit tests:
```shell
./gradlew test -Pexcludee2e=true
```

### Manual Testing or Starting App locally

> Note: All the endpoints are using Basic Authorization mechanism to demonstrate how we can secure our API Endpoints.</br>
> In a real world scenario we preferably use Oauth 2.0 based authentication and authorization mechanism.

We will use docker compose to locally start the application and test the application functionality.

From the root of this directory execute below commands:
```shell
./gradlew clean build -x test
```

To start the application and postgres DB locally.

```shell
docker compose up -d
```
The above command will read the instructions specified in the [docker-compose.yml](docker-compose.yml) file in the root of this project.

First it will pull the postgres docker image and start the database container then build a local docker image from our codebase and then start the app docker container.

#### Test Using Postman

Open [Postman](https://www.postman.com/) and import the Postman collection stored in the [./springboot-jpa/scripts/springbootjpa-helloworld.postman_collection.json](./scripts/springbootjpa-helloworld.postman_collection.json) file.

[Click here](https://learning.postman.com/docs/getting-started/importing-and-exporting-data/) to learn how to import the Collection Data.

1. Adding/Updating a user

Once the Collection has been successfully imported Open the `Add/Update User` request and click Send Button.

You should receive `204` success status as shown below.
![](./doc-resources/images/postman-put-request.png)

2. Get Birthday Message

Open the `Get Birthday Message` request and click Send.

You should receive `200` success status and message in body as below:
![](./doc-resources/images/postman-get-request.png)

#### Test Using Curl

Alternatively we can use curl commands to invoke the API's and test them locally.

1. Adding/Updating a user

Open terminal or command prompt and execute below command:

```shell
curl --location --request PUT 'http://localhost:8080/hello/roger' \
--header 'Authorization: Basic YWJoaXNoZWs6cmFqcHV0' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=E4E5F52B085DA7B9F8B59A88A3A5E105' \
--data-raw '{ "dateOfBirth": "1980-10-10" }'
```

2. Get Birthday Message

```shell
curl --location --request GET 'http://localhost:8080/hello/roger' \
--header 'Authorization: Basic YWJoaXNoZWs6cmFqcHV0' \
--header 'Cookie: JSESSIONID=E4E5F52B085DA7B9F8B59A88A3A5E105'
```

Once executed successfully you should see a response similar to below screenshot.
![](./doc-resources/images/curl-put-get-request.png)

3. When User does not exist
```shell
curl --location --request GET 'http://localhost:8080/hello/sam' \
--header 'Authorization: Basic YWJoaXNoZWs6cmFqcHV0' \
--header 'Cookie: JSESSIONID=E4E5F52B085DA7B9F8B59A88A3A5E105'
```
Expected Error Response:
```text
No user found, please check the username
```

4. When Special characters are passed in username
```shell
curl --location --request GET 'http://localhost:8080/hello/@abishek' \
--header 'Authorization: Basic YWJoaXNoZWs6cmFqcHV0' \
--header 'Cookie: JSESSIONID=E4E5F52B085DA7B9F8B59A88A3A5E105'
```
Expected Error Response:
```text
UserName must contain only letters
```

5. When the dateOfBirth is equal to current date
```shell
curl --location --request PUT 'http://localhost:8080/hello/roger' \
--header 'Authorization: Basic YWJoaXNoZWs6cmFqcHV0' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=E4E5F52B085DA7B9F8B59A88A3A5E105' \
--data-raw '{ "dateOfBirth": "2022-10-26" }'
```

```text
Date of birth must be a date before the today date
```
Once testing is done let us remove the containers by executing below command:

Expected Error Response:
```shell
docker compose down
```

## Assumptions and Considerations

1. All the dates are calculated in `UTC` and supporting the respective time zones were kept out of scope as it would have required Zone Ids from client.
2. The username can contain spaces in between and if a username is provided with trailing spaces we will trim it.
3. The Basic Auth mechanism is added to demonstrate how we can secure our API's in real world scenario we will prefer OAuth 2.0.
4. All the credentials (Basic Auth and DB credentials) are replaced in CICD pipeline.</br> Additionally, we have used Spring's Active profile concept to load prod environment specific configuration by setting `SPRING_PROFILE_ACTIVE=prod` environment variable.
5. The application expects a Postgress Database to be already provisioned before the application deployment and appropriate credentials are shared.

You can refer [another repository by clicking here](https://github.com/abhisheksr01/spring-boot-microservice-best-practices) to learn more about individual tools and mechanism used in this repository.