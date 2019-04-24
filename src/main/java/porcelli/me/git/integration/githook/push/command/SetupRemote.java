package porcelli.me.git.integration.githook.push.command;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import porcelli.me.git.integration.githook.push.github.GitHubCredentials;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.StoredConfig;

import com.aliction.git.remote.integration.GitRemoteIntegration;

public class SetupRemote implements Command {

    private final GitHubCredentials credentials;
    private final GitRemoteIntegration integration;

    public SetupRemote(GitHubCredentials credentials,
                       GitRemoteIntegration integration) {
        this.credentials = credentials;
        this.integration = integration;
    }

    public String execute(final Git git,
                          final Path currentPath) throws IOException, GitAPIException {
        final StoredConfig storedConfig = git.getRepository().getConfig();

        final String repoName = new GetRepoName().execute(currentPath);
        final String remoteURL = integration.createRepository(repoName);
        storedConfig.setString("remote", "origin", "url", remoteURL);
        storedConfig.setString("remote", "origin", "fetch", "+refs/heads/*:refs/remotes/origin/*");

        final List<Ref> branches = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        for (Ref value : branches) {
            final String shortName = value.getName().replaceAll("refs/heads/", "");
            storedConfig.setString("branch", shortName, "remote", "origin");
            storedConfig.setString("branch", shortName, "merge", "refs/heads/" + shortName);
        }
        storedConfig.save();

        git.push().setCredentialsProvider(credentials.getCredentials()).call();
        return repoName;
    }
}
