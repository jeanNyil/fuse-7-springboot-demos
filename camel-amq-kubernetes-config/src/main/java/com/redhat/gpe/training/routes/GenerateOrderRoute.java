package com.redhat.gpe.training.routes;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * A route to generate a random order every 5th second.

 * When this route is started, it will automatically send xml messages to the JMS queue incoming.orders
 * on the ActiveMQ broker.
 *
 * The log component is used to add human-friendly business logging statements. It makes it easier to 
 * see what the route is doing.
 *
 * The amq component ensures to use the broker in the kubernetes cluster.
 */
@Component
public class GenerateOrderRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        onException(Exception.class)
            .handled(true)
            .maximumRedeliveries(0)
            .log(LoggingLevel.ERROR, "Unexpected error occured: ${exception}")
            .to(ExchangePattern.InOnly, "amqp:queue:error.queue");

        from("timer:order?period=5000")
            .routeId("generate-order-route")
            .log(LoggingLevel.INFO, "{{hello.message}}")
            .bean("orderGenerator", "generateOrder")
            .setHeader(Exchange.FILE_NAME).method("orderGenerator", "generateFileName")
            .log(LoggingLevel.INFO, "Generating order ${file:name}")
            .to(ExchangePattern.InOnly, "amqp:queue:incoming.orders");
    }

}