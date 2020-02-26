PROJECT_ROOT 	:= $(shell git rev-parse --show-toplevel)

test:
	mvn test

test-integration:
	$(MAKE) -C testing/integration test-integration TYPE=$(TYPE) ID=$(ID)

build-java:
	mvn clean verify

build-docker:
	docker build -t $(REGISTRY)/eduamp:$(VERSION) -f infra/Dockerfile .

# For cleaning of java docs
clean-html:
	rm -rf 	$(PROJECT_ROOT)/dist

# For creation of java docs
build-html:
	rm -rf $(PROJECT_ROOT)/dist/