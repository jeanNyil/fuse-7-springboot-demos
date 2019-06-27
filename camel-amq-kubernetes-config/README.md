#### :warning: TODO: _README to be updated to include information about the SPRING-CLOUD-KUBERNETES PLUGIN and logback JSON logging for logstach encoder_ :warning:

# Spring Boot, Camel, ActiveMQ and External Configuration QuickStart

This quickstart shows how to connect a Spring-Boot application to an A-MQ xPaaS message broker and use AMQP messaging between two Camel routes using OpenShift.
Besides, the _logback_ logging framework is configured to use the [logstach-logback-JSON encoder](https://github.com/logstash/logstash-logback-encoder) for ELK.

### Building

The example can be built with

    mvn clean package -Dfabric8.skip

### Running the example locally

    mvn spring-boot:run

### Running the example in OpenShift

It is assumed that:
- OpenShift platform is already running, if not you can find details how to [Install OpenShift at your site](https://docs.openshift.com/container-platform/3.3/install_config/index.html).
- Your system is configured for Fabric8 Maven Workflow, if not you can find a [Get Started Guide](https://access.redhat.com/documentation/en/red-hat-jboss-middleware-for-openshift/3/single/red-hat-jboss-fuse-integration-services-20-for-openshift/)
- The Red Hat JBoss A-MQ xPaaS product should already be installed and running on your OpenShift installation, one simple way to run a A-MQ service is following the documentation of the A-MQ xPaaS image for OpenShift related to the `amq63-basic` template.

Then the following command will package your app and run it on OpenShift:

    mvn fabric8:deploy

To list all the running pods:

    oc get pods

Then find the name of the pod that runs this quickstart, and output the logs from the running pods with:

    oc logs <name of pod>

You can also use the openshift [web console](https://docs.openshift.com/enterprise/3.1/getting_started/developers/developers_console.html#tutorial-video) to manage the
running pods, and view logs and much more.

### Integration Testing

The example includes a [Arquillian Cube Openshift](https://github.com/arquillian/arquillian-cube/tree/master/openshift) OpenShift Integration Test. 
Once the container image has been built and deployed in OpenShift, the integration test can be run with:

    mvn test -Dtest=*KT

The test is disabled by default and has to be enabled using `-Dtest`. Open Source Community documentation at [Arquillian Cube](http://arquillian.org/arquillian-cube/) provide more information on writing full fledged black box integration tests for OpenShift. 
