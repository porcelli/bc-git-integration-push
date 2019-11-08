package porcelli.me.git.integration.githook.push.integration;

import porcelli.me.git.integration.githook.push.properties.GitRemoteProperties;

public enum GitProvider {
    GIT_HUB {
        public GitRemoteIntegration getRemoteIntegration(GitRemoteProperties prop) {
            return new GitHubIntegration(prop);
        }
    },
    GIT_LAB {
        public GitRemoteIntegration getRemoteIntegration(GitRemoteProperties prop) {
            return new GitLabIntegration(prop);
        }
    },
    BIT_BUCKET {
        public GitRemoteIntegration getRemoteIntegration(GitRemoteProperties prop) {
            return new BitBucketIntegration(prop);
        }
    };

    public abstract GitRemoteIntegration getRemoteIntegration(GitRemoteProperties prop);
}
