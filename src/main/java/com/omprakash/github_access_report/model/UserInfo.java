package com.omprakash.github_access_report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Model class representing a GitHub User.
 *
 * This class maps the user information returned
 * from the GitHub API endpoint:
 *
 * GET /repos/{org}/{repo}/collaborators
 *
 * Only the required fields are included.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    /**
     * GitHub username (login).
     *
     * Example:
     * octocat
     * torvalds
     * spring-projects
     */
    private String login;


    private Map<String, Boolean> permissions;


}
