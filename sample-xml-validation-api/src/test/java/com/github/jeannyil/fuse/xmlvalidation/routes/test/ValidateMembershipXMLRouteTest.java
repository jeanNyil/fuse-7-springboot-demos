package com.github.jeannyil.fuse.xmlvalidation.routes.test;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.github.jeannyil.fuse.xmlvalidation.Application;

/**
 * ValidateMembershipRoute Test class
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {Application.class})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@DisableJmx(value=true)
@UseAdviceWith(value=true)
public class ValidateMembershipXMLRouteTest {

	@Autowired
	private CamelContext camelContext;
	
	@Autowired
	private ProducerTemplate producerTemplate;
	
	@EndpointInject(uri="mock:success")
	private MockEndpoint successMockEndpoint;
	
	@EndpointInject(uri="mock:validationException")
	private MockEndpoint validationExceptionMockEndpoint;
	
	@EndpointInject(uri="mock:unexpectedException")
	private MockEndpoint unexpectedExceptionMockEndpoint;
	
	private final static String VALID_MEMBERSHIP_XML = "src/test/resources/data/valid_membership.xml";
	private final static String INVALID_MEMBERSHIP_XML = "src/test/resources/data/invalid_membership.xml";
	
	private final static String EXPECTED_OK_VALIDATIONRESULT = "src/test/resources/data/expectedOKValidationResult.json";
	
	@Before
	public void setup() throws Exception {
		RouteDefinition validateMembershipXMLRoute = camelContext.getRouteDefinition("validate-membership-xml-route");
		
		validateMembershipXMLRoute.adviceWith(camelContext, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				// select the route node with the id=log-validateMembership-KO-response
                // and then add the following route parts afterwards
                weaveById("log-validateMembershipXML-KO-response").after()
                	.to("mock:validationException")
            	;
                
                // select the route node with the id=to-validateMembership-500
                // and then add the following route parts afterwards
                weaveById("to-validateMembershipXML-500").after()
                	.to("mock:unexpectedException")
            	;
				
				// add at the end of the route to route to this mock endpoint
                weaveAddLast().to("mock:success");
			}
		});
		
		successMockEndpoint.reset();
		validationExceptionMockEndpoint.reset();
		unexpectedExceptionMockEndpoint.reset();
		
		// Start the Camel context
		camelContext.setTracing(true);
		camelContext.start();
	}
	
	@Test
	public void validXmlValidatorTest() throws Exception {
		// Expectations
		successMockEndpoint.expectedMessageCount(1);
		validationExceptionMockEndpoint.expectedMessageCount(0);
		unexpectedExceptionMockEndpoint.expectedMessageCount(0);
		
		// Test the ValidateMembershipRoute
		String validationResult = producerTemplate.requestBody("direct:validateMembershipXML", readFile(VALID_MEMBERSHIP_XML), String.class);
		
		// Assert expectations
		assertTrue("There should be at least 1 Camel Exchange in successMockEndpoint!", successMockEndpoint.getExchanges().size() == 1);
		successMockEndpoint.assertIsSatisfied();
		validationExceptionMockEndpoint.assertIsSatisfied();
		unexpectedExceptionMockEndpoint.assertIsSatisfied();
		JSONAssert.assertEquals(readFile(EXPECTED_OK_VALIDATIONRESULT), validationResult, true);
	}
	
	@Test
	public void invalidXmlValidatorTest() throws Exception {
		// Expectations
		successMockEndpoint.expectedMessageCount(0);
		validationExceptionMockEndpoint.expectedMessageCount(1);
		unexpectedExceptionMockEndpoint.expectedMessageCount(0);
		
		// Test the ValidateMembershipRoute
		producerTemplate.sendBody("direct:validateMembershipXML", readFile(INVALID_MEMBERSHIP_XML));
		
		// Assert expectations
		assertTrue("There should be at least 1 Camel Exchange in failureMockEndpoint!", validationExceptionMockEndpoint.getExchanges().size() == 1);
		successMockEndpoint.assertIsSatisfied();
		validationExceptionMockEndpoint.assertIsSatisfied();
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
