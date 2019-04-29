package com.aliction.git.remote.integration;

public interface GitRemoteIntegration {

    public String createRepository(String repoName);

    public String deleteRepository(String repoName);

}
