package com.aliction.git.remote.integration;

import java.io.IOException;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import com.aliction.git.properties.GitRemoteProperties;

public class GitHubIntegration implements GitRemoteIntegration {


    GitHub github;
    String user;
	GHRepository repo = null;

	public GitHubIntegration() {
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings("deprecation")
	public GitHubIntegration(GitRemoteProperties props) {
		// TODO Auto-generated constructor stub

		try {
			if(props.getToken().isEmpty()) {
				System.out.println("Unsecured connection using username and password since token is not provided in properties file");
				github = GitHub.connectToEnterprise(props.getRemoteGitUrl(), props.getLogin(), props.getPassword());
			}else {
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
		String url = null;
	try {
        repo = github.createRepository(repoName)
		          .description("Created from Business Central: " + repoName)
		          .autoInit(false)
		          .create();
		if(user.isEmpty()) {
			user=repo.getOwnerName();
		}
	    url= repo.getHttpTransportUrl();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

		return url;
	}
	
	public String deleteRepository(String repoName) {
		String url=null;
		try {
			repo = github.getRepository(user + "/" + repoName);
			url = repo.getHttpTransportUrl();
			repo.delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		
		return url;
		
	}

}
