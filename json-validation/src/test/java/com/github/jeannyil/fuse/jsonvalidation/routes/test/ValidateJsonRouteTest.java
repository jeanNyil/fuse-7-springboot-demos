package com.github.jeannyil.fuse.jsonvalidation.routes.test;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.DisableJmx;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.github.jeannyil.fuse.jsonvalidation.Application;

/**
 * Camel JSON Schema Validator component Test class
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {Application.class})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@DisableJmx(value=true)
public class ValidateJsonRouteTest {

	@Autowired
	private CamelContext camelContext;
	
	@Autowired
	private ProducerTemplate producerTemplate;
	
	@EndpointInject(uri="mock:success")
	private MockEndpoint successMockEndpoint;
	
	@EndpointInject(uri="mock:businessException")
	private MockEndpoint businessExceptionMockEndpoint;
	
	@EndpointInject(uri="mock:unexpectedException")
	private MockEndpoint unexpectedExceptionMockEndpoint;
	
	private final static String VALID_JSON_DATA = "src/test/resources/json-schema-validator/valid_json_data.json";
	private final static String INVALID_JSON_DATA = "src/test/resources/json-schema-validator/invalid_json_data.json";
	
	@Before
	public void setup() throws Exception {
		camelContext.setTracing(true);
		successMockEndpoint.reset();
		businessExceptionMockEndpoint.reset();
		unexpectedExceptionMockEndpoint.reset();
	}
	
	@Test
	public void validJsonSchemaValidatorTest() throws Exception {
		// Expectations
		successMockEndpoint.expectedMessageCount(1);
		businessExceptionMockEndpoint.expectedMessageCount(0);
		unexpectedExceptionMockEndpoint.expectedMessageCount(0);
		
		// Test the json-schema-validator component
		producerTemplate.sendBody("direct:test-json-schema-validator", readFile(VALID_JSON_DATA));
		
		// Assert expectations
		assertTrue("There should be at least 1 Camel Exchange in successMockEndpoint!", successMockEndpoint.getExchanges().size() == 1);
		successMockEndpoint.assertIsSatisfied();
		businessExceptionMockEndpoint.assertIsSatisfied();
		unexpectedExceptionMockEndpoint.assertIsSatisfied();
	}
	
	@Test
	public void invalidJsonSchemaValidatorTest() throws Exception {
		// Expectations
		successMockEndpoint.expectedMessageCount(0);
		businessExceptionMockEndpoint.expectedMessageCount(1);
		unexpectedExceptionMockEndpoint.expectedMessageCount(0);
		
		// Test the json-schema-validator component
		producerTemplate.sendBody("direct:test-json-schema-validator", readFile(INVALID_JSON_DATA));
		
		// Assert expectations
		assertTrue("There should be at least 1 Camel Exchange in failureMockEndpoint!", businessExceptionMockEndpoint.getExchanges().size() == 1);
		successMockEndpoint.assertIsSatisfied();
		businessExceptionMockEndpoint.assertIsSatisfied();
		unexpectedExceptionMockEndpoint.assertIsSatisfied();
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
