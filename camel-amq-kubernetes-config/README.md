#### :warning: TODO: _README to be updated to include information about the SPRING-CLOUD-KUBERNETES PLUGIN and logback JSON logging for logstach encoder_ :warning:

# Spring Boot, Camel, ActiveMQ and External Configuration QuickStart

This quickstart shows how to connect a Spring-Boot application to an A-MQ xPaaS message broker and use AMQP messaging between two Camel routes using OpenShift.
Besides, the _logback_ logging framework is configured to use the [logstach-logback-JSON encoder](https://github.com/logstash/logstash-logback-encoder) for ELK.

### Building

The example can be built with

    mvn clean package -Pjava11 -Djkube.skip

### Running the example locally

    mvn spring-boot:run -Pjava11 -Djkube.skip

### Running the example in OpenShift

It is assumed that:
- A fully functional *OpenShift cluster* containing *Red Hat Fuse 7.9 imagestreams* is available and running.
- Please see the [Red Hat Fuse 7.9 on OpenShift Getting Started for Adminstrators](https://access.redhat.com/documentation/en-us/red_hat_fuse/7.9/html-single/fuse_on_openshift_guide/index#get-started-admin) for more details.
- The Red Hat AMQ 7 product should already be installed and running on your OpenShift installation with an SSL-enabled AMQP acceptor.

Then the following command will package your app using `JDK 11` and deploy it on OpenShift thanks to the [*Eclipse Jkube OpenShift Maven Plugin*](https://www.eclipse.org/jkube/docs/openshift-maven-plugin):

    mvn clean oc:deploy -Pjava11

To list all the running pods:

    oc get pods

Then find the name of the pod that runs this quickstart, and output the logs from the running pods with:

    oc logs <name of pod>

You can also use the OpenShift [web console](https://docs.openshift.com/enterprise/3.1/getting_started/developers/developers_console.html#tutorial-video) to manage the
running pods, and view logs and much more.

### Integration Testing

The example includes a [Arquillian Cube OpenShift](https://github.com/arquillian/arquillian-cube/tree/master/openshift) OpenShift Integration Test. 
Once the container image has been built and deployed in OpenShift, the integration test can be run with:

    mvn test -Dtest=*KT

The test is disabled by default and has to be enabled using `-Dtest`. Open Source Community documentation at [Arquillian Cube](http://arquillian.org/arquillian-cube/) provide more information on writing full fledged black box integration tests for OpenShift. 
