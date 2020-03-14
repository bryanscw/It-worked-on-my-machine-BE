PROJECT_ROOT 	:= $(shell git rev-parse --show-toplevel)

test:
	mvn test

test-integration:
	$(MAKE) -C testing/integration test-integration TYPE=$(TYPE) ID=$(ID)

build-java:
	mvn clean verify

build-docker:
	./infra/docker/build_eduamp_backend.sh
	
clean-docker:
	cd ./infra/deploy && docker-compose down
	
deploy-full:
	cd ./infra/deploy && docker-compose up -d
	
deploy-app:
	cd ./infra/deploy && docker-compose up -d app
	
deploy-db:
	cd ./infra/deploy && docker-compose up -d db

# For cleaning of java docs
clean-html:
	rm -rf 	$(PROJECT_ROOT)/dist

# For creation of java docs
build-html:
	rm -rf $(PROJECT_ROOT)/dist/
