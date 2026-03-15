package com.omprakash.github_access_report.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class responsible for creating
 * a RestTemplate bean used for calling external APIs.
 *
 * In production systems it is very important to configure
 * timeout settings when communicating with external services.
 *
 * Without timeouts:
 * If GitHub API becomes slow or unresponsive,
 * the application could hang indefinitely.
 *
 * Therefore we configure:
 *
 * 1. Connection Timeout → Maximum time allowed to establish connection
 * 2. Read Timeout → Maximum time allowed to wait for response
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates a RestTemplate bean with timeout configuration.
     *
     * This bean will be injected wherever RestTemplate
     * is required (for example in GithubApiClient).
     *
     * @return configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {

        // Request factory used to configure HTTP settings
        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        /**
         * Connection timeout
         *
         * Maximum time (milliseconds) to establish a connection
         * with the GitHub API server.
         *
         * Example:
         * If connection is not established within 5 seconds,
         * the request will fail.
         */
        requestFactory.setConnectTimeout(5000);

        /**
         * Read timeout
         *
         * Maximum time (milliseconds) to wait for the response
         * after the connection has been established.
         *
         * If GitHub takes longer than this time to respond,
         * the request will be aborted.
         */
        requestFactory.setReadTimeout(15000);

        // Create RestTemplate using the configured request factory
        return new RestTemplate(requestFactory);
    }
}
