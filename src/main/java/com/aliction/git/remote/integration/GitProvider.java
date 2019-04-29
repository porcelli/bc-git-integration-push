package com.aliction.git.remote.integration;

public enum GitProvider {
    GitHub,
    GitLab,
    BitBucket;

    public static GitProvider find(String provider) {
        for (GitProvider prov : GitProvider.values()) {
            if (prov.name().equalsIgnoreCase(provider)) {
                return prov;
            }
        }
        return null;
    }

}
