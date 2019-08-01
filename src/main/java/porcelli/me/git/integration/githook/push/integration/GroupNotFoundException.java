package porcelli.me.git.integration.githook.push.integration;

public class GroupNotFoundException extends RuntimeException {

    public GroupNotFoundException(String message) {
        super(message);
    }
}