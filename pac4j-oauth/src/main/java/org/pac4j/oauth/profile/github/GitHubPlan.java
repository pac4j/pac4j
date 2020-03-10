package org.pac4j.oauth.profile.github;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * This class represents a GitHub plan.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class GitHubPlan implements Serializable {

    private static final long serialVersionUID = -4718500186419958716L;

    private String name;

    private Integer collaborators;

    private Integer space;

    @JsonProperty("private_repos")
    private Integer privateRepos;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(final Integer collaborators) {
        this.collaborators = collaborators;
    }

    public Integer getSpace() {
        return space;
    }

    public void setSpace(final Integer space) {
        this.space = space;
    }

    public Integer getPrivateRepos() {
        return privateRepos;
    }

    public void setPrivateRepos(final Integer privateRepos) {
        this.privateRepos = privateRepos;
    }
}
