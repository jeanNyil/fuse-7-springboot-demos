package com.redhat.gpe.training.fuse;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration parameters filled in from application.yml and overridden using env variables, configmaps or
 * secrets on Openshift.
 */
@Configuration
@ConfigurationProperties(prefix = "hello")
public class HelloConfiguration {
    
    private String message;

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

}