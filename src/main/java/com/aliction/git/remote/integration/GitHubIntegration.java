package com.aliction.git.remote.integration;

import java.io.IOException;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import com.aliction.git.properties.GitRemoteProperties;

public class GitHubIntegration implements GitRemoteIntegration {

    GitHub github;
    String user;
    GHRepository repo = null;
    GitRemoteProperties props;
    Boolean usingToken;
    CredentialsProvider credentialsProvider;
    String remoteURL = null;

    public GitHubIntegration() {
        // TODO Auto-generated constructor stub
    }

    @SuppressWarnings("deprecation")
    public GitHubIntegration(GitRemoteProperties properties) {
        // TODO Auto-generated constructor stub
        props = properties;
        try {
            if (props.getToken().isEmpty()) {
                usingToken = false;
                System.out.println("Unsecured connection using username and password since token is not provided in properties file");
                github = GitHub.connectToEnterprise(props.getRemoteGitUrl(), props.getLogin(), props.getPassword());
            } else {
                usingToken = true;
                System.out.println("Connecting using secured token from properties file");
                github = GitHub.connectUsingOAuth(props.getRemoteGitUrl(), props.getToken());
                //				.connectToEnterpriseWithOAuth(props.getRemoteGitUrl(), props.getLogin(), props.getToken());//This also works regardless of the provided login, user info is in the token anyway
            }
            //			github = GitHub.connect();
            user = github.getMyself().getLogin();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String createRepository(String repoName) {
        // TODO Auto-generated method stub
        try {
            repo = github.createRepository(repoName)
                         .description("Created from Business Central: " + repoName)
                         .autoInit(false)
                         .create();
            if (user.isEmpty()) {
                user = repo.getOwnerName();
            }
            remoteURL = repo.getHttpTransportUrl();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return remoteURL;
    }

    public String deleteRepository(String repoName) {
        try {
            repo = github.getRepository(user + "/" + repoName);
            remoteURL = repo.getHttpTransportUrl();
            repo.delete();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //		
        return remoteURL;

    }

    @Override
    public CredentialsProvider getCredentialsProvider() {
        // TODO Auto-generated method stub
        if (usingToken) {
            credentialsProvider = new UsernamePasswordCredentialsProvider(props.getToken(), "");
        } else {
            credentialsProvider = new UsernamePasswordCredentialsProvider(props.getLogin(), props.getPassword());
        }
        return credentialsProvider;
    }

}
