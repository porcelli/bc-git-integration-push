package porcelli.me.git.integration.githook.push.integration;

import org.eclipse.jgit.transport.CredentialsProvider;

public interface GitRemoteIntegration {

    String createRepository(String repoName);

    CredentialsProvider getCredentialsProvider();
}
