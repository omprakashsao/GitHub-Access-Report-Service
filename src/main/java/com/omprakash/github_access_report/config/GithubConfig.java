package com.omprakash.github_access_report.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for GitHub API settings.
 *
 * This class reads configuration values from
 * application.properties using the prefix "github".
 *
 * Example configuration:
 *
 * github.token=your_token
 * github.base-url=https://api.github.com
 */
@Configuration
@ConfigurationProperties(prefix = "github")
public class GithubConfig {

    /**
     * GitHub Personal Access Token used for authentication.
     */
    private String token;

    /**
     * Base URL of GitHub API.
     * Default value: https://api.github.com
     */
    private String baseUrl;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
