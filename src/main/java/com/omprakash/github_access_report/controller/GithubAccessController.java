package com.omprakash.github_access_report.controller;

import com.omprakash.github_access_report.exception.CustomErrorResponse;
import com.omprakash.github_access_report.exception.GithubApiException;
import com.omprakash.github_access_report.model.AccessReport;
import com.omprakash.github_access_report.service.GithubAccessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller responsible for exposing API endpoints
 * related to GitHub repository access reporting.
 *
 * Base URL: /v1/api/github
 */
@RestController
@RequestMapping("/v1/api/github")
public class GithubAccessController {

    // Service layer that contains business logic
    @Autowired
    private GithubAccessService githubAccessService;

    /**
     * API endpoint to generate access report for a GitHub organization.
     *
     * Example request:
     * GET /v1/api/github/access-report?org=google
     *
     * @param org GitHub organization name
     * @return AccessReport containing user -> repositories mapping
     */
    @Operation(summary = "Generate GitHub repository access report for an organization")
    @ApiResponses(value = {

            @ApiResponse(
                    responseCode = "200",
                    description = "Access report generated successfully",
                    content = @Content(mediaType = "application/json")
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request: Organization parameter is missing or invalid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized: GitHub authentication failed. Please verify the configured GitHub access token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden: Access denied to view collaborators for one or more repositories",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found: GitHub organization does not exist or is not accessible",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            ),

            @ApiResponse(
                    responseCode = "429",
                    description = "Too Many Requests: GitHub API rate limit exceeded",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            ),

            @ApiResponse(
                    responseCode = "502",
                    description = "Bad Gateway: GitHub service is unavailable",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error: Unexpected error occurred",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class)
                    )
            )
    })
    @GetMapping("/access-report")
    public ResponseEntity<AccessReport> getAccessReport(
            @RequestParam String org
    ) {

        if (org == null || org.trim().isEmpty()) {
            throw new GithubApiException(
                    "Organization parameter 'org' must not be empty",
                    HttpStatus.BAD_REQUEST
            );
        }
        AccessReport report =
                githubAccessService.generateAccessReport(org);

        return ResponseEntity.ok(report);
    }
}
