package porcelli.me.git.integration.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PushEvent extends Payload {

    public PushEvent() {
        setEventType(EventType.PUSH);
    }

    private String ref;

    private Repository repository;

    private User sender;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "PushEvent{" +
                "ref='" + ref + '\'' +
                ", repository=" + repository +
                ", sender=" + sender +
                '}';
    }
}
