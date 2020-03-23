package porcelli.me.git.integration.webhook;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import com.jcraft.jsch.Session;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.URIish;
import porcelli.me.git.integration.common.integration.BCRemoteIntegration;
import porcelli.me.git.integration.common.integration.GitRemoteIntegration;
import porcelli.me.git.integration.common.properties.GitRemoteProperties;
import porcelli.me.git.integration.common.model.PullRequestEvent;
import porcelli.me.git.integration.common.model.PushEvent;

public class BCIntegration {

    private final Map<String, Repository> repositoryMap = new HashMap<>();

    private final GitRemoteProperties properties;
    private final GitRemoteIntegration integration;
    private final BCRemoteIntegration bcRemoteIntegration;
    private final TransportConfigCallback transportConfigCallback;

    public BCIntegration(final GitRemoteProperties properties) {
        this.properties = properties;

        if (!properties.validate()) {
            throw new IllegalStateException("Invalid properties file.");
        }

        integration = properties.getGitProvider().getRemoteIntegration(properties);

        bcRemoteIntegration = new BCRemoteIntegration(properties);

        transportConfigCallback = transport -> {
            final SshTransport sshTransport = (SshTransport) transport;
            sshTransport.setSshSessionFactory(new JschConfigSessionFactory() {
                @Override
                protected void configure(OpenSshConfig.Host host, Session session) {
                    // additional configurations can be set here
                }
            });
        };
    }

    public void onPush(final PushEvent pushEvent) throws GitAPIException, URISyntaxException {
        if (!pushEvent.getRef().contains("master")) {
            return;
        }
        final Git git = getGit(pushEvent.getRepository());

        try {
            final PullCommand pullCommandFromBC = git.pull().setRemote(BCRemoteIntegration.ORIGIN_NAME).setRebase(true);
            final PullCommand pullCommandFromService = git.pull().setRemote(integration.getOriginName()).setRebase(true);
            final PushCommand pushCommandToBC = git.push().setRemote(BCRemoteIntegration.ORIGIN_NAME).setForce(true);
            final PushCommand pushCommandToService = git.push().setRemote(integration.getOriginName()).setForce(true);

            if (properties.getUseSSH()) {
                pullCommandFromBC.setTransportConfigCallback(transportConfigCallback);
                pullCommandFromService.setTransportConfigCallback(transportConfigCallback);
                pushCommandToBC.setTransportConfigCallback(transportConfigCallback);
                pushCommandToService.setTransportConfigCallback(transportConfigCallback);
            } else {
                pullCommandFromBC.setCredentialsProvider(bcRemoteIntegration.getCredentialsProvider());
                pullCommandFromService.setCredentialsProvider(integration.getCredentialsProvider());
                pushCommandToBC.setCredentialsProvider(bcRemoteIntegration.getCredentialsProvider());
                pushCommandToService.setCredentialsProvider(integration.getCredentialsProvider());
            }

            pullCommandFromBC.call();
            pullCommandFromService.call();
            pushCommandToBC.call();
            pushCommandToService.call();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onPullRequest(final PullRequestEvent pullRequestEvent) throws GitAPIException, URISyntaxException {
        if (!pullRequestEvent.getAction().equals(PullRequestEvent.Action.CLOSED)) {
            return;
        }
        final String branchName = pullRequestEvent.getPullRequest().getBody();

        final Git git = getGit(pullRequestEvent.getRepository());
        git.branchDelete().setBranchNames("refs/heads/" + branchName).call();

        final RefSpec refSpec = new RefSpec().setSource(null).setDestination("refs/heads/" + branchName);
        git.push().setRefSpecs(refSpec).setRemote("origin").call();
    }

    private Git getGit(porcelli.me.git.integration.common.model.Repository repository)
            throws GitAPIException, URISyntaxException {
        final Git git;
        if (!repositoryMap.containsKey(repository.getDescription())) {
            final String bcRepo = repository.getDescription();

            try {
                final CloneCommand cloneCommand = Git.cloneRepository()
                        .setURI(bcRepo)
                        .setDirectory(tempDir(repository.getFullName()));

                if (properties.getUseSSH()) {
                    cloneCommand.setTransportConfigCallback(transportConfigCallback);
                } else {
                    cloneCommand.setCredentialsProvider(bcRemoteIntegration.getCredentialsProvider());
                }

                git = cloneCommand.call();
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }

            final RemoteAddCommand remoteAddCommand = git.remoteAdd();
            remoteAddCommand.setName(integration.getOriginName());
            remoteAddCommand.setUri(new URIish(repository.getCloneUrl()));
            remoteAddCommand.call();
            repositoryMap.put(repository.getDescription(), git.getRepository());
        } else {
            git = new Git(repositoryMap.get(repository.getDescription()));
        }
        return git;
    }

    private File tempDir(String reponame) throws IOException {
        return Files.createTempDirectory(new File(System.getProperty("java.io.tmpdir")).toPath(), "temp").resolve(reponame).toFile();
    }
}
