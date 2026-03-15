package com.omprakash.github_access_report.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * Utility class containing helper methods for
 * interacting with the GitHub API.
 *
 * This class centralizes common operations such as:
 * - Creating authenticated request headers
 * - Standardizing GitHub API requests
 */
public final class GithubApiUtils {

    /**
     * Private constructor to prevent instantiation.
     * Utility classes should not be instantiated.
     */
    private GithubApiUtils() {
    }

    /**
     * Creates HTTP headers required for GitHub API requests.
     *
     * This includes:
     * - Authorization header (Bearer token)
     * - Accept header for JSON responses
     *
     * @param token GitHub Personal Access Token
     * @return configured HttpHeaders object
     */
    public static HttpHeaders createGithubHeaders(String token) {

        HttpHeaders headers = new HttpHeaders();

        // Add GitHub authentication token
        headers.setBearerAuth(token);

        // Ensure response is JSON
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(MediaType.parseMediaTypes("application/json"));

        return headers;
    }
}
