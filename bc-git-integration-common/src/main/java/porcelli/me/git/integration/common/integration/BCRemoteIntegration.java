package porcelli.me.git.integration.common.integration;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import porcelli.me.git.integration.common.properties.GitRemoteProperties;

public class BCRemoteIntegration {

    public static final String ORIGIN_NAME = "origin";

    private CredentialsProvider credentialsProvider;

    public BCRemoteIntegration(final GitRemoteProperties properties) {
        credentialsProvider = new UsernamePasswordCredentialsProvider(properties.getBcUsername(),
                                                                      properties.getBcPassword());
    }

    public CredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }
}
