#!/bin/bash

password=12345

printf "\nBuilding eduamp image...\n\n"

printf "Pruning system...\n"
yes | docker system prune
printf "Pruning completed.\n\n"

if [[ $(docker container ls -aq) ]]; then
    printf "Stopping containers..\n"
    docker container stop $(docker container ls -aq)
    printf "Containers stopped.\n\n"
    
    printf "Removing containers...\n"
    docker container rm $(docker container ls -aq)
    printf "Containers removed.\n\n"
    
else
    printf "No containers found.\n\n"
fi

if [[ "$(docker images -q eduamp:latest 2> /dev/null)" ]]; then
    
    printf "Removing previous eduamp image...\n"    
    docker rmi $(docker images -q eduamp:latest 2> /dev/null)
    printf "Previous eduamp image removed.\n\n"
fi

printf "Updating repository...\n"
ssh-agent bash -c 'ssh-add ~/.ssh/id_rsa; git pull git@github.com:bryanscw/It-worked-on-my-machine-BE.git'
printf "Repository updated.\n\n"

printf "Updating properties file for test...\n"
sed -i "s/\(spring\.datasource\.password=\).*\$/\1${password}/" eduamp/src/test/resources/application.properties
printf "Properties file updated.\n\n"

printf "Starting MySQL container...\n"
docker run --name eduamp-mysql -e MYSQL_ROOT_PASSWORD=12345 -e MYSQL_USER=user -e MYSQL_PASSWORD=my5ql -p 3306:3306 -d mysql:latest
sleep 10
printf "MySQL container started.\n\n"

printf "Building jar file...\n"
mvn clean install
printf "Jar file created.\n\n"

printf "Copying files...\n"
cp ./eduamp/target/eduamp-0.0.1-SNAPSHOT.jar infra/docker/
printf "Files copied.\n\n"

printf "Building eduamp image...\n"
docker build -t eduamp:latest -f infra/docker/Dockerfile .
printf "Eduamp image created.\n\n"

printf "Stopping MySQL container...\n"
docker stop eduamp-mysql
printf "MySQL container stopped.\n\n"

printf "Removing MySQL container...\n"
docker rm eduamp-mysql
printf "MySQL container removed.\n\n"

printf "Cleaning up infra/docker directory...\n"
rm ./infra/docker/eduamp-0.0.1-SNAPSHOT.jar
printf "Clean up done.\n\n"

printf "Image created locally.\n"
