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
* Docker-compose

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

# Installing Docker-compose
sudo apt install docker-compose.io
```

## 3. Deployment

### Local deployment
#### Application backend
To deploy Eduamp on your local host, do the following:
1. Change directory to repository root and execute command `mvn clean install`. This will take about 10 minutes.
2. Change directory to eduamp/target and execute command `java -jar eduamp-0.0.1-SNAPSHOT.jar`

#### Database
To deploy the an instance of MySQL on your local host, do the following:
1. Execute command `sudo make run-mysql`
2. To stop and remove the MySQL container, execute command `sudo make stop-mysql`

### Application deployment
To deploy the app, do the following:
1. Go to root folder.
2. Execute `sudo make image`. This will create the latest eduamp Docker image.
3. Execute `sudo make deploy`. This will run the Docker containers for eduamp-app and eduamp-mysql. Note that on first run, the eduamp-app will throw errors as the MySQL is getting started.
4. To clean up, execute `sudo make clean-docker`. This will stop and remove the running Docker containers.

## 4. Testing

### Automated functional testing

The following commands are run to automate testing:
```
# Start MySQL container
sudo make run-mysql

# Run test cases
sudo mvn clean test

# Stop MySQL container once testing is done
sudo make stop-mysql
```

### Automated load testing

The following commands are run to do load testing:
```
# To install Jmeter
sudo apt install jmeter

# Start load testing with Jmeter
sudo make load-test
```
