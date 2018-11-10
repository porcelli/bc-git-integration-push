package porcelli.me.git.integration.githook.push.github;

import java.io.IOException;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class GitHubIntegration {

    public String createRepository(final String repoName) throws IOException {
        final GitHub github = GitHub.connect();
        final GHRepository repo = github.createRepository(repoName)
                .description("Created from Business Central: " + repoName)
                .autoInit(false)
                .create();

        return repo.getHttpTransportUrl();
    }
}
