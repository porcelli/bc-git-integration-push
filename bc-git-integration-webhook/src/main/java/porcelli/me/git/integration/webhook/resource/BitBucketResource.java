package porcelli.me.git.integration.webhook.resource;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import porcelli.me.git.integration.common.model.Payload;
import porcelli.me.git.integration.common.model.PullRequestEvent;
import porcelli.me.git.integration.common.model.PushEvent;

@Path("/bitbucket")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BitBucketResource extends HookResourceBase {

    @POST
    @Path("/")
    public Response post(@HeaderParam("X-Event-Key") String event,
                         @Context HttpServletRequest request) {
        try (InputStream in = request.getInputStream()) {
            Payload.EventType type = Payload.EventType.valueOf(event.replace(":", "_").toUpperCase());

            switch (type) {
                case PULL_REQUEST:
                    bcIntegration.onPullRequest(objectMapper.readValue(in, PullRequestEvent.class));
                    break;
                case REPO_PUSH:
                    bcIntegration.onPush(objectMapper.readValue(in, PushEvent.class));
                    break;
                default:
                    break;
            }

            return Response.ok().build();
        } catch (JsonParseException | JsonMappingException e) {
            throw new InternalServerErrorException(e.getMessage(), Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage() + "\n").build(), e);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e);
        } catch (Exception e) {
            throw new InternalServerErrorException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                                           .entity(e.getMessage()).build(), e);
        }
    }
}
