
package com.github.jeannyil.fuse.common.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Root Type for error
 * <p>
 * Error response
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "error"
})
public class ErrorResponse {

    @JsonProperty("error")
    private Error error;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ErrorResponse() {
    }

    /**
     * 
     * @param error
     */
    public ErrorResponse(Error error) {
        super();
        this.error = error;
    }

    @JsonProperty("error")
    public Error getError() {
        return error;
    }

    @JsonProperty("error")
    public void setError(Error error) {
        this.error = error;
    }

}
