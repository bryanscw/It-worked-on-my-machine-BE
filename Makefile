PROJECT_ROOT 	:= $(shell git rev-parse --show-toplevel)

test:
	docker run --name eduamp-mysql -e MYSQL_ROOT_PASSWORD=12345 -e MYSQL_USER=user -e MYSQL_PASSWORD=my5ql -p 3306:3306 -d mysql:latest && mvn clean test && docker rm -rf $(docker ps -aq)

#test-integration:
#	$(MAKE) -C testing/integration test-integration TYPE=$(TYPE) ID=$(ID)

build-java:
	mvn clean verify
	
run-mysql:
	docker run --name eduamp-mysql -e MYSQL_ROOT_PASSWORD=12345 -e MYSQL_USER=user -e MYSQL_PASSWORD=my5ql -p 3306:3306 -d mysql:latest
	
stop-mysql:
	docker stop eduamp-mysql && docker rm eduamp-mysql

image:
	./infra/docker/build_eduamp_backend.sh
	
javadocs:
	sudo mvn javadoc:javadoc && rm -rf target/ && rm -rf eduamp/target/javadoc-bundle-options
	
clean-up:
	cd ./infra/deploy && docker-compose down
	
deploy:
	cd ./infra/deploy && docker-compose up -d
	
load-test:
	cd ./eduamp/src/test/resources && jmeter -n -t eduamp_load_test.jmx

## For cleaning of java docs
#clean-html:
#	rm -rf 	$(PROJECT_ROOT)/dist

## For creation of java docs
#build-html:
#	rm -rf $(PROJECT_ROOT)/dist/
