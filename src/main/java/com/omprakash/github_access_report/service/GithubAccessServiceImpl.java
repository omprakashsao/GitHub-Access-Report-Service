package com.omprakash.github_access_report.service;

import com.omprakash.github_access_report.client.GithubApiClient;
import com.omprakash.github_access_report.dto.RepositoryAccessDTO;
import com.omprakash.github_access_report.exception.GithubApiException;
import com.omprakash.github_access_report.model.AccessReport;
import com.omprakash.github_access_report.model.RepositoryInfo;
import com.omprakash.github_access_report.model.UserInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of GithubAccessService.
 *
 * This class contains the core business logic
 * responsible for generating the GitHub access report.
 *
 * Responsibilities:
 * 1. Fetch organization repositories
 * 2. Fetch collaborators for each repository
 * 3. Aggregate data into user -> repositories mapping
 */
@Service
public class GithubAccessServiceImpl implements GithubAccessService {

    private static final int RESTRICTION_CHECK_LIMIT = 5;


    private static final Logger log =
            LoggerFactory.getLogger(GithubAccessServiceImpl.class);

    /**
     * Client responsible for calling GitHub APIs.
     */
    @Autowired
    private GithubApiClient githubApiClient;


    /**
     * Generates a GitHub access report for a given organization.
     *
     * @param organization GitHub organization name
     * @return aggregated AccessReport
     *
     * This result is cached so repeated requests
     * do not repeatedly call GitHub APIs.
     */
    @Override
    @Cacheable(value = "githubAccessReport", key = "#organization")
    public AccessReport generateAccessReport(String organization) {

        log.info("Generating access report for organization: {}", organization);


        // Step 1: Fetch repositories from GitHub
        List<RepositoryInfo> repositories =
                githubApiClient.getOrganizationRepositories(organization);

        log.info("Total repositories fetched: {}", repositories.size());




        /**
         * Using ConcurrentHashMap to support parallel processing.
         *
         * Structure:
         * username -> list of repositories
         */
        Map<String, List<RepositoryAccessDTO>> userRepoMap = new ConcurrentHashMap<>();

        // Counter to track repositories where collaborator data
        // cannot be accessed due to permission restrictions.
        AtomicInteger restrictedRepositoryCount = new AtomicInteger(0);

        /**
         * Step 2:
         * Process repositories in parallel to improve performance.
         *
         * This helps when organizations have:
         * - 100+ repositories
         * - many collaborators
         */


        ExecutorService executor = Executors.newFixedThreadPool(10);

        try {

            List<Future<?>> futures = new ArrayList<>();

            for (RepositoryInfo repo : repositories) {

                futures.add(executor.submit(() -> {

                    try {

                        log.info("Fetching collaborators for repository: {}", repo.getName());

                        List<UserInfo> collaborators =
                                githubApiClient.getRepositoryCollaborators(organization, repo.getName());

                        for (UserInfo user : collaborators) {

                            String permission = determinePermission(user.getPermissions());

                            userRepoMap
                                    .computeIfAbsent(user.getLogin(), key -> Collections.synchronizedList(new ArrayList<>()))
                                    .add(new RepositoryAccessDTO(repo.getName(), permission));

                        }

                    } catch (GithubApiException ex) {

                        log.warn("Skipping repository '{}' due to permission restriction", repo.getName());

                        restrictedRepositoryCount.incrementAndGet();
                    } catch (Exception ex) {

                        log.warn("Skipping repository '{}' due to error", repo.getName());
                    }

                }));
            }

            // Wait for all tasks to finish
            for (Future<?> future : futures) {

                try {
                    future.get();
                }
                catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    log.error("Thread interrupted while waiting for GitHub tasks", e);
                }
                catch (ExecutionException e) {

                    log.error("Error occurred during GitHub collaborator fetch", e);
                }
            }

        }
        finally {

            executor.shutdown();
        }

//        repositories.parallelStream().forEach(repo -> {
//
//            String repoName = repo.getName();
//
//            try {
//
//                log.info("Fetching collaborators for repository: {}", repoName);
//
//                List<UserInfo> collaborators =
//                        githubApiClient.getRepositoryCollaborators(
//                                organization,
//                                repoName
//                        );
//
//                /**
//                 * Step 3:
//                 * Aggregate user -> repositories mapping
//                 */
//
//
//                for (UserInfo user : collaborators) {
//
//                    String username = user.getLogin();
//
//                    userRepoMap
//                            .computeIfAbsent(username,
//                                    key -> Collections.synchronizedList(new ArrayList<>()))
//                            .add(repoName);
//                }
//
//            } catch (GithubApiException ex) {
//
//                log.warn("Skipping repository '{}' due to permission restriction", repoName);
//
//                restrictedRepositories.add(repoName);
//            }catch (Exception ex) {
//
//                log.error("Failed processing repository: {}", repoName, ex);
//
//                // continue processing other repositories
//            }
//
//        });



        log.info("Access report generation completed.");

        // Step 4: Build final response
        return new AccessReport(
                organization,
                userRepoMap,
                restrictedRepositoryCount.get()
        );
    }

    private String determinePermission(Map<String, Boolean> permissions) {

        if (permissions == null) return "unknown";

        if (Boolean.TRUE.equals(permissions.get("admin"))) {
            return "admin";
        }
        if (Boolean.TRUE.equals(permissions.get("push"))) {
            return "push";
        }
        if (Boolean.TRUE.equals(permissions.get("pull"))) {
            return "pull";
        }

        return "unknown";
    }

}
