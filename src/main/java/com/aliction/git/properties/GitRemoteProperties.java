package com.aliction.git.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.aliction.git.remote.integration.GitProvider;

public class GitRemoteProperties {

    private String remoteGitUrl;
    private String login;
    private String password;
    private String token;
    private String ignoreList;
    private GitProvider gitProvider;
    private Properties props;

    public GitRemoteProperties() {
        File homeDir = new File(System.getProperty("user.home"));
        File propertyFile = new File(homeDir, ".gitremote");
        props = new Properties();

        if (!LoadProperties(propertyFile)) {
            CreateTemplate(propertyFile);
            LoadProperties(propertyFile);
        }

        setRemoteGitUrl(props.getProperty("remoteGitUrl", "https://gitremote.com/"));
        setLogin(props.getProperty("login"));
        setPassword(props.getProperty("password"));
        setToken(props.getProperty("token"));
        setIgnoreList(props.getProperty("ignore"));
        setGitProvider(props.getProperty("provider"));
    }

    public boolean LoadProperties(File propertyFile) {
        try (FileInputStream in = new FileInputStream(propertyFile)) {
            props.load(in);
        } catch (FileNotFoundException fe) {
            System.out.println("~/.gitremote is not found");
            //        	new GitPropertiesFileNotFoundException("~/.gitremote is not found");
            return false;

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        return true;
    }

    public boolean CheckMandatory() {
        if (this.getToken().isEmpty()) {
            if (this.login.isEmpty()) {
                System.out.println("'login' is a mandatory missing value in ~/.gitremote");
                System.out.println("You can set value for 'token' and skip 'login'/'password'");
                return false;
            }
            if (this.password.isEmpty()) {
                System.out.println("'password' is a mandatory missing value in ~/.gitremote");
                System.out.println("You can set value for 'token' and skip 'login'/'password'");
                return false;
            }
        }
        return true;
    }

    private void CreateTemplate(File propertyFile) {
        try {
            propertyFile.createNewFile();
            Properties props = new Properties();
            props.setProperty("provider", "GitHub");
            props.setProperty("remoteGitUrl", "https://api.github.com/");
            props.setProperty("login", "");
            props.setProperty("password", "");
            props.setProperty("token", "");
            props.setProperty("ignore", ".*demo.*, test.*");
            FileOutputStream out = new FileOutputStream(propertyFile);
            props.store(out, "This is an auto generated template empty property file");
            System.out.println("~/.gitremote template file has been created for you, kindly fill in the missing values");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public GitProvider getGitProvider() {
        return gitProvider;
    }

    private void setGitProvider(String provider) {
        gitProvider = GitProvider.find(provider);
        System.out.println("The git provider is : " + gitProvider);
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

}
