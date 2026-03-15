package com.omprakash.github_access_report.client;

import com.omprakash.github_access_report.exception.GithubApiException;
import com.omprakash.github_access_report.model.RepositoryInfo;
import com.omprakash.github_access_report.model.UserInfo;
import com.omprakash.github_access_report.util.GithubApiUtils;
import com.omprakash.github_access_report.config.GithubConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Client class responsible for communicating with GitHub REST APIs.
 *
 * This class acts as a bridge between our application
 * and the external GitHub service.
 *
 * Responsibilities:
 * - Authenticate with GitHub
 * - Fetch organization repositories
 * - Fetch repository collaborators
 */
@Component
public class GithubApiClient {

    private static final Logger log =
            LoggerFactory.getLogger(GithubApiClient.class);

    /**
     * RestTemplate used to make HTTP requests
     * to GitHub API.
     */
    @Autowired
    private RestTemplate restTemplate;

    /**
     * GitHub configuration class
     * containing token and base URL.
     */
    @Autowired
    private GithubConfig githubConfig;

    /**
     * Fetch repositories belonging to a GitHub organization.
     *
     * Example API:
     * https://api.github.com/orgs/google/repos
     *
     * This method automatically retries if GitHub temporarily fails.
     *
     * @param organization GitHub organization name
     * @return list of repositories
     */

    @Retryable(
            retryFor = {
                    ResourceAccessException.class,
                    HttpServerErrorException.class
            },
            exclude = {
                    HttpClientErrorException.Forbidden.class,
                    HttpClientErrorException.Unauthorized.class,
                    HttpClientErrorException.NotFound.class
            },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public List<RepositoryInfo> getOrganizationRepositories(String organization) {

        log.info("Fetching repositories for organization: {}", organization);

        List<RepositoryInfo> allRepositories = new ArrayList<>();

        int page = 1;
        int perPage = 100;

        while (true) {

            String url = githubConfig.getBaseUrl() +
                    "/orgs/" + organization +
                    "/repos?per_page=" + perPage +
                    "&page=" + page;

            try {

                HttpHeaders headers =
                        GithubApiUtils.createGithubHeaders(githubConfig.getToken());

                HttpEntity<Void> request = new HttpEntity<>(headers);

                ResponseEntity<RepositoryInfo[]> response =
                        restTemplate.exchange(
                                url,
                                HttpMethod.GET,
                                request,
                                RepositoryInfo[].class
                        );

                // Validate GitHub rate limit after API call
                handleRateLimit(response);

                RepositoryInfo[] repos = response.getBody();

                if (repos == null || repos.length == 0) {
                    break;
                }

                allRepositories.addAll(Arrays.asList(repos));

                page++;

            }catch (HttpClientErrorException.Unauthorized ex) {

                log.warn("GitHub authentication failed. Invalid or missing token while accessing organization: '{}'. \n Status: {}, \n Response: {}",
                        organization,
                        ex.getStatusCode(),
                        ex.getResponseBodyAsString());

                throw new GithubApiException(
                        "GitHub authentication failed. Please verify the configured GitHub access token.",
                        HttpStatus.UNAUTHORIZED
                );
            } catch (HttpClientErrorException.NotFound ex) {

                log.warn("Organization '{}' not found on GitHub. \n Status: {}, \n Response: {}",
                        organization,
                        ex.getStatusCode(),
                        ex.getResponseBodyAsString());

                throw new GithubApiException(
                        "GitHub organization '" + organization + "' does not exist or is not accessible",
                        HttpStatus.NOT_FOUND
                );
            } catch (HttpClientErrorException.TooManyRequests ex) {

                log.warn("GitHub API rate limit exceeded. \n Status: {}, \n Response: {}",
                        ex.getStatusCode(),
                        ex.getResponseBodyAsString());

                throw new GithubApiException(
                        "GitHub API rate limit exceeded. Please try again later.",
                        HttpStatus.TOO_MANY_REQUESTS
                );
            } catch (HttpServerErrorException ex) {

                log.error("GitHub API server error. \n Status: {}, \n Response: {}",
                        ex.getStatusCode(),
                        ex.getResponseBodyAsString());

                throw new GithubApiException(
                        "GitHub service is currently unavailable",
                        HttpStatus.BAD_GATEWAY
                );
            } catch (RestClientException ex) {

                log.error("Error fetching repositories from GitHub", ex);

                throw new GithubApiException(
                        "Failed to fetch repositories from GitHub: " + ex.getMessage()
                );
            }
        }

        log.info("Total repositories fetched: {}", allRepositories.size());

        return allRepositories;
    }



    /**
     * Fetch collaborators who have access to a repository.
     *
     * Example API:
     * https://api.github.com/repos/google/gvisor/collaborators
     *
     * This method automatically retries if GitHub temporarily fails.
     *
     * @param organization GitHub organization
     * @param repository repository name
     * @return list of collaborators
     */

    @Retryable(
            retryFor = {
                    ResourceAccessException.class,
                    HttpServerErrorException.class
            },
            exclude = {
                    HttpClientErrorException.Forbidden.class,
                    HttpClientErrorException.Unauthorized.class,
                    HttpClientErrorException.NotFound.class
            },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public List<UserInfo> getRepositoryCollaborators(String organization, String repository) {

        String url = githubConfig.getBaseUrl() +
                "/repos/" + organization + "/" + repository + "/collaborators?per_page=100";

        log.info("Fetching collaborators for repository: {}", repository);

        try {

            HttpHeaders headers =
                    GithubApiUtils.createGithubHeaders(githubConfig.getToken());

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<UserInfo[]> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            request,
                            UserInfo[].class
                    );

            // Validate GitHub rate limit after API call
            handleRateLimit(response);

            UserInfo[] users = response.getBody();

            if (users == null) {
                throw new GithubApiException(
                        "No collaborators found for repository: " + repository
                );
            }

            return Arrays.asList(users);

        }catch (HttpClientErrorException.Forbidden ex) {

            // Expected behavior for many public repositories
            log.warn("Skipping repository '{}' due to collaborator permission restriction. \n Status: {}, \n Response: {}",
                    repository,
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString());

            throw new GithubApiException(
                    "Access denied to view collaborators for repository '" + repository + "'",
                    HttpStatus.FORBIDDEN
            );
        } catch (HttpClientErrorException.Unauthorized ex) {

            log.warn("GitHub authentication failed. Invalid or missing token while accessing organization: '{}'. \n Status: {}, \n Response: {}",
                    organization,
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString());

            throw new GithubApiException(
                    "GitHub authentication failed. Please verify the configured GitHub access token.",
                    HttpStatus.UNAUTHORIZED
            );
        }catch (HttpClientErrorException.TooManyRequests ex) {
            log.warn("GitHub API rate limit exceeded. \n Status: {}, \n Response: {}",
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString());
            throw new GithubApiException(
                    "GitHub API rate limit exceeded. Please try again later.",
                    HttpStatus.TOO_MANY_REQUESTS
            );
        } catch (HttpServerErrorException ex) {

            log.error("GitHub API server error. \n Status: {}, \n Response: {}",
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString());

            throw new GithubApiException(
                    "GitHub service is currently unavailable.",
                    HttpStatus.BAD_GATEWAY
            );
        } catch (RestClientException ex) {

            log.error("Error fetching collaborators from GitHub", ex);

            throw new GithubApiException(
                    "Failed to fetch collaborators from GitHub: " + ex.getMessage()
            );
        }
    }

    /**
     * Checks GitHub API rate limit after every API call.
     *
     * GitHub provides rate limit information in response headers.
     *
     * Important header:
     * X-RateLimit-Remaining
     *
     * Example:
     * X-RateLimit-Remaining: 4999
     *
     * If the remaining requests become 0,
     * GitHub will block further API calls until the limit resets.
     *
     * This method reads the header and throws a custom exception
     * if the rate limit has been exceeded.
     *
     * @param response HTTP response returned from GitHub API
     */
    private void handleRateLimit(ResponseEntity<?> response) {

        // Read the header that contains remaining request count
        List<String> remainingHeader =
                response.getHeaders().get("X-RateLimit-Remaining");

        // If the header exists, process it
        if (remainingHeader != null && !remainingHeader.isEmpty()) {

            // Convert the header value to integer
            int remainingRequests = Integer.parseInt(remainingHeader.get(0));

            // If remaining requests are 0, GitHub has blocked further calls
            if (remainingRequests == 0) {

                log.error("GitHub API rate limit exceeded");

                // Throw custom exception so the system can respond properly
                throw new GithubApiException(
                        "GitHub API rate limit exceeded. Please try again later.",
                        HttpStatus.TOO_MANY_REQUESTS
                );
            }
        }
    }

}
