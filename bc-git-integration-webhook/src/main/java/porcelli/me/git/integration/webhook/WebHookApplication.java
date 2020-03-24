package porcelli.me.git.integration.webhook;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import porcelli.me.git.integration.webhook.resource.BitBucketResource;
import porcelli.me.git.integration.webhook.resource.GitHubResource;
import porcelli.me.git.integration.webhook.resource.GitLabResource;

public class WebHookApplication extends Application {

    public WebHookApplication() {
    }

    @Override
    public Set<Object> getSingletons() {
        return new HashSet<Object>(){{
            add(new GitHubResource());
            add(new BitBucketResource());
            add(new GitLabResource());
        }};
    }
}
