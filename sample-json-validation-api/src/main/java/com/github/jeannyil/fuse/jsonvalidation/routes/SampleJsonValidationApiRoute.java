package com.github.jeannyil.fuse.jsonvalidation.routes;

import javax.ws.rs.core.MediaType;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.github.jeannyil.fuse.common.models.ErrorResponse;

/**
 * Exposes the Sample JSON Validation RESTful API
 */
@Component
public class SampleJsonValidationApiRoute extends RouteBuilder {

	private static String logName = SampleJsonValidationApiRoute.class.getName();

	@Autowired
	BuildProperties buildProperties;
	
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
			.scheme("http")
			.host("0.0.0.0")
			.port("{{server.port}}")
			.contextPath("/")
			// Add information for the generated Open API Specification
            .apiContextPath("/validateMembershipJSON/api-doc")
            	.apiContextRouteId("api-doc-route")
				.apiProperty("api.title", "Sample JSON Validation API")
				.apiProperty("api.description", "A simple API to test the Camel json-schema-validator component")
				.apiProperty("api.version", buildProperties.getVersion())
				.apiProperty("api.contact.name", "Jean Nyilimbibi")
				.apiProperty("api.license.name", "MIT License")
				.apiProperty("api.license.url", "https://opensource.org/licenses/MIT")
				.apiProperty("api.specification.contentType.json", "application/json")
				.apiProperty("api.specification.contentType.yaml", "text/yaml")
				.apiProperty("cors", "true")
		;
		
		/**
		 * REST endpoint for the Service OpenAPI document 
		 
		rest().id("openapi-document-restapi")
			.produces(MediaType.APPLICATION_JSON)
			
			// Gets the OpenAPI document for this service
			.get("/validateMembershipJSON/openapi.json")
				.id("get-openapi-doc-route")
				.description("Gets the OpenAPI document for this service")
				.responseMessage()
					.code(HttpStatus.OK.value())
					.message(HttpStatus.OK.getReasonPhrase())
					.responseModel(com.github.jeannyil.fuse.common.models.ValidationResult.class)
				.endResponseMessage()
				.route()
					.log(LoggingLevel.INFO, logName, ">>> ${routeId} - IN: headers:[${headers}] - body:[${body}]").id("log-openapi-doc-request")
					.setHeader(Exchange.CONTENT_TYPE, constant("application/vnd.oai.openapi+json")).id("set-content-type")
					.setBody()
						.constant("resource:classpath:openapi/openapi.json")
						.id("setBody-for-openapi-document")
					.log(LoggingLevel.INFO, logName, ">>> ${routeId} - OUT: headers:[${headers}] - body:[${body}]").id("log-openapi-doc-response")
				.end()
			.endRest()
		; */
		
		/**
		 * REST endpoint for the Sample JSON Validation RESTful API 
		 */
		rest().id("sample-json-validation-restapi")
			.consumes(MediaType.APPLICATION_JSON)
			.produces(MediaType.APPLICATION_JSON)
				
			// Validates a `Membership` JSON instance
			.post("/validateMembershipJSON")
				.id("json-validation-api-route")
				.description("Validates a `Membership` JSON instance")
				.param()
					.name("body")
					.type(RestParamType.body)
					.description("A `Membership` JSON instance to be validated.")
					.dataType("string")
					.required(true)
					.example(MediaType.APPLICATION_JSON,
							 "{\n    \"requestType\": \"API\",\n    \"requestID\": 5948,\n    \"memberID\": 85623617,\n    \"status\": \"A\",\n    \"enrolmentDate\": \"2019-06-16\",\n    \"changedBy\": \"jeanNyil\",\n    \"forcedLevelCode\": \"69\",\n    \"vipOnInvitation\": \"Y\",\n    \"startDate\": \"2019-06-16\",\n    \"endDate\": \"2100-06-16\"\n}")
				.endParam()
				.responseMessage()
					.code(HttpStatus.OK.value())
					.message(HttpStatus.OK.getReasonPhrase())
					.responseModel(com.github.jeannyil.fuse.common.models.ValidationResult.class)
					.example(MediaType.APPLICATION_JSON, 
							 "{\n    \"validationResult\": {\n        \"status\": \"OK\"\n    }\n}")
				.endResponseMessage()
				.responseMessage()
					.code(HttpStatus.BAD_REQUEST.value())
					.message(HttpStatus.BAD_REQUEST.getReasonPhrase())
					.responseModel(com.github.jeannyil.fuse.common.models.ValidationResult.class)
					.example(MediaType.APPLICATION_JSON, 
							 "{\n    \"validationResult\": {\n        \"status\": \"KO\",\n        \"errorMessage\": \"6 errors found\"\n    }\n}")
				.endResponseMessage()
				.responseMessage()
					.code(HttpStatus.INTERNAL_SERVER_ERROR.value())
					.message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
					.responseModel(ErrorResponse.class)
					.example(MediaType.APPLICATION_JSON, 
							 "{\n\t\"error\": {\n\t\t\"id\": \"500\",\n\t\t\"description\": \"Internal Server Error\",\n\t\t\"messages\": [\n\t\t\t\"java.lang.Exception: Mocked error message\"\n\t\t]\n\t}\n}")
				.endResponseMessage()
				// call the ValidateMembershipJSONRoute
				.to("direct:validateMembershipJSON")
		;
			
	}

}
