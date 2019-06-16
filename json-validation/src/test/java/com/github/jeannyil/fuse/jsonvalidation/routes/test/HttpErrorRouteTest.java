package com.github.jeannyil.fuse.jsonvalidation.routes.test;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.DisableJmx;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.github.jeannyil.fuse.common.constants.ESBConstants;
import com.github.jeannyil.fuse.jsonvalidation.Application;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {Application.class})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@DisableJmx(value=true)
@UseAdviceWith(value=true)
public class HttpErrorRouteTest {
	
	@Autowired
	CamelContext camelContext;
	
	@Autowired
	ProducerTemplate producerTemplate;
	
	@EndpointInject(uri="mock:common-500-http-code-result")
	private MockEndpoint common500HttpCodeMockEndpoint;
	
	@EndpointInject(uri="mock:custom-http-error-result")
	private MockEndpoint customHttpErrorMockEndpoint;
	
	private static String logName = HttpErrorRouteTest.class.getName();
	
	private final static String TEST_EXCEPTION_MESSAGE = "Test error message";
	
	private final static String EXPECTECTED_COMMON500HTTPERROR_RESPONSE = "src/test/resources/data/expectedCommon500HttpErrorResponse.json";
	private final static String EXPECTECTED_CUSTOMHTTPERROR_RESPONSE = "src/test/resources/data/expectedCustomHttpErrorResponse.json";
	
	@Before
	public void amendRoutes() throws Exception {
		
		// Routes to amend
		RouteDefinition customHttpErrorRoute = camelContext.getRouteDefinition("custom-http-error-route");
		RouteDefinition common500HttpCodeRoute = camelContext.getRouteDefinition("common-500-http-code-route");
		
		// Amend routes
		
		customHttpErrorRoute.adviceWith(camelContext, new AdviceWithRouteBuilder() {
			@Override
			public void configure() throws Exception {
				// add at the end of the route to route to this mock endpoint
                weaveAddLast().to("mock:custom-http-error-result");
			}
		});
		
		common500HttpCodeRoute.adviceWith(camelContext, new AdviceWithRouteBuilder() {
			@Override
			public void configure() throws Exception {
				// Add a simulation of an exception on the Camel exchange
				weaveAddFirst().setProperty(Exchange.EXCEPTION_CAUGHT, constant("Mocked error message"));
				// add at the end of the route to route to this mock endpoint
                weaveAddLast().to("mock:common-500-http-code-result");
			}
		});
		
		// Reset all mock endpoints
		common500HttpCodeMockEndpoint.reset();
		customHttpErrorMockEndpoint.reset();
	
		// Start the Camel context
		camelContext.start();
	}
	
	@Test
	public void testCommon500HttpCodeRoute() throws Exception {
		
		// Add a starter route to test the common-500-http-code-route
		camelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("direct:start")
					.setProperty(Exchange.EXCEPTION_CAUGHT, constant(TEST_EXCEPTION_MESSAGE))
					.log(LoggingLevel.INFO, logName, ">>> UNIT TEST - before common-500-http-code-route")
					.to("direct:common-500")
					.log(LoggingLevel.INFO, logName, ">>> UNIT TEST - after common-500-http-code-route");
			}
		});
		
		// Set test expectations
		common500HttpCodeMockEndpoint.expectedMessageCount(1);
		common500HttpCodeMockEndpoint.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
		common500HttpCodeMockEndpoint.expectedHeaderReceived(Exchange.HTTP_RESPONSE_TEXT, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		common500HttpCodeMockEndpoint.expectedHeaderReceived(Exchange.CONTENT_TYPE, "application/json");
		
		// Test the common-500-http-code-route
		String response = producerTemplate.requestBody("direct:start", null, String.class);
		
		// Assert test expectations
		assertTrue("Response is null", response != null); // The expected assertions are below
		common500HttpCodeMockEndpoint.assertIsSatisfied();
		JSONAssert.assertEquals(readFile(EXPECTECTED_COMMON500HTTPERROR_RESPONSE), response, true);
	}
	
	@Test
	public void testCustomHttpErrorRoute() throws Exception {
		
		// Add a starter route to test the custom-http-error-route
		camelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("direct:start")
					.setProperty(ESBConstants.HTTP_STATUS_CODE, simple("" + HttpStatus.NOT_FOUND.value()))
					.setProperty(ESBConstants.HTTP_STATUS_MSG, simple("" + HttpStatus.NOT_FOUND.getReasonPhrase()))
					.setProperty(ESBConstants.ERROR_ID, constant("not_found"))
					.setProperty(ESBConstants.ERROR_DESCRIPTION, constant("Resource not found"))
					.setProperty(ESBConstants.ERROR_MESSAGE, constant(TEST_EXCEPTION_MESSAGE))
					.log(LoggingLevel.INFO, logName, ">>> UNIT TEST - before custom-http-error-route")
					.to("direct:custom-http-error")
					.log(LoggingLevel.INFO, logName, ">>> UNIT TEST - after custom-http-error-route");
			}
		});
		
		// Set test expectations
		customHttpErrorMockEndpoint.expectedMessageCount(1);
		customHttpErrorMockEndpoint.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, HttpStatus.NOT_FOUND.value());
		customHttpErrorMockEndpoint.expectedHeaderReceived(Exchange.HTTP_RESPONSE_TEXT, HttpStatus.NOT_FOUND.getReasonPhrase());
		customHttpErrorMockEndpoint.expectedHeaderReceived(Exchange.CONTENT_TYPE, "application/json");
		
		// Test the common-500-http-code-route
		String response = producerTemplate.requestBody("direct:start", null, String.class);
		
		// Assert test expectations
		assertTrue("Response is null", response != null); // The expected assertions are below
		customHttpErrorMockEndpoint.assertIsSatisfied();
		JSONAssert.assertEquals(readFile(EXPECTECTED_CUSTOMHTTPERROR_RESPONSE), response, true);
	}
	
	private String readFile(String filePath) throws Exception {
        String content;
        FileInputStream fis = new FileInputStream(filePath);
        try {
            content = camelContext.getTypeConverter().convertTo(String.class, fis);
        } finally {
            fis.close();
        }
        return content;
	}
	
}
