# Camel based implementation of the _AddressBook API_

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

The above command line will deploy 1 _Openshift pod_ running the _Address Book API_ service instance.
Cf. the ``replicas`` property in the [deployment.yml](src/main/fabric8/deployment.yml) template.

Openshift resources similar to the following will be created:

````
$ oc get all
NAME                              READY     STATUS      RESTARTS   AGE
pod/addressbook-api-1-bqhq4       1/1       Running     0          34s
pod/addressbook-api-s2i-1-build   0/1       Completed   0          49s

NAME                                      DESIRED   CURRENT   READY     AGE
replicationcontroller/addressbook-api-1   1         1         1         36s

NAME                      TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)    AGE
service/addressbook-api   ClusterIP   172.30.196.199   <none>        8080/TCP   36s

NAME                                                 REVISION   DESIRED   CURRENT   TRIGGERED BY
deploymentconfig.apps.openshift.io/addressbook-api   1          1         1         config,image(addressbook-api:1.0.0)

NAME                                                 TYPE      FROM      LATEST
buildconfig.build.openshift.io/addressbook-api-s2i   Source    Binary    1

NAME                                             TYPE      FROM      STATUS     STARTED          DURATION
build.build.openshift.io/addressbook-api-s2i-1   Source    Binary    Complete   49 seconds ago   11s

NAME                                             DOCKER REPO                                                      TAGS      UPDATED
imagestream.image.openshift.io/addressbook-api   docker-registry.default.svc:5000/fuse-services/addressbook-api   1.0.0     38 seconds ago

NAME                                       HOST/PORT                                                     PATH      SERVICES          PORT      TERMINATION   WILDCARD
route.route.openshift.io/addressbook-api   addressbook-api-fuse-services.apps.e06d.example.opentlc.com             addressbook-api   8080                    None
````

And then you can access it's OpenAPI docs hosted by the service at:

    curl -s http://$(oc get route addressbook-api --template={{.spec.host}})/openapi.json
    
To delete the `addressbook-api` application and delete all its resources from OpenShift, run the following command line:

```
$ oc delete all -lapp=addressbook-api
```

