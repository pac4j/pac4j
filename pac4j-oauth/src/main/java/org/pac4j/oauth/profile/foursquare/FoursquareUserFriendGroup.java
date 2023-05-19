package org.pac4j.oauth.profile.foursquare;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a Foursquare group of friends, could be mutual or other.
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareUserFriendGroup implements Serializable {

    @Serial
    private static final long serialVersionUID = -8744746298033480494L;

    private int count;
    private String name;
    private String type;
    private List<FoursquareUserFriend> friends = new ArrayList<>();

    /**
     * <p>Getter for the field <code>count</code>.</p>
     *
     * @return a int
     */
    public int getCount() {
        return count;
    }

    /**
     * <p>Setter for the field <code>count</code>.</p>
     *
     * @param count a int
     */
    public void setCount(int count) {
        this.count = count;
    }

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
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getType() {
        return type;
    }

    /**
     * <p>Setter for the field <code>type</code>.</p>
     *
     * @param type a {@link String} object
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * <p>Getter for the field <code>friends</code>.</p>
     *
     * @return a {@link List} object
     */
    public List<FoursquareUserFriend> getFriends() {
        return friends;
    }

    /**
     * <p>Setter for the field <code>friends</code>.</p>
     *
     * @param friends a {@link List} object
     */
    public void setFriends(List<FoursquareUserFriend> friends) {
        this.friends = friends;
    }
}
