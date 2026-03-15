package com.omprakash.github_access_report.model;

import com.omprakash.github_access_report.dto.RepositoryAccessDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Model class representing the final GitHub access report.
 *
 * This object will be returned by our API endpoint
 * and contains the aggregated mapping of users
 * to the repositories they can access.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessReport {

    /**
     * Name of the GitHub organization.
     *
     * Example:
     * google
     * microsoft
     * spring-projects
     */
    private String organization;



    /**
     * Mapping of user -> list of repositories they can access.
     *
     * Example:
     *
     * {
     *   "alice": ["repo1","repo2"],
     *   "bob": ["repo3"]
     * }
     */
    private Map<String, List<RepositoryAccessDTO>> accessReport;


    /**
     * Number of repositories where collaborator information
     *
     * could not be retrieved due to permission restrictions.
     */
     private int restrictedRepositoryCount;



}
