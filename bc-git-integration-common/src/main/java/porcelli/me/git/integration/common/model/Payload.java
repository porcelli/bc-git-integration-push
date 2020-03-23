package porcelli.me.git.integration.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Payload {

    /**
     * When configuring a webhook, you can choose which events you would like to
     * receive payloads for. You can even opt-in to all current and future
     * events. Only subscribing to the specific events you plan on handling is
     * useful for limiting the number of HTTP requests to your server. You can
     * change the list of subscribed events through the API or UI anytime. By
     * default, webhooks are only subscribed to the push event.
     *
     * <p>
     * Each event corresponds to a certain set of actions that can happen to
     * your organization and/or repository. For example, if you subscribe to the
     * issues event you’ll receive detailed payloads every time an issue is
     * opened, closed, labeled, etc.
     * </p>
     */
    public static enum EventType {
        /**
         * Any time a Commit is commented on.
         */
        COMMIT_COMMENT,

        /**
         * Any time a Branch or Tag is created.
         */
        CREATE,

        /**
         * Any time a Branch or Tag is deleted.
         */
        DELETE,

        /**
         * Any time a Repository has a new deployment created from the API.
         */
        DEPLOYMENT,

        /**
         * Any time a deployment for a Repository has a status update from the API.
         */
        DEPLOYMENT_STATUS,

        /**
         * Any time a Repository is forked.
         */
        FORK,

        /**
         * Any time a Wiki page is updated.
         */
        GOLLUM,

        /**
         * Any time an Issue is commented on.
         */
        ISSUE_COMMENT,

        /**
         * Any time an Issue is assigned, unassigned, labeled, unlabeled, opened, closed, or reopened.
         */
        ISSUES,

        /**
         * Any time a User is added as a collaborator to a non-Organization Repository.
         */
        MEMBER,

        /**
         * Any time a User is added or removed from a team. <B>Organization hooks only.</B>
         */
        MEMBERSHIP,

        /**
         * Any time a Pages site is built or results in a failed build.
         */
        PAGE_BUILD,

        PING,

        /**
         * Any time a Repository changes from private to public.
         */
        PUBLIC,

        /**
         * Any time a Commit is commented on while inside a Pull Request review (the Files Changed tab).
         */
        PULL_REQUEST_REVIEW_COMMENT,

        /**
         * Any time a Pull Request is assigned, unassigned, labeled, unlabeled,
         * opened, closed, reopened, or synchronized (updated due to a new push
         * in the branch that the pull request is tracking).
         */
        PULL_REQUEST,

        /**
         * Any Git push to a Repository, including editing tags or branches.
         * Commits via API actions that update references are also counted. This
         * is the default event.
         */
        PUSH,
        REPO_PUSH,
        PUSH_HOOK,

        /**
         * Any time a Repository is created. Organization hooks only.
         */
        REPOSITORY,

        /**
         * Any time a Release is published in a Repository.
         */
        RELEASE,

        /**
         * Any time a Repository has a status update from the API
         */
        STATUS,

        /**
         * Any time a team is added or modified on a Repository.
         */
        TEAM_ADD,

        /**
         * Any time a User watches a Repository.
         */
        WATCH;

    }

    @JsonIgnore
    private EventType eventType;

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
