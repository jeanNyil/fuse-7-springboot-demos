package com.github.jeannyil.fuse.xmlvalidation.routes;

import javax.ws.rs.core.MediaType;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.github.jeannyil.fuse.common.models.ErrorResponse;

/**
 * Exposes the Sample XML Validation RESTful API
 */
@Component
public class SampleXmlValidationApiRoute extends RouteBuilder {

	private static String logName = SampleXmlValidationApiRoute.class.getName();
	
	@Override
	public void configure() throws Exception {
		
		/**
		 * Catch unexpected exceptions
		 */
		onException(Exception.class).id("handle-all-other-exceptions")
			.handled(true)
			.maximumRedeliveries(0)
			.log(LoggingLevel.ERROR, logName, ">>> ${routeId} - Caught exception: ${exception.stacktrace}").id("log-api-unexpected")
			.to("direct:common-500").id("to-common-500")
			.log(LoggingLevel.INFO, logName, ">>> ${routeId} - OUT: headers:[${headers}] - body:[${body}]").id("log-api-unexpected-response")
		;
		
		
		/**
		 * REST configuration with Camel servlet component
		 */
		restConfiguration()
			.component("servlet")
			.enableCORS(true)
			.bindingMode(RestBindingMode.off) // RESTful responses will be explicitly marshaled for logging purposes
			.dataFormatProperty("prettyPrint", "true")
			// Add the context path for the OpenAPI documentation:
            .apiContextPath("/openapi.json")
		;
		
		/**
		 * REST endpoint for the Service OpenAPI document 
		 */
		rest().id("openapi-document-restapi")
			.path("/")
			.consumes(MediaType.TEXT_XML)
			.produces(MediaType.APPLICATION_JSON)
			
			// Gets the OpenAPI document for this service
			.get("openapi.json")
				.id("get-openapi-doc-route")
				.description("Gets the OpenAPI document for this service")
				.route()
					.log(LoggingLevel.INFO, logName, ">>> ${routeId} - IN: headers:[${headers}] - body:[${body}]").id("log-openapi-doc-request")
					.setHeader(Exchange.CONTENT_TYPE, constant("application/vnd.oai.openapi+json")).id("set-content-type")
					.setBody()
						.constant("resource:classpath:openapi/openapi.json")
						.id("setBody-for-openapi-document")
					.log(LoggingLevel.INFO, logName, ">>> ${routeId} - OUT: headers:[${headers}] - body:[${body}]").id("log-openapi-doc-response")
				.end()
			.endRest()
		;
		
		/**
		 * REST endpoint for the Sample XML Validation RESTful API 
		 */
		rest().id("sample-json-validation-restapi")
				
			// Validates a `Membership` XML instance
			.post("/validateMembershipXML")
				.id("xml-validation-api-route")
				.description("Validates a `Membership` XML instance")
				.param()
					.name("body")
					.type(RestParamType.body)
					.description("A `Membership` XML instance to be validated.")
					.dataType("string")
					.required(true)
				.endParam()
				.responseMessage()
					.code(HttpStatus.OK.value())
					.message(HttpStatus.OK.getReasonPhrase())
					.responseModel(com.github.jeannyil.fuse.common.models.ValidationResult.class)
				.endResponseMessage()
				.responseMessage()
					.code(HttpStatus.BAD_REQUEST.value())
					.message(HttpStatus.BAD_REQUEST.getReasonPhrase())
					.responseModel(com.github.jeannyil.fuse.common.models.ValidationResult.class)
				.endResponseMessage()
				.responseMessage()
					.code(HttpStatus.INTERNAL_SERVER_ERROR.value())
					.message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
					.responseModel(ErrorResponse.class)
				.endResponseMessage()
				// call the ValidateMembershipXMLRoute
				.to("direct:validateMembershipXML")
		;
			
	}

}
