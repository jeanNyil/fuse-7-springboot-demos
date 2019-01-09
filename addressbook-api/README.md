# Camel based implementation of the _AddressBook API_ API

## API Description ##
Address Book API

### Building

    mvn clean package

### Running Locally

    mvn spring-boot:run

Getting the API docs:

    curl http://localhost:8080/openapi.json

## Running on OpenShift

    mvn fabric8:deploy

You can expose the service externally using the following command:

    oc expose svc addressbook-api

And then you can access it's OpenAPI docs hosted by the service at:

    curl -s http://$(oc get route addressbook-api --template={{.spec.host}})/openapi.json
