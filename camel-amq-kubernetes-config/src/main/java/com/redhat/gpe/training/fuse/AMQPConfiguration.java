package com.redhat.gpe.training.fuse;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration parameters filled in from application.yml and overridden using env variables, configmaps or
 * secrets on OpenShift.
 */
@Configuration
@ConfigurationProperties(prefix = "amqp")
public class AMQPConfiguration {

    /**
     * AMQ service host
     */
    private String host;

    /**
     * AMQ service port
     */
    private Integer port;

    /**
     * AMQ username
     */
    private String username;

    /**
     * AMQ password
     */
    private String password;

    /**
     * Pool max connections
     */
    private Integer maxConnections;

    /**
     * Number of max active sessions per connection in the pool
     */
    private Integer maxActiveSessionsPerConnection;

    public AMQPConfiguration() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the maxConnections
     */
    public Integer getMaxConnections() {
        return maxConnections;
    }

    /**
     * @param maxConnections the maxConnections to set
     */
    public void setMaxConnections(Integer maxConnections) {
        this.maxConnections = maxConnections;
    }

    /**
     * @return the maxActiveSessionsPerConnection
     */
    public Integer getMaxActiveSessionsPerConnection() {
        return maxActiveSessionsPerConnection;
    }

    /**
     * @param maxActiveSessionsPerConnection the maxActiveSessionsPerConnection to
     *                                       set
     */
    public void setMaxActiveSessionsPerConnection(Integer maxActiveSessionsPerConnection) {
        this.maxActiveSessionsPerConnection = maxActiveSessionsPerConnection;
    }

}
