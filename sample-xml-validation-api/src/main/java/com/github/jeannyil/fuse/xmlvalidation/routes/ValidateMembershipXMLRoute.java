package com.github.jeannyil.fuse.xmlvalidation.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 *  Route that validates a sample XML data against the Membership XML schema.
 *  Expects the sample XML as a Camel message body.
 *
 */
@Component
public class ValidateMembershipXMLRoute extends RouteBuilder {
	
	private static String logName = ValidateMembershipXMLRoute.class.getName();

	@Override
	public void configure() throws Exception {
		
		/**
		 * Catch unexpected exceptions
		 */
		onException(Exception.class)
			.handled(true)
			.maximumRedeliveries(0)
			.log(LoggingLevel.ERROR, logName, ">>> ${routeId} - Caught exception: ${exception.stacktrace}").id("log-validateMembershipXML-unexpected")
			.to("direct:common-500").id("to-validateMembershipXML-500")
			.log(LoggingLevel.INFO, logName, ">>> ${routeId} - OUT: headers:[${headers}] - body:[${body}]").id("log-validateMembershipXML-unexpected-response")
		;
		
		/**
		 * Catch the org.apache.camel.component.jsonvalidator.JsonValidationException exception
		 */
		onException(org.apache.camel.ValidationException.class)
			.handled(true)
			.maximumRedeliveries(0)
			.log(LoggingLevel.ERROR, logName, ">>> ${routeId} - Caught exception after XML Schema Validation: ${exception.stacktrace}").id("log-validateMembershipXML-exception")
			.setHeader(Exchange.HTTP_RESPONSE_CODE, simple("" + HttpStatus.BAD_REQUEST.value()))
			.setProperty(Exchange.HTTP_RESPONSE_TEXT, simple("" + HttpStatus.BAD_REQUEST.getReasonPhrase()))
			.setBody()
				.method("validationResultHelper", "generateKOValidationResult(${exception.message})")
				.id("set-KO-validationResult")
			.marshal().json(JsonLibrary.Jackson, true).id("marshal-KO-validationResult-to-json")
			.log(LoggingLevel.INFO, logName, ">>> ${routeId} - validateMembershipXML response: headers:[${headers}] - body:[${body}]").id("log-validateMembershipXML-KO-response")
		;
		
		/**
		 * Validates a sample XML data against the Membership XML schema.
		 * Expects the sample XML as a Camel message body.
		 */
		from("direct:validateMembershipXML")
			.routeId("validate-membership-xml-route")
			.log(">>> ${routeId} - Before XML Schema Validation - Camel Exchange message: in.headers[${headers}] - in.body[${body}]")
			.to("validator:xml-schema/membership.xsd")
			.log(">>> ${routeId} - XML Schema validation is successful")
			.setBody()
				.method("validationResultHelper", "generateOKValidationResult()")
				.id("set-OK-validationResult")
			.marshal().json(JsonLibrary.Jackson, true).id("marshal-OK-validationResult-to-json")
			.log(LoggingLevel.INFO, logName, ">>> ${routeId} - validateMembershipXML response: headers:[${headers}] - body:[${body}]").id("log-validateMembershipXML-response")
		;
		
	}

}
