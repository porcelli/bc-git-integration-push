package porcelli.me.git.integration.webhook.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import porcelli.me.git.integration.common.properties.GitRemoteProperties;
import porcelli.me.git.integration.webhook.BCIntegration;
import porcelli.me.git.integration.common.json.MappingModule;

public abstract class HookResourceBase {

    protected final ObjectMapper objectMapper;
    protected final BCIntegration bcIntegration;

    public HookResourceBase() {
        final GitRemoteProperties properties = new GitRemoteProperties();
        final MappingModule module = new MappingModule();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
        bcIntegration = new BCIntegration(properties);
    }
}
