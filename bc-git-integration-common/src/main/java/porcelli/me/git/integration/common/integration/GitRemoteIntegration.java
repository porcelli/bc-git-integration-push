package porcelli.me.git.integration.common.integration;

import org.eclipse.jgit.transport.CredentialsProvider;

public interface GitRemoteIntegration {

    String createRepository(String repoName);

    CredentialsProvider getCredentialsProvider();

    String getOriginName();
}
