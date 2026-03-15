package com.omprakash.github_access_report.config;


import com.omprakash.github_access_report.exception.CustomErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

@Target(METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({

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
public @interface ApiErrorResponses {
}
