package com.github.jeannyil.fuse.common.beans;

import org.springframework.stereotype.Component;

import com.github.jeannyil.fuse.common.models.ValidationResult;
import com.github.jeannyil.fuse.common.models.ValidationResult_;

/**
 * 
 * Error Response helper bean 
 *
 */
@Component(value = "validationResultHelper")
public class ValidationResultHelper {
	
	/**
	 * Generates a successful ValidationResult
	 * @return successful ValidationResult
	 */
	public ValidationResult generateOKValidationResult() {
		ValidationResult validationResult = new ValidationResult();
		ValidationResult_ validationAttributes = new ValidationResult_();
		validationAttributes.setStatus("OK");
		validationResult.setValidationResult(validationAttributes);
		return validationResult;
	}
	
	/**
	 * Generates a KO ValidationResult
	 * @param errorMessage
	 * @return KO ValidationResult
	 */
	public ValidationResult generateKOValidationResult(String errorMessage) {
		ValidationResult validationResult = new ValidationResult();
		ValidationResult_ validationAttributes = new ValidationResult_();
		validationAttributes.setStatus("KO");
		validationAttributes.setErrorMessage(errorMessage);
		validationResult.setValidationResult(validationAttributes);
		return validationResult;
	}

}
