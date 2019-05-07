package com.aliction.git.remote.integration;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;

import com.aliction.git.properties.GitRemoteProperties;

public class GitLabIntegration implements GitRemoteIntegration {

    GitLabApi gitlab;
    String user;
    Project gitlabProject;
    GitRemoteProperties props;
    Boolean usingToken;
    CredentialsProvider credentialsProvider;
    String remoteURL = null;

    public GitLabIntegration() {
        // TODO Auto-generated constructor stub

    }

    public GitLabIntegration(GitRemoteProperties properties) {
        props = properties;
        if (props.getToken().isEmpty()) {
            usingToken = false;
            System.out.println("Connecting using username and password is not supported with gitlab, kindly use token instead.");
            return;
            //gitlab = new GitLabApi(props.getRemoteGitUrl(), props.getLogin(), props.getPassword());
            //gitlab4j only support token authentication .. user/password is not supported
        } else {
            usingToken = true;
            System.out.println("Connecting using token");
            gitlab = new GitLabApi(props.getRemoteGitUrl(), props.getToken());
        }
        try {
            user = gitlab.getUserApi().getCurrentUser().getUsername();
        } catch (GitLabApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String createProject(String projectName, GitRemoteProperties props) {
        return "";
    }

    @Override
    public String createRepository(String repoName) {
        try {
            gitlabProject = gitlab.getProjectApi().createProject(repoName);
        } catch (GitLabApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        remoteURL = gitlabProject.getHttpUrlToRepo();
        return remoteURL;
    }

    @Override
    public String deleteRepository(String repoName) {
        try {
            gitlabProject = gitlab.getProjectApi().getProject(user + "/" + repoName);
            remoteURL = gitlabProject.getHttpUrlToRepo();
            gitlab.getProjectApi().deleteProject(gitlabProject);
        } catch (GitLabApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return remoteURL;
    }

    @Override
    public CredentialsProvider getCredentialsProvider() {
        // TODO Auto-generated method stub
        if (usingToken) {
            credentialsProvider = new UsernamePasswordCredentialsProvider(props.getLogin(), props.getToken());
        } else {
            credentialsProvider = new UsernamePasswordCredentialsProvider(props.getLogin(), props.getPassword());
        }
        return credentialsProvider;
    }


}
