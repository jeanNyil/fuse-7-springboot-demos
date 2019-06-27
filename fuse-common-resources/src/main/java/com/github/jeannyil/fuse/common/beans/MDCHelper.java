package com.github.jeannyil.fuse.common.beans;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * 
 * MDC logging helper 
 *
 */
@Component(value = "mdcHelper")
public class MDCHelper {

	/**
	 * Adds a key, value pair to the current MDC context
	 * @param mdcKey
	 * @param mdcKeyValue
	 */
	public void addToMDC(String mdcKey, String mdcKeyValue) throws Exception {
		MDC.put(mdcKey, mdcKeyValue);
	}

}
