package com.aliction.git.remote.integration;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.Project;

import com.aliction.git.properties.GitRemoteProperties;
import com.aliction.git.remote.exceptions.GroupNotFoundException;

public class GitLabIntegration implements GitRemoteIntegration {

    GitLabApi gitlab;
    String user;
    Project gitlabProject;
    GitRemoteProperties props;
    Boolean usingToken;
    CredentialsProvider credentialsProvider;
    String remoteURL = null;
    Integer groupId = -1;

    public GitLabIntegration() {
        // TODO Auto-generated constructor stub

    }

    public GitLabIntegration(GitRemoteProperties properties) throws GroupNotFoundException {
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
            groupId = getGroupId(props.getGroup());
        } catch (GitLabApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Integer getGroupId(String groupPath) throws GroupNotFoundException {

        if (!groupPath.isEmpty()) {
            try {
                Group group = gitlab.getGroupApi().getGroup(groupPath);
                groupId = group.getId();
                System.out.println("Saving to GitLab Group " + group.getFullPath());
            } catch (GitLabApiException e) {
                // TODO Auto-generated catch block
                throw new GroupNotFoundException("Group \"" + groupPath + "\" is not found");
            }
        }
        return groupId;
    }

    @Override
    public String createRepository(String repoName) {
        try {
            if (groupId >= 0) {
                gitlabProject = gitlab.getProjectApi().createProject(groupId, repoName);
            } else {
                gitlabProject = gitlab.getProjectApi().createProject(repoName);
            }
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
            if (groupId >= 0) {
                gitlabProject = gitlab.getProjectApi().getProject(props.getGroup(), repoName);
            } else {
                gitlabProject = gitlab.getProjectApi().getProject(user, repoName);
                //            gitlabProject = gitlab.getProjectApi().getProject(user + "/" + repoName);
            }
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
