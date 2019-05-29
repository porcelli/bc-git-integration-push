package com.aliction.git.remote.integration;

import com.aliction.git.properties.GitRemoteProperties;
import com.aliction.git.remote.exceptions.GroupNotFoundException;

public class GitIntegration {

    public static GitRemoteIntegration getIntegration(GitRemoteProperties properties) throws GroupNotFoundException {
        GitRemoteIntegration integration = null;

        switch (properties.getGitProvider()) {
            case GitHub:
                integration = new GitHubIntegration(properties);
                break;
            case GitLab:
                integration = new GitLabIntegration(properties);
                break;
            case BitBucket:
                System.out.println("Support for BitBucket is still under development");
                break;
            default:
                System.out.println("GitHub and GitLab are the only current supported providers");
                break;
        }
        return integration;

    }

}
