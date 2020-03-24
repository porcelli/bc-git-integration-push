package porcelli.me.git.integration.common.properties;

public class IgnoreList {

    private String[] ignoreItems;

    public IgnoreList(final GitRemoteProperties props) {
        if (props.getIgnoreList() != null && !props.getIgnoreList().isEmpty()) {
            ignoreItems = props.getIgnoreList().split(",");
        } else {
            ignoreItems = new String[]{};
        }
    }

    public String[] getIgnoreList() {
        return ignoreItems;
    }
}
