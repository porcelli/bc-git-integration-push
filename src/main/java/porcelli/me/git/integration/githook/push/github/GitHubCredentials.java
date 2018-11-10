package porcelli.me.git.integration.githook.push.github;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class GitHubCredentials {

    private UsernamePasswordCredentialsProvider credentialsProvider = null;
    private String space = null;

    public GitHubCredentials() {
        File homeDir = new File(System.getProperty("user.home"));
        File propertyFile = new File(homeDir, ".github");

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(propertyFile)) {
            props.load(in);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        space = props.getProperty("login");
        this.credentialsProvider = new UsernamePasswordCredentialsProvider(props.getProperty("login"), props.getProperty("password"));
        System.err.println("credentials: " + space + " : " + props.getProperty("password"));
    }

    public CredentialsProvider getCredentials() {
        return credentialsProvider;
    }

    public String getSpace() {
        return space;
    }
}
