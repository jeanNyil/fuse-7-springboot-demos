package com.github.jeannyil.fuse.common.beans.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.github.jeannyil.fuse.common.beans.ErrorResponseHelper;
import com.github.jeannyil.fuse.common.models.ErrorResponse;

public class ErrorResponseHelperTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(ErrorResponseHelperTest.class);
	
	private final static String TEST_ERROR_ID = String.valueOf(HttpStatus.BAD_REQUEST.value());
	private final static String TEST_ERROR_DESCRIPTION = HttpStatus.BAD_REQUEST.getReasonPhrase();
	private final static String TEST_ERROR_MESSAGE = "Test error message";

	@Test
	public void testGenerateErrorResponse() {
		
		// Set test expectation
		int expectedMessageListSize = 1;
		
		// Test
		LOG.info(">>> UNIT TEST - testGenerateErrorResponse - id[{}], description[{}] and 1 message[{}]",
				TEST_ERROR_ID, TEST_ERROR_DESCRIPTION, TEST_ERROR_MESSAGE);
		ErrorResponse actualErrorResponse = new ErrorResponseHelper()
				.generateErrorResponse(TEST_ERROR_ID, TEST_ERROR_DESCRIPTION, TEST_ERROR_MESSAGE);
		
		// Assertions
		assertTrue(actualErrorResponse != null);
		
		LOG.info(">>> UNIT TEST - testGenerateErrorResponse - final error messages list: {}", 
				actualErrorResponse.getError().getMessages());
		
		assertTrue(actualErrorResponse.getError().getMessages().size() == expectedMessageListSize);
		assertEquals(TEST_ERROR_ID, actualErrorResponse.getError().getId());
		assertEquals(TEST_ERROR_DESCRIPTION, actualErrorResponse.getError().getDescription());
		assertEquals(TEST_ERROR_MESSAGE, actualErrorResponse.getError().getMessages().get(0));
		
	}

}
