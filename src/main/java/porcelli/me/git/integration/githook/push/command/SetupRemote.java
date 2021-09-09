package porcelli.me.git.integration.githook.push.command;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.StoredConfig;
import porcelli.me.git.integration.githook.push.integration.GitRemoteIntegration;

public class SetupRemote implements Command {

    private final GitRemoteIntegration integration;

    public SetupRemote(final GitRemoteIntegration integration) {
        this.integration = integration;
    }

    public String execute(final Git git,
                          final Path currentPath,
                          final String remoteGitRepoUrl) throws IOException, GitAPIException {
        final StoredConfig storedConfig = git.getRepository().getConfig();

        final String repoName = new GetRepoName().execute(currentPath);

        storedConfig.setString("remote", "origin", "url", remoteGitRepoUrl);
        storedConfig.setString("remote", "origin", "fetch", "+refs/heads/*:refs/remotes/origin/*");

        final List<Ref> branches = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        for (Ref value : branches) {
            final String shortName = value.getName().replaceAll("refs/heads/", "");
            storedConfig.setString("branch", shortName, "remote", "origin");
            storedConfig.setString("branch", shortName, "merge", "refs/heads/" + shortName);
        }
        storedConfig.save();

        git.push().setCredentialsProvider(integration.getCredentialsProvider()).call();
        return repoName;
    }
}
