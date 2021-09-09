package porcelli.me.git.integration.githook.push;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcraft.jsch.Session;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.SshTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import porcelli.me.git.integration.githook.push.command.SetupRemote;
import porcelli.me.git.integration.githook.push.integration.GitRemoteIntegration;
import porcelli.me.git.integration.githook.push.properties.GitRemoteProperties;
import porcelli.me.git.integration.githook.push.properties.IgnoreList;

import static java.util.Comparator.comparing;

public class GitHook {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHook.class);

    public static void main(String[] args) throws IOException, GitAPIException {

        // collect the repository info, like path and parent path
        final Path currentPath = new File("").toPath().toAbsolutePath();
        final String parentFolderName = currentPath.getParent().getName(currentPath.getParent().getNameCount() - 1).toString();
        final String projectName = currentPath.getName(currentPath.getNameCount() - 1).toString();

        // ignoring system space
        if (parentFolderName.equalsIgnoreCase("system") ||
                parentFolderName.equalsIgnoreCase(".config")) {
            LOGGER.info("System repositories are ignored.");
            return;
        }

        final GitRemoteProperties properties = new GitRemoteProperties();

        if (!properties.validate()) {
            return;
        }

        final IgnoreList ignoreList = new IgnoreList(properties);

        for (final String ignore : ignoreList.getIgnoreList()) {
            if (projectName.matches(ignore)) {
                LOGGER.warn("This project " + projectName.substring(0, projectName.length() - 4) + " will not be pushed to remote repo as it's name matches your ignore list");
                return;
            }
        }

        final GitRemoteIntegration integration;

        if (!properties.isPushOnlyMode()) {
            integration = properties.getGitProvider().getRemoteIntegration(properties);
        } else {
            integration = null;
        }

        // setup the JGit repository access
        final Repository repo = new FileRepositoryBuilder()
                .setGitDir(currentPath.toFile())
                .build();
        final Git git = new Git(repo);

        // collect all remotes for the current repository
        final StoredConfig storedConfig = repo.getConfig();
        final Set<String> remotes = storedConfig.getSubsections("remote");

        if (remotes.isEmpty() && !properties.isPushOnlyMode()) {
            //create a remote repository, if it does not exist
            new SetupRemote(integration).execute(git, currentPath, properties.getRemoteGitRepoUrl());
        }

        // mechanism to find the latest commit
        final List<Ref> branches = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();

        try (final RevWalk revWalk = new RevWalk(git.getRepository())) {
            branches.stream()
                    .map(branch -> {
                        try {
                            return revWalk.parseCommit(branch.getObjectId());
                        } catch (Exception e) {
                            LOGGER.error("An unexpected error occurred.", e);
                            throw new RuntimeException(e);
                        }
                    })
                    .max(comparing((RevCommit commit) -> commit.getAuthorIdent().getWhen()))
                    .ifPresent(latestCommit -> {
                        // the integration here
                        try {
                            //get the branches where this commit is referenced
                            final Map<ObjectId, String> branchesAffected = git
                                    .nameRev()
                                    .addPrefix("refs/heads")
                                    .add(latestCommit)
                                    .call();

                            //iterate over all remote repositories
                            for (String remoteName : remotes) {
                                final String remoteURL = storedConfig.getString("remote", remoteName, "url");
                                for (String ref : branchesAffected.values()) {
                                    final PushCommand pushCommand = git.push()
                                            .setRefSpecs(new RefSpec(ref + ":" + ref))
                                            .setRemote(remoteURL);
                                    if (properties.getUseSSH()) {
                                        // setup of ssh transport config
                                        pushCommand.setTransportConfigCallback(transport -> {
                                            final SshTransport sshTransport = (SshTransport) transport;
                                            sshTransport.setSshSessionFactory(new JschConfigSessionFactory() {
                                                @Override
                                                protected void configure(OpenSshConfig.Host host, Session session) {
                                                }
                                            });
                                        });
                                    } else {
                                        // if not ssh, it requires credentials
                                        pushCommand.setCredentialsProvider(integration.getCredentialsProvider());
                                    }

                                    // push changes to the remote repository
                                    pushCommand.call();

                                    //check if the branch has a remote config
                                    final String remote = storedConfig.getString("branch", ref, "remote");
                                    if (remote == null) {
                                        //branch had no remote info, now needs to be update
                                        storedConfig.setString("branch", ref, "remote", remoteName);
                                        storedConfig.setString("branch", ref, "merge", "refs/heads/" + ref);
                                        storedConfig.save();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.error("An unexpected error occurred.", e);
                        }
                    });
        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred.", e);
        }
    }
}
