package org.pac4j.oauth.profile.github;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents a GitHub plan.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class GitHubPlan implements Serializable {

    @Serial
    private static final long serialVersionUID = -4718500186419958716L;

    private String name;

    private Integer collaborators;

    private Integer space;

    @JsonProperty("private_repos")
    private Integer privateRepos;

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link String} object
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>collaborators</code>.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getCollaborators() {
        return collaborators;
    }

    /**
     * <p>Setter for the field <code>collaborators</code>.</p>
     *
     * @param collaborators a {@link Integer} object
     */
    public void setCollaborators(Integer collaborators) {
        this.collaborators = collaborators;
    }

    /**
     * <p>Getter for the field <code>space</code>.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getSpace() {
        return space;
    }

    /**
     * <p>Setter for the field <code>space</code>.</p>
     *
     * @param space a {@link Integer} object
     */
    public void setSpace(Integer space) {
        this.space = space;
    }

    /**
     * <p>Getter for the field <code>privateRepos</code>.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getPrivateRepos() {
        return privateRepos;
    }

    /**
     * <p>Setter for the field <code>privateRepos</code>.</p>
     *
     * @param privateRepos a {@link Integer} object
     */
    public void setPrivateRepos(Integer privateRepos) {
        this.privateRepos = privateRepos;
    }
}
