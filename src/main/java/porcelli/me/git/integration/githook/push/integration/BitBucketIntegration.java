package porcelli.me.git.integration.githook.push.integration;

import java.util.List;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliction.gitproviders.bitbucket.client.BitbucketV2API;
import com.aliction.gitproviders.bitbucket.exceptions.BitbucketCreateRepositoryException;
import com.aliction.gitproviders.bitbucket.objects.BitbucketRepository;
import com.aliction.gitproviders.bitbucket.objects.BitbucketRole;
import com.aliction.gitproviders.bitbucket.objects.BitbucketTeam;
import com.aliction.gitproviders.bitbucket.objects.BitbucketUser;

import porcelli.me.git.integration.githook.push.properties.GitRemoteProperties;

public class BitBucketIntegration implements GitRemoteIntegration {

    private static final Logger LOGGER = LoggerFactory.getLogger(BitBucketIntegration.class);

    private static final String ERROR_LOGIN = "Connecting using username and password is not supported with Bitbucket Cloud, kindly use token instead.";

    private BitbucketV2API bitbucket;
    private CredentialsProvider credentialsProvider;
    private BitbucketTeam team = null;
    private BitbucketUser user;

    public BitBucketIntegration(final GitRemoteProperties props) {
        if (props.getToken().isEmpty()) {
            LOGGER.error(ERROR_LOGIN);
            throw new RuntimeException(ERROR_LOGIN);
        } else {
            LOGGER.info("Connecting using secured token from properties file");
            bitbucket = new BitbucketV2API(props.getRemoteGitUrl(), props.getLogin(), props.getToken());
        }
        user = bitbucket.UserAPI().getLoggedUser();
        credentialsProvider = new UsernamePasswordCredentialsProvider(props.getLogin(), props.getToken());
        if (!props.getBitbucketTeam().isEmpty()) {
            team = getTeam(props.getBitbucketTeam());
        }
    }

    @Override
    public String createRepository(final String repoName) {
        BitbucketRepository repository;
        BitbucketRepository createdRepository = null;
        String repoURL = null;
        if (team != null) {
            repository = new BitbucketRepository(repoName.toLowerCase(), team, "git", true);
        } else {
            repository = new BitbucketRepository(repoName, user, "git", true);
        }
        try {
            createdRepository = bitbucket.RepositoryAPI().createRepository(repository);
            repoURL = createdRepository.getLinks().getClone_https();
        } catch (BitbucketCreateRepositoryException e) {
            LOGGER.error("An unexpected error occurred.", e);
            throw new RuntimeException(e);
        }

        return repoURL;
    }

    public BitbucketTeam getTeam(final String groupPath) {
        List<BitbucketTeam> teams = null;
        try {
            teams = bitbucket.TeamAPI().getUserTeams(BitbucketRole.ADMIN);
        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred.", e);
            throw new RuntimeException(e);
        }
        for (BitbucketTeam team : teams) {
            if (team.getUsername().equals(groupPath)) {
                return team;
            }
        }
        throw new TeamNotFoundException("Team \"" + groupPath + "\" is not found or User \"" + user.getUsername() + "\" has no admin rights to create a repository within the team");
    }

    @Override
    public CredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }
}
