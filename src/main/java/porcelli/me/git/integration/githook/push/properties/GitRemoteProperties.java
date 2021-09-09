package porcelli.me.git.integration.githook.push.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import porcelli.me.git.integration.githook.push.integration.GitProvider;

public class GitRemoteProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitRemoteProperties.class);

    private String remoteGitUrl = null;
    private String login = null;
    private String password = null;
    private String token = null;
    private String ignoreList = null;
    private GitProvider gitProvider = null;
    private String gitHubOrg;
    private String gitLabGroup;
    private String bitbucketTeam;
    private Properties props = new Properties();
    private boolean useSSH;
    private boolean pushOnlyMode = false;
    private String remoteGitRepoUrl = null;

    public GitRemoteProperties() {
        final File homeDir = new File(System.getProperty("user.home"));
        final File propertyFile = new File(homeDir, ".gitremote");

        if (!propertyFile.exists()) {
            LOGGER.error(getPropertiesFileName() + " file does not exists. A sample will be automatically generated that will require manual input of valid data.");
            createTemplate(propertyFile);
            return;
        }

        loadProperties(propertyFile);

        setRemoteGitUrl(props.getProperty("remoteGitUrl"));
        setLogin(props.getProperty("login"));
        setPassword(props.getProperty("password"));
        setToken(props.getProperty("token", ""));
        setIgnoreList(props.getProperty("ignore"));
        setGitProvider(props.getProperty("provider"));
        setGitLabGroup(props.getProperty("gitlabGroup", ""));
        setGitHubOrg(props.getProperty("githubOrg", ""));
        setBitbucketTeam(props.getProperty("bitbucketTeam", ""));
        setUseSSH(props.getProperty("useSSH", "false"));
        setRemoteGitRepoUrl(props.getProperty("remoteGitRepoUrl"));
    }

    private void loadProperties(File propertyFile) {
        try (FileInputStream in = new FileInputStream(propertyFile)) {
            props.load(in);
        } catch (Exception e) {
            LOGGER.error("Exception. ", e);
            throw new RuntimeException(e);
        }
    }

    public boolean validate() {
        if (gitProvider == null) {
            LOGGER.error("'provider' is a mandatory missing or incorrect value in " + getPropertiesFileName());
            return false;
        }
        if (remoteGitRepoUrl == null) {
            LOGGER.error("'remoteGitRepoUrl' is a mandatory missing value in " + getPropertiesFileName());
            return false;
        }
        if (this.getToken().isEmpty()) {
            if (useSSH && (this.login.isEmpty() || this.password.isEmpty())){
                LOGGER.warn("Git hook in push mode only. External repository creation won't be executed.");
                pushOnlyMode = true;
                return true;
            }
            if (this.login.isEmpty()) {
                LOGGER.error("'login' is a mandatory missing value in " + getPropertiesFileName());
                LOGGER.error("You can set value for 'token' and skip 'login'/'password'");
                return false;
            }
            if (this.password.isEmpty()) {
                LOGGER.error("'password' is a mandatory missing value in " + getPropertiesFileName());
                LOGGER.error("You can set value for 'token' and skip 'login'/'password'");
                return false;
            }
        }
        return true;
    }

    private void createTemplate(File propertyFile) {
        try {
            propertyFile.createNewFile();
            Properties props = new Properties();
            props.setProperty("provider", "GIT_HUB");
            props.setProperty("githubOrg", "OrgName");
            props.setProperty("bitbucketTeam", "TeamName");
            props.setProperty("gitlabGroup", "Group/subgroup");
            props.setProperty("remoteGitUrl", "https://api.github.com/");
            props.setProperty("remoteGitRepoUrl", "");
            props.setProperty("login", "");
            props.setProperty("password", "");
            props.setProperty("token", "");
            props.setProperty("useSSH", "false");
            props.setProperty("ignore", ".*demo.*, test.*");
            FileOutputStream out = new FileOutputStream(propertyFile);
            props.store(out, "This is an auto generated template empty property file");
            LOGGER.warn(getPropertiesFileName() + " template file has been created for you, kindly fill in the missing values");
        } catch (IOException e) {
            LOGGER.error("Exception. ", e);
            throw new RuntimeException(e);
        }
    }

    public GitProvider getGitProvider() {
        return gitProvider;
    }

    private void setGitProvider(String provider) {
        try {
            gitProvider = GitProvider.valueOf(provider);
        } catch (Exception ex) {
            gitProvider = null;
        }
        LOGGER.info("The git provider is : " + gitProvider);
    }

    public String getGitHubOrg() {
        return gitHubOrg;
    }

    public void setGitHubOrg(String gitHubOrg) {
        this.gitHubOrg = gitHubOrg;
    }

    public String getGitLabGroup() {
        return gitLabGroup;
    }

    public void setGitLabGroup(final String gitLabGroup) {
        this.gitLabGroup = gitLabGroup;
    }

    public String getBitbucketTeam() {
        return bitbucketTeam;
    }

    public void setBitbucketTeam(String bitbucketTeam) {
        this.bitbucketTeam = bitbucketTeam;
    }

    public String getRemoteGitUrl() {
        return remoteGitUrl;
    }

    private void setRemoteGitUrl(String remoteGitUrl) {
        this.remoteGitUrl = remoteGitUrl;
    }

    public String getLogin() {
        return this.login;
    }

    private void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIgnoreList() {
        return ignoreList;
    }

    private void setIgnoreList(String ignoreList) {
        this.ignoreList = ignoreList;
    }

    public String getPropertiesFileName() {
        return "~/.gitremote";
    }

    public void setUseSSH(final String value) {
        try {
            this.useSSH = Boolean.valueOf(value);
        } catch (Exception ex) {
            this.useSSH = false;
        }
    }

    public boolean getUseSSH() {
        return useSSH;
    }

    public boolean isPushOnlyMode() {
        return pushOnlyMode;
    }

    public String getRemoteGitRepoUrl() {
        return remoteGitRepoUrl;
    }

    private void setRemoteGitRepoUrl(String remoteGitRepoUrl) {
        this.remoteGitRepoUrl = remoteGitRepoUrl;
    }

}
