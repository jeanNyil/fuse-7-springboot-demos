package com.github.jeannyil.fuse.common.beans.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.github.jeannyil.fuse.common.beans.MDCHelper;

public class MDCHelperTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(MDCHelperTest.class);
	
	private static final String TEST_MDC_KEY = "fieldName";
	private static final String TEST_MDC_VALUE = "UnitTest";

	@Test
	public void testAddToMDC() throws Exception {
		
		// Test
		new MDCHelper().addToMDC(TEST_MDC_KEY, TEST_MDC_VALUE);
		LOG.info(">>> UNIT TEST - Added [key, value] = [{}, {}] to MDC Context", 
				TEST_MDC_KEY, TEST_MDC_VALUE);
		
		// Assertion
		assertEquals(TEST_MDC_VALUE, MDC.get(TEST_MDC_KEY));

	}

}
