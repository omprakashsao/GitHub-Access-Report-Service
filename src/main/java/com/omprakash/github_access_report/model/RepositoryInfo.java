package com.omprakash.github_access_report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing a GitHub repository.
 *
 * This class maps the repository information returned
 * from the GitHub API endpoint:
 *
 * GET /orgs/{org}/repos
 *
 * We only include the fields that are required
 * for this project to keep the model simple.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryInfo {

    /**
     * Name of the repository.
     *
     * Example:
     * spring-boot
     * kubernetes
     * github-access-report
     */
    private String name;


}
