package com.redhat.gpe.training.fuse;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration parameters filled in from application.yml and overridden using env variables, configmaps or
 * secrets on Openshift.
 */
@Configuration
@ConfigurationProperties(prefix = "timer")
public class TimerConfiguration {

	
	Integer periodInMs;

	public Integer getPeriodInMs() {
		return periodInMs;
	}

	public void setPeriodInMs(Integer periodInMs) {
		this.periodInMs = periodInMs;
	}
	
}
