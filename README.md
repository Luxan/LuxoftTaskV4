# Description
Programming task for Luxoft recruitment process

# Prerequisites:
* java 17
* maven
* postgresql 

# Task description
    Coding task
    Purpose
    Assess candidate coding skills and style
    Short description
    Design and implement REST interface to consume data-snapshots from one client, validate and persist data in storage,
    distribute persisted data to other clients via REST interface.
    Technological stack and limitations
    • Language: Java
    • Build tools: Maven/Gradle
    • Frameworks: no restrictions
    Requirements
    • REQ-01: As a client I want to upload CSV file via HTTP request
    a. CSV file contains header with following attributes:
    ▪ PRIMARY_KEY
    ▪ NAME
    ▪ DESCRIPTION
    ▪ UPDATED_TIMESTAMP
    b. The last line of file is always empty
    c. "PRIMARY_KEY" attribute must be non-blank string
    d. "UPDATED_TIMESTAMP" attribute if present must be a ISO8601 timestamp string
    • REQ-02: As client I want get persisted data by PRIMARY_KEY attribute via HTTP request. Request parameter(s) should
    be passed in the request URL.
    • REQ-03: As service owner I want to remove record from storage by PRIMARY_KEY attribute via HTTP request
    • REQ-04: As service owner I want no invalid records from uploaded CSV file to be saved
    Non-functional requirements
    • Authentication: not required
    • Logging: standard for web services
    • Error reporting and monitoring: standard for web services
    • Reliability: standard for web services
    • Deployment: standard java-based application deployment options
    • Performance:
    o file upload time less than 60 minutes
    o get record time less than 1sec
    o remove record time no restrictions

    Planned efforts: 2 - 4h

# Setup
Before running the application you need to start postgresql server locally using `systemctl start postgresql.service`
To check whether server is already running use `systemctl status postgresql.service`

To be able to run application you need to create database called `codingtaskv4` or change this name in `src/main/resources/application.properties`

# Running an application with maven
To run the application execute `mvn spring-boot:run`. 
Server will be running on 8881 port.

You can use postman to verify application correctly implemented by executing following requests:

    POST localhost:8081/api/v1/uploadCSV
        - with request parameter csvFile attached
    GET localhost:8081/api/v1/clientData/{primaryKey}
    DELETE localhost:8081/api/v1/clientData/{primaryKey}

Where {primaryKey} is the PRIMARY_KEY supplied in csv file.

# Running tests
To run all the tests enter `mvn test`