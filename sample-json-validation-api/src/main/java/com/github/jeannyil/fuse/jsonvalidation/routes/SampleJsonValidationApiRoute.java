package com.github.jeannyil.fuse.jsonvalidation.routes;

import javax.ws.rs.core.MediaType;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.github.jeannyil.fuse.jsonvalidation.constants.ESBConstants;
import com.github.jeannyil.fuse.jsonvalidation.models.ErrorResponse;

/**
 * Exposes the Sample JSON Validation RESTful API
 */
@Component
public class SampleJsonValidationApiRoute extends RouteBuilder {

	private static String logName = SampleJsonValidationApiRoute.class.getName();
	
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
			.consumes(MediaType.APPLICATION_JSON)
			.produces(MediaType.APPLICATION_JSON)
			
			// Gets the OpenAPI document for this service
			.get("openapi.json")
				.id("get-version-api-route")
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
		 * REST endpoint for the Sample JSON Validation RESTful API 
		 */
		rest().id("sample-json-validation-restapi")
				
			// Validates a `Membership` instance
			.post("/validateMembership")
				.id("view-ba-api-route")
				.description("Validates a `Membership` instance")
				.param()
					.name("body")
					.type(RestParamType.body)
					.description("A new `Membership` to be created.")
					.dataType("string")
					.required(true)
				.endParam()
				.responseMessage()
					.code(HttpStatus.OK.value())
					.message(HttpStatus.OK.getReasonPhrase())
					.responseModel(com.github.jeannyil.fuse.jsonvalidation.models.ValidationResult.class)
				.endResponseMessage()
				.responseMessage()
					.code(HttpStatus.BAD_REQUEST.value())
					.message(HttpStatus.BAD_REQUEST.getReasonPhrase())
					.responseModel(com.github.jeannyil.fuse.jsonvalidation.models.ValidationResult.class)
				.endResponseMessage()
				.responseMessage()
					.code(HttpStatus.INTERNAL_SERVER_ERROR.value())
					.message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
					.responseModel(ErrorResponse.class)
				.endResponseMessage()
				// call viewBA route
				.to("direct:validateMembership")
		;
			
	}

}
