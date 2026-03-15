package com.omprakash.github_access_report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;


/**
 * Main Spring Boot application class.
 *
 * @EnableRetry   → enables retry mechanism for external API calls
 * @EnableCaching → enables caching support
 */
@SpringBootApplication
@EnableRetry
@EnableCaching
public class GithubAccessReportApplication {

	public static void main(String[] args) {
		SpringApplication.run(GithubAccessReportApplication.class, args);
	}

}
