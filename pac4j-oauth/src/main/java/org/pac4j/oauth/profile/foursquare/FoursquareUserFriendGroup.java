package org.pac4j.oauth.profile.foursquare;

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

    private static final long serialVersionUID = -8744746298033480494L;

    private int count;
    private String name;
    private String type;
    private List<FoursquareUserFriend> friends = new ArrayList<>();

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public List<FoursquareUserFriend> getFriends() {
        return friends;
    }

    public void setFriends(final List<FoursquareUserFriend> friends) {
        this.friends = friends;
    }
}
