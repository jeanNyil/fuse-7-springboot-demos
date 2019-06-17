package com.github.jeannyil.fuse.jsonvalidation.beans;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.github.jeannyil.fuse.jsonvalidation.models.Error;
import com.github.jeannyil.fuse.jsonvalidation.models.ErrorResponse;

/**
 * 
 * Error Response helper bean 
 *
 */
@Component(value = "errorResponseHelper")
public class ErrorResponseHelper {
	
	/**
	 * Generates an ErrorResponse object
	 * @param id
	 * @param description
	 * @param message
	 * @return ErrorResponse
	 */
	public ErrorResponse generateErrorResponse(String id, String description, String message) {
		ArrayList<String> messages = new ArrayList<String>(0);
		if (message != null) {
			messages.add(message);
		}
		Error error = new Error(id, description, messages);
		return new ErrorResponse(error);
	}

}
