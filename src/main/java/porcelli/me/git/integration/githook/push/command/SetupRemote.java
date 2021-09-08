package porcelli.me.git.integration.githook.push.command;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

//To read remote url from file
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
                          final Path currentPath) throws IOException, GitAPIException {
        final StoredConfig storedConfig = git.getRepository().getConfig();

        final String repoName = new GetRepoName().execute(currentPath);

        //Check repository exists
        //System.out.println("repo name: " + repoName);

        //final String remoteURL = integration.createRepository(repoName);

        //final String remoteURL = "https://github.com/itsbigspark/dev-decision-manager.git";

        //To read remote url from file
        String remoteURL = "";
        try {
            File myObj = new File("/Users/sebastienmichaud/.gitremote");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                int remoteURLIndex = data.indexOf("setupRemoteURL");
                if (remoteURLIndex > -1) {
                    remoteURL = data.substring(remoteURLIndex + "setupRemoteURL".length() + 1);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not get setupRemoteURL");
            e.printStackTrace();
        }

        storedConfig.setString("remote", "origin", "url", remoteURL);
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
