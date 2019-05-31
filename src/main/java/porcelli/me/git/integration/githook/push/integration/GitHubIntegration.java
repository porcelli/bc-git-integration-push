package porcelli.me.git.integration.githook.push.integration;

import java.io.IOException;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import porcelli.me.git.integration.githook.push.properties.GitRemoteProperties;

public class GitHubIntegration implements GitRemoteIntegration {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubIntegration.class);

    private GitHub github;
    private String user;
    private CredentialsProvider credentialsProvider;

    public GitHubIntegration(final GitRemoteProperties props) {
        try {
            if (props.getToken().isEmpty()) {
                credentialsProvider = new UsernamePasswordCredentialsProvider(props.getLogin(), props.getPassword());
                LOGGER.warn("Unsecured connection using username and password since token is not provided in properties file");
                github = GitHub.connectToEnterprise(props.getRemoteGitUrl(), props.getLogin(), props.getPassword());
            } else {
                LOGGER.warn("Connecting using secured token from properties file");
                github = GitHub.connectUsingOAuth(props.getRemoteGitUrl(), props.getToken());
                credentialsProvider = new UsernamePasswordCredentialsProvider(props.getToken(), "");
            }
            user = github.getMyself().getLogin();
        } catch (IOException e) {
            LOGGER.error("An unexpected error occurred.", e);
            throw new RuntimeException(e);
        }
    }

    public String createRepository(String repoName) {
        try {
            final GHRepository repo = github.createRepository(repoName)
                    .description("Created from Business Central: " + repoName)
                    .autoInit(false)
                    .create();
            if (user.isEmpty()) {
                user = repo.getOwnerName();
            }
            return repo.getHttpTransportUrl();
        } catch (IOException e) {
            LOGGER.error("An unexpected error occurred.", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public CredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }
}
