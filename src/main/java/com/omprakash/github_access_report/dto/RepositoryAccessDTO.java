package com.omprakash.github_access_report.dto;

public class RepositoryAccessDTO {

    private String repository;
    private String permission;

    public RepositoryAccessDTO(String repository, String permission) {
        this.repository = repository;
        this.permission = permission;
    }

    public String getRepository() {
        return repository;
    }

    public String getPermission() {
        return permission;
    }

}

