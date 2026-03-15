package com.omprakash.github_access_report.service;

import com.omprakash.github_access_report.model.AccessReport;

/**
 * Service interface defining business operations
 * related to GitHub repository access reporting.
 *
 * Controller interacts with this interface,
 * while the actual logic will be implemented
 * in GithubAccessServiceImpl.
 */
public interface GithubAccessService {

    /**
     * Generates an access report for a GitHub organization.
     *
     * The report contains a mapping of:
     *
     * user -> list of repositories they can access
     *
     * Example:
     *
     * {
     *   "alice": ["repo1", "repo2"],
     *   "bob": ["repo3"]
     * }
     *
     * @param organization GitHub organization name
     * @return AccessReport containing aggregated user access details
     */
    AccessReport generateAccessReport(String organization);

}
