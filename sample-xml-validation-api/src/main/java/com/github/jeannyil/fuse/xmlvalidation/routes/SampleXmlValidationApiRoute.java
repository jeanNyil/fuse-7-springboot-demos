package com.github.jeannyil.fuse.xmlvalidation.routes;

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

import com.github.jeannyil.fuse.common.models.ErrorResponse;

/**
 * Exposes the Sample XML Validation RESTful API
 */
@Component
public class SampleXmlValidationApiRoute extends RouteBuilder {

	private static String logName = SampleXmlValidationApiRoute.class.getName();

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
            // .apiContextPath("/validateMembershipXML/api-doc")
            // 	.apiContextRouteId("api-doc-route")
            //     .apiProperty("api.title", "Sample XML Validation API")
			// 	.apiProperty("api.description", "A simple API to test the Camel XML validator component")
			// 	.apiProperty("api.version", buildProperties.getVersion())
			// 	.apiProperty("api.contact.name", "Jean Nyilimbibi")
			// 	.apiProperty("api.license.name", "MIT License")
			// 	.apiProperty("api.license.url", "https://opensource.org/licenses/MIT")
			// 	.apiProperty("api.specification.contentType.json", "application/json")
			// 	.apiProperty("api.specification.contentType.yaml", "text/yaml")
			// 	.apiProperty("cors", "true")
		;
		
		/**
		 * REST endpoint for the Service OpenAPI document 
		 */
		rest().id("openapi-document-restapi")
			.produces(MediaType.APPLICATION_JSON)
			.enableCORS(true)
		
			// Gets the OpenAPI document for this service
			.get("openapi.json")
				.id("get-openapi-spec-route")
				.description("Gets the OpenAPI document for this service in JSON format")
				.route()
					.log(LoggingLevel.INFO, logName, ">>> ${routeId} - IN: headers:[${headers}] - body:[${body}]").id("log-openapi-doc-request")
					.setHeader(Exchange.CONTENT_TYPE, constant("application/vnd.oai.openapi+json")).id("set-content-type")
					.setBody()
						.constant("resource:classpath:openapi/openapi.json")
						.id("setBody-for-openapi-document")
					.log(LoggingLevel.INFO, logName, ">>> ${routeId} - OUT: headers:[${headers}] - body:[${body}]").id("log-openapi-doc-response")
				.end()
		;
		
		/**
		 * REST endpoint for the Sample XML Validation RESTful API 
		 */
		rest().id("sample-xml-validation-restapi")
			.consumes(MediaType.TEXT_XML)
			.produces(MediaType.APPLICATION_JSON)
				
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
					.example(MediaType.TEXT_XML,
							 "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<p:membership xmlns:p=\"http://www.github.com/jeanNyil/schemas/membership/v1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n  <p:requestType>API</p:requestType>\n  <p:requestID>5948</p:requestID>\n  <p:memberID>85623617</p:memberID>\n  <p:status>A</p:status>\n  <p:enrolmentDate>2019-06-29</p:enrolmentDate>\n  <p:changedBy>jeanNyil</p:changedBy>\n  <p:forcedLevelCode>69</p:forcedLevelCode>\n  <p:vipOnInvitation>Y</p:vipOnInvitation>\n  <p:startDate>2019-06-29</p:startDate>\n  <p:endDate>2100-06-29</p:endDate>\n</p:membership>")
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
							 "{\n\t\"validationResult\": {\n\t\t\"status\": \"KO\",\n\t\t\"errorMessage\": \"Validation failed for: com.sun.org.apache.xerces.internal.jaxp.validation.SimpleXMLSchema@5f86796e\\nerrors: [\\norg.xml.sax.SAXParseException: cvc-datatype-valid.1.2.1: '20-06-29' is not a valid value for 'date'., Line : 7, Column : 46\\norg.xml.sax.SAXParseException: cvc-type.3.1.3: The value '20-06-29' of element 'p:enrolmentDate' is not valid., Line : 7, Column : 46\\n]. Exchange[ID-jeansmacbookair-home-1561803539861-1-1]\"\n\t}\n}")
				.endResponseMessage()
				.responseMessage()
					.code(HttpStatus.INTERNAL_SERVER_ERROR.value())
					.message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
					.responseModel(ErrorResponse.class)
					.example(MediaType.APPLICATION_JSON, 
							 "{\n\t\"error\": {\n\t\t\"id\": \"500\",\n\t\t\"description\": \"Internal Server Error\",\n\t\t\"messages\": [\n\t\t\t\"java.lang.Exception: Mocked error message\"\n\t\t]\n\t}\n}")
				.endResponseMessage()
				// call the ValidateMembershipXMLRoute
				.to("direct:validateMembershipXML")
		;
			
	}

}
