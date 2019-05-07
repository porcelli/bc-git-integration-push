package com.aliction.git.remote.integration;

import org.eclipse.jgit.transport.CredentialsProvider;

public interface GitRemoteIntegration {

    public String createRepository(String repoName);

    public String deleteRepository(String repoName);

    public CredentialsProvider getCredentialsProvider();

}
