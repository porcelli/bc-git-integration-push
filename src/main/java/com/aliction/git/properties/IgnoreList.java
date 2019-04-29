package com.aliction.git.properties;

public class IgnoreList {

    private String ignoreString;
    private String[] ignoreItems;

    public IgnoreList(GitRemoteProperties props) {
        this.ignoreString = props.getIgnoreList();
        createRegexList();

    }

    private void createRegexList() {
        // TODO Auto-generated method stub
        ignoreItems = this.ignoreString.split(",");
        for (String item : ignoreItems) {

        }
    }

    public String[] getIgnoreList() {
        // TODO Auto-generated method stub
        return ignoreItems;
    }

}
