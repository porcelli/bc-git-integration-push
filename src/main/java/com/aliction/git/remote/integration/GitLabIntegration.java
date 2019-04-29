package com.aliction.git.remote.integration;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;

import com.aliction.git.properties.GitRemoteProperties;

public class GitLabIntegration implements GitRemoteIntegration {

    GitLabApi gitlab;
    String user;
    Project gitlabProject;

    public GitLabIntegration() {
        // TODO Auto-generated constructor stub

    }

    public GitLabIntegration(GitRemoteProperties props) {

        if (props.getToken().isEmpty()) {
            System.out.println("Connecting using username and password is not supported with gitlab, kindly use token instead.");
            return;
            //gitlab = new GitLabApi(props.getRemoteGitUrl(), props.getLogin(), props.getPassword());
            //gitlab4j only support token authentication .. user/password is not supported
        } else {
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
        String url = "";
        try {
            gitlabProject = gitlab.getProjectApi().createProject(repoName);
        } catch (GitLabApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        url = gitlabProject.getWebUrl();
        return url;
    }

    @Override
    public String deleteRepository(String repoName) {
        String url = null;
        try {
            gitlabProject = gitlab.getProjectApi().getProject(user + "/" + repoName);
            url = gitlabProject.getWebUrl();
            gitlab.getProjectApi().deleteProject(gitlabProject);
        } catch (GitLabApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return url;
    }

}
