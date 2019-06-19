package com.redhat.gpe.training.fuse.routes;

import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 *  This route consumes messages from the JMS queue incomingOrders on the ActiveMQ broker within the ESB.
 *
 *  The <choice/> element contains the content based router. The two <when/> clauses use XPath to define the criteria
 *  for entering that part of the route. When the country in the XML message is set to UK or US, the message will follow
 *  the specific rules defined for that country. The <otherwise/> element ensures that any message that does not meet the
 *  requirements for either of the <when/> elements will follow another route.
 */
@Component
public class AmqpCBRRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        onException(Exception.class)
            .handled(true)
            .maximumRedeliveries(0)
            .useOriginalMessage()
            .log(LoggingLevel.ERROR, "Unexpected error occured: ${exception}")
            .to(ExchangePattern.InOnly, "amqp:queue:error.queue");

        from("amqp:queue:incoming.orders")
            .routeId("amqp-cbr-route")
            .streamCaching()
            .choice()
                .when(xpath("/order/customer/country = 'UK'"))
                    .log(LoggingLevel.INFO, "Sending order ${file:name} to the UK")
                    .to(ExchangePattern.InOnly, "amqp:queue:uk.orders")
                .when(xpath("/order/customer/country = 'US'"))
                    .log(LoggingLevel.INFO, "Sending order ${file:name} to the US")
                    .to(ExchangePattern.InOnly, "amqp:queue:us.orders")
                .otherwise()
                    .log(LoggingLevel.INFO, "Sending order ${file:name} to another country")
                    .to(ExchangePattern.InOnly, "amqp:queue:other.orders")
            .end()
            .log(LoggingLevel.INFO, "Done processing ${file:name}");

    }

}