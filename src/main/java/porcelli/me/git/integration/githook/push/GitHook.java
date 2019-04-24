package porcelli.me.git.integration.githook.push;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;

import com.aliction.git.properties.GitRemoteProperties;
import com.aliction.git.properties.IgnoreList;
import com.aliction.git.remote.integration.GitIntegration;
import com.aliction.git.remote.integration.GitRemoteIntegration;

import porcelli.me.git.integration.githook.push.command.SetupRemote;
import porcelli.me.git.integration.githook.push.github.GitHubCredentials;

import static java.util.Comparator.comparing;

public class GitHook {

    public static void main(String[] args) throws IOException, GitAPIException {
        // collect the repository info, like path and parent path
        final Path currentPath = new File("").toPath().toAbsolutePath();
        final String parentFolderName = currentPath.getParent().getName(currentPath.getParent().getNameCount() - 1).toString();
        // ignoring system space
        if (parentFolderName.equalsIgnoreCase("system")) {
            return;
        }

        // setup GitHub credentials and integration
        GitRemoteProperties properties = new GitRemoteProperties();

        if(!properties.CheckMandatory()) {
        	return;
        }
        final GitHubCredentials ghCredentials = new GitHubCredentials(properties);
        final GitRemoteIntegration integration = GitIntegration.getIntegration(properties);
        if (integration == null) {
        	return;
        }
        IgnoreList ignoreList = new IgnoreList(properties);
        
        for (String ignoreitem : ignoreList.getIgnoreList()) {
        	if(parentFolderName.matches(ignoreitem)) {
        		System.out.println("This project "
        	+ parentFolderName.substring(0, parentFolderName.length() - 4) 
        		+ " will not be pushed to remote repo as it's name matches your ignore list");
        		return;
        	}
        }
        
        // setup the JGit repository access
        final Repository repo = new FileRepositoryBuilder()
                .setGitDir(currentPath.toFile())
                .build();
        final Git git = new Git(repo);

        // collect all remotes for the current repository
        final StoredConfig storedConfig = repo.getConfig();
        final Set<String> remotes = storedConfig.getSubsections("remote");

        if (remotes.isEmpty()) {
            //create a remote repository, if it does not exist
            new SetupRemote(ghCredentials, integration).execute(git, currentPath);
        }

        // mechanism to find the latest commit
        final List<Ref> branches = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        final RevWalk revWalk = new RevWalk(git.getRepository());

        branches.stream()
                .map(branch -> {
                    try {
                        return revWalk.parseCommit(branch.getObjectId());
                    } catch (Exception e) {
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
                                // push changes to the remote repository
                                git.push()
                                        .setRefSpecs(new RefSpec(ref + ":" + ref))
                                        .setRemote(remoteURL)
                                        .setCredentialsProvider(ghCredentials.getCredentials())
                                        .call();

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
                        throw new RuntimeException(e);
                    }
                });
    }
}
