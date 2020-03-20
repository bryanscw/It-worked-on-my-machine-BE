PROJECT_ROOT 	:= $(shell git rev-parse --show-toplevel)

test:
	docker run --name eduamp-mysql -e MYSQL_ROOT_PASSWORD=12345 -e MYSQL_USER=user -e MYSQL_PASSWORD=my5ql -p 3306:3306 -d mysql:latest && mvn test && docker rm -rf $(docker ps -aq)

#test-integration:
#	$(MAKE) -C testing/integration test-integration TYPE=$(TYPE) ID=$(ID)

build-java:
	mvn clean verify
	
sql:
	./infra/docker/build_eduamp_backend.sh

image:
	./infra/docker/build_eduamp_backend.sh
	
javadocs:
	sudo mvn clean javadoc:javadoc && rm -rf target/ && rm -rf eduamp/target/javadoc-bundle-options
	
clean-docker:
	cd ./infra/deploy && docker-compose down
	
deploy:
	cd ./infra/deploy && docker-compose up -d

## For cleaning of java docs
#clean-html:
#	rm -rf 	$(PROJECT_ROOT)/dist

## For creation of java docs
#build-html:
#	rm -rf $(PROJECT_ROOT)/dist/
