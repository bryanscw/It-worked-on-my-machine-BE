# EduAmp

This repository contains the backend for the It-worked-on-my-machine CZ3003 project.

## 1. Warning(s)

EduaAmp uses *JDK11*. A few warnings can be safely ignored.

```text
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by org.springframework.cglib.core.ReflectUtils (file:/Users/su/.m2/repository/org/springframework/spring-core/5.2.4.RELEASE/spring-core-5.2.4.RELEASE.jar) to method java.lang.ClassLoader.defineClass(java.lang.String,byte[],int,int,java.security.ProtectionDomain)
WARNING: Please consider reporting this to the maintainers of org.springframework.cglib.core.ReflectUtils
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
```

The warning shown above is caused by the use of internal JDK API in CGLIB, which will appear on *JDK9+*. 

You can read more about the [related issue here](https://github.com/spring-projects/spring-framework/issues/22674).

## 2. Dependencies

The following dependencies are required to run eduamp
* Build-essentials
* Maven
* Java 11
* Docker

To install dependencies for this project, execute the following 
commands:
```
# Installing build essentials
sudo apt install build-essentials

# Installing maven
sudo apt install maven

# Installing Java
sudo apt install openjdk-11-jdk

# Installing and setting up Docker
sudo apt install docker.io
groupadd docker
sudo usermod -aG docker $USER
```

## 3. Deployment

### Local deployment
#### Backend
To deploy Eduamp on your local host, do the following:
1. Edit eduamp/src/main/resources/application.properties such that under the `Data source properties` section, the database source url is pointing to local host (127.0.0.1) 
2. Change directory to root and execute command `mvn clean install`. This will take about 10 minutes.
3. Change directory to eduamp/target and execute command `java -jar eduamp-0.0.1-SNAPSHOT.jar`

#### Database
To deploy the an instance of MySQL on your local host, do the following:
1. Execute command `docker run --name eduamp-mysql -e MYSQL_ROOT_PASSWORD=12345 -e MYSQL_USER=user -e MYSQL_PASSWORD=my5ql -p 3306:3306 -d mysql:latest`

### Application deployment
To deploy the app, do the following:
1. Go to root folder.
2. Execute `sudo make build-docker`. This will create the latest eduamp Docker image.
3. Execute `sudo make deploy`. This will run the Docker containers for eduamp-app and eduamp-mysql. Note that on first run, the eduamp-app will throw errors as the MySQL is getting started.
4. To clean up, execute `sudo make clean-docker`. This will stop and remove the running Docker containers.

## 4. Testing

In the initial stages of this project, testing is done manually. 

The following commands are run for initial testing for the database:
```
docker exec -it eduamp-mysql bash

mysql -u root -p12345

USE security;

INSERT INTO app_user (user_email, user_pass, user_role) VALUES ('admin1@test.com', '$2y$12$SuYibPdYWQ7EsDfgvvvM1eUhGB4kbRr.Rod8ctgasELFO/spNbuEC', 'ROLE_ADMIN');
INSERT INTO app_user (user_email, user_pass, user_role) VALUES ('user1@test.com', '$2y$12$VO4g7Wg/YuI051vri9E9YOBuw7RAvyNczZD3MT2kckPkoSsuBUMcW', 'ROLE_USER');
```

Once the data is inserted successfully into the database, the Postman collection is used to verify the operations of the API.
