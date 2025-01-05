# springboot-jpa

## Table of Content

- [Introduction](#introduction)
- [Architecture](#architecture)
  - [Microservice Structure](#microservice-structure)
  - [Continuous Integration, Delivery and Deployment](#continuous-integration-delivery-and-deployment)
  - [AWS System Architecture](#aws-system-architecture)
- [AWS Infrastructure Provisioning](#aws-infrastructure-provisioning)
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

- <https://martinfowler.com/articles/continuousIntegration.html>
- <https://martinfowler.com/bliki/ContinuousDelivery.html>
- <https://dzone.com/articles/continuous-delivery-vs-continuous-deployment-an-ov>

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

### AWS Infrastructure Provisioning

Provisioning of the infrastructure is typically managed by a separate team (Platform or Site Reliability Engineering team).

In this section we will discuss database setup, installation, schema installation/upgrade and how would that be handled in the infrastructure and application deployment.

Prerequisite:

- Terraform Installed `version >= 1.0`
- AWS CLI and access to create resources in a account

1. Terraform Introduction:

   We are using Terraform for provisioning a Postgres RDS Instance on AWS. The Terraform code is in the [infrastructure](./infrastructure/) directory.

   In our TF codebase we are using Open Source terraform modules as they:
   - Save Time
   - Highly reusable
   - Highly Configurable
   - Reusable modules encourage best practices
   - Maintaining modules is hard and time consuming and is managed by the community

   [Click here](https://transcend.io/blog/terraform-modules-open-source/) to learn more about the advantages of using terraform modules.

   Although in certain cases we do not want to rely o Open Source modules but that depends on th respective scenario.

2. Infrastructure Backend setup

   > This is a one time setup and should not be repeated

   Terraform uses backend for storing the TF Infrastructure state to know what resources it needs to  provision, change or de provision. In AWS it uses S3 buckets for storing the statefile.

    <details>
      <summary> Click here to learn more about backend configuration </summary></br>

   ### Problem

      Before we begin to start provisioning autonomous infrastructure and the platforms through pipelines etc    we have to do some bootstrapping work such as creating a   bucket for storing Terraform backend and creating IAM    roles for allowing cross account access with admin privileges so that we can assume the role to create further resources without switching credentials for    different AWS Accounts.

      This is the chicken and the egg paradox where one has some dependency on another.

   ### Aim or Solution

      Instead of going to the console and manually creating the bootstrap infrastructure, we will use Terraform to    provision the resources and then store the state to the   remote bucket.

      The very first thing that will have to do is to create an S3 backend bucket for storing the state of  the    Terraform execution for this repository.

      Then create Cross Account IAM role so that we can use it for further infrastructure provisioning.

   ### Execution

      </b>Provisioning backend S3 bucket in Dev Account:</b>

      1. Comment the `backend "s3" {}` section in the `infrastructure/backend/provider.tf` file as we     currently do not have an S3 bucket to store the state file.
      2. Authenticate AWS CLI with your credentials
      3. Once done navigate to `infrastructure/backend` directory and execute below make command to initialize terraform:

      ```
      terraform init
      ```

      4. Run make plan to see the changes:

      ```
      terraform plan
      ```

      5. Once you see the planned changes, run make apply     command and type `yes` when prompted:ake plan to  see     the changes:

      ```
      terraform apply
      ```

      6. Once terraform changes are applied successfully  uncomment the `backend "s3" {}` section in the `infrastructure/backend/provider.tf` and run below   command so the local Terraform state gets stored in   the S3 bucket.

      ```
      terraform init -migrate-state -backend-config=./    backend-config.hcl
      ```

      When prompted type `yes` and hit enter.
    </details></br>

3. DB Provisioning

     we are using a very popular [terraform-aws-modules/rds/aws](https://registry.terraform.io/modules/terraform-aws-modules/rds/aws/latest) module for provisioning a Postgres DB in AWS Cloud & [VPC](https://registry.terraform.io/modules/terraform-aws-modules/vpc/aws/latest) module in which our RDS DB will be provisioned.

     The Terraform code will provision a single instance of DB in `eu-west-2` region and replication in `eu-west-1` for disaster recovery. This replicated DB can be used for READ heavy operations to keep the load on primary DB low.

     All the DB Configuration are managed in the [dev.tfvars](./infrastructure/dev.tfvars) file as below:

     ```
      aws_rds_db = {
        name                                  = "springbootjpadb",
        region                                = "eu-west-2",
        region2                               = "eu-west-1",
        engine                                = "postgres"
        engine_version                        = "15",
        family                                = "postgres14",
        major_engine_version                  = "14"
        publicly_accessible                   = false
        instance_class                        = "db.t4g.large",
        allocated_storage                     = 20
        max_allocated_storage                 = 100
        port                                  = 5432
        multi_az                              = true
        maintenance_window                    = "Mon:00:00-Mon:03:00"
        backup_window                         = "03:00-06:00"
        enabled_cloudwatch_logs_exports       = ["postgresql", "upgrade"]
        create_cloudwatch_log_group           = true
        backup_retention_period               = 1
        skip_final_snapshot                   = true
        deletion_protection                   = false
        performance_insights_enabled          = true
        performance_insights_retention_period = 7
        create_monitoring_role                = true
        monitoring_interval                   = 60
        monitoring_role_name                  = "springbootjpa-monitoring-role-name"
        monitoring_role_use_name_prefix       = true
        monitoring_role_description           = "Description for monitoring  role"
      }
     ```

     Based on our requirements for the DB we can alter any of the above property.

     Change the directory to `./infrastructure` and execute below command (make sure you have followed the instructions for the backend) to initialize the TF.

     ```
     terraform init -backend-config=./backend-config.hcl
     ```

     Execute below command to see what terraform will provision for us:

     ```
     terraform plan --var-file=./dev.tfvars
     ```

     Execute below command to provision the infrastructure and when prompted enter `yes`:

     ```
     terraform apply --var-file=./dev.tfvars
     ```

     By default the TF code creates a non publicly accessible RDS Instance which can only be accessed within the VPC. Hence the application should be deployed inside the same VPC.

     In case you wish to access from another VPC then you will have to create vpc peering.

     [Click here to learn more about it.](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/USER_VPC.Scenarios.html)

     - Public access to RDS instances:

       Sometimes it is handy to have public access to RDS instances (it is not recommended for production) by specifying these arguments:

       ```
       create_database_subnet_group           = true
       create_database_subnet_route_table     = true
       create_database_internet_gateway_route = true
       enable_dns_hostnames = true
       enable_dns_support   = true
       ```

4. DB Initial Setup and DDL management using [Flyway](https://flywaydb.org/)

   Hibernate DDL creation is a nice feature for PoCs or small projects. For more significant projects that have a complex deployment workflow and features like version rollback in case of a significant issue, the solution is not sufficient.

   There are several tools to handle database migrations, and one of the most popular is Flyway, which works flawlessly with Spring Boot. Briefly, Flyway looks for SQL scripts on our project’s resource path and runs all scripts not previously executed in a defined order. Flyway stores what files were executed into a particular table called SCHEMA_VERSION.

   To implement Flyway in our codebase we follow below steps:

   - Added Flyway dependency in the [build.gradle](./build.gradle):

   ```
   implementation 'org.flywaydb:flyway-core:9.8.1'
   ```

   - Created a new sub dir as `src/main/resources/db/migration/` where all the sql files will reside.
   - Added a new sql file called [V1__ddl.sql](./src/main/resources/db/migration/V1__ddl.sql): By default, Flyway looks at files in the format V$X__$DESCRIPTION.sql, where $X is the migration version name.
   - Update the application.yaml ddl-auto to validate, which will only validate if the required DB tables exist:

     ```
     spring:
      jpa:
        hibernate:
          ddl-auto: validate
     ```

   When the application starts Flyway will use the [V1__ddl.sql](./src/main/resources/db/migration/V1__ddl.sql) file to create a new table.

   ```sql
   create table users (
       id int8 not null,
        date_of_birth date,
        name varchar(255),
        primary key (id)
    );
   ```

    Create new versions of the file to manage the DB schema.

    The advantages of using this approach are:
    - Create a database from scratch.
    - Have a single source of truth for the version of the database state.
    - Have a reproducible state of the database in local and remote environments.
    - Automate database changes deployment, which helps to minimize human errors.
    - Sync the DB changes and test along with the application. In our case Spring boot will first make the required DB changes ensuring the   application do not fail due to non existence DB changes.

    For this approach to work as expected, we must first test the changes in lower environment and ensure all the tests pass and no errors  detected.

5. API versioning strategy for DDL management and Application deployment

     The Flyway approach works best with smaller DB with Microservice Architecture.

     But all the teams or organization might not prefer the Flyway approach towards the DB DDL management for multiple reasons.
     - Separation of DB Management & Application Deployment
     - DB used by multiple Teams or Services
     - Compliance and Security reasons etc

     In these scenarios a separate team or DBA's first apply the DDL changes (recommendation is to do in it Immutable manner) and then new version of API is deployed.

     The old version of the API will support the existing functionality with the help of a Mapper/Converter which we can slowly deprecate over the time.

     For this strategy to work appropriately:
     - Should have consistent and established API Versioning strategy
     - Strategy to deploy the changes to manage the dependency
     - Very strong & reliable Automation test suite to detect any issues
     - Coordination between all the concerned teams
     - Real time or Low latency data replication to avoid data loss if any
  
7. DDL changes with downtime

     If the application has scope for downtime and all the key stakeholders agree we can perform the DB changes and application deployment with some downtime.

     All the above strategies above is still applicable to this strategy as well.

8. General Best practices around DB:

- Ensure no public access is granted to DB unless for specific reason
- No or Low replication strategy for non prod system for cost effectiveness.
- Multiple replicas for Prod system
- While performing the DB changes and application deployment ensure we have a robust rollback strategy
- Prefer AWS Authentication mechanism
- Use non admin/master accounts for DB usage and management
- Use Read Replicas and Cache for read heavy operations
- All the changes should be well tested in lower environments
- Understand the technical and business needs to set the "Maintenance Window"

You can refer below link for AWS Recommendation for Amazon RDS:

<https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_BestPractices.html>

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
5. The application expects a Postgres Database to be already provisioned before the application deployment and appropriate credentials are shared.

You can refer [another repository by clicking here](https://github.com/abhisheksr01/spring-boot-microservice-best-practices) to learn more about individual tools and mechanism used in this repository.
