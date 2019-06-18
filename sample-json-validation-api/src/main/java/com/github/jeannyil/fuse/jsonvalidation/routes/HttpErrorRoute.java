package com.github.jeannyil.fuse.jsonvalidation.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * 
 * Routes to handle common HTTP errors
 *
 */
@Component
public class HttpErrorRoute extends RouteBuilder {
	
	private static String logName = HttpErrorRoute.class.getName();

	@Override
	public void configure() throws Exception {
		
		/**
		 * Route that returns the common 500-Internal-Server-Error response in JSON format
		 */
		from("direct:common-500")
			.routeId("common-500-http-code-route")
			.log(LoggingLevel.INFO, logName, ">>> ${routeId} - IN: headers:[${headers}] - body:[${body}]").id("log-common-500-request")
			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.INTERNAL_SERVER_ERROR.value())).id("set-common-500-http-code")
			.setHeader(Exchange.HTTP_RESPONSE_TEXT, constant(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())).id("set-common-500-http-reason")
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json")).id("set-500-json-content-type")
			.setBody()
				.method("errorResponseHelper", 
						"generateErrorResponse(${headers.CamelHttpResponseCode}, ${headers.CamelHttpResponseText}, ${exception})")
				.id("set-500-errorresponse-object")
			.end()
			.marshal().json(JsonLibrary.Jackson, true).id("marshal-500-errorresponse-to-json")
			.convertBodyTo(String.class).id("convert-500-errorresponse-to-string")
			.log(LoggingLevel.INFO, logName, ">>> ${routeId} - OUT: headers:[${headers}] - body:[${body}]").id("log-common-500-response")
		;
		
		/**
		 * Route that returns a custom error response in JSON format
		 * The following properties are expected to be set on the incoming Camel Exchange:
		 * <br>- errorId ({@link com.github.jeannyil.fuse.jsonvalidation.constants.ESBConstants#ERROR_ID})
		 * <br>- errorDescription ({@link com.github.jeannyil.fuse.jsonvalidation.constants.ESBConstants#ERROR_DESCRIPTION })
		 * <br>- errorMessage ({@link com.github.jeannyil.fuse.jsonvalidation.constants.ESBConstants#ERROR_MESSAGE })
		 * <br>- httpStatusCode ({@link com.github.jeannyil.fuse.jsonvalidation.constants.ESBConstants#HTTP_STATUS_CODE })
		 * <br>- httpStatusMsg ({@link com.github.jeannyil.fuse.jsonvalidation.constants.ESBConstants#HTTP_STATUS_MSG })
		 */
		from("direct:custom-http-error")
			.routeId("custom-http-error-route")
			.log(LoggingLevel.INFO, logName, ">>> ${routeId} - IN: headers:[${headers}] - body:[${body}]").id("log-custom-http-error-request")
			.setHeader(Exchange.HTTP_RESPONSE_CODE, simple("${exchangeProperty.httpStatusCode}")).id("set-custom-http-code")
			.setHeader(Exchange.HTTP_RESPONSE_TEXT, simple("${exchangeProperty.httpStatusMsg}")).id("set-custom-http-msg")
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json")).id("set-custom-json-content-type")
			.setBody()
				.method("errorResponseHelper", 
						"generateErrorResponse(${exchangeProperty.errorId}, ${exchangeProperty.errorDescription}, ${exchangeProperty.errorMessage})")
				.id("set-custom-errorresponse-object")
			.end()
			.marshal().json(JsonLibrary.Jackson, true).id("marshal-custom-errorresponse-to-json")
			.convertBodyTo(String.class).id("convert-custom-errorresponse-to-string")
			.log(LoggingLevel.INFO, logName, ">>> ${routeId} - OUT: headers:[${headers}] - body:[${body}]").id("log-custom-http-error-response")
		;
	}

}
