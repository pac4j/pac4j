package org.pac4j.oauth.profile.foursquare;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a Foursquare user friend group container, with count and list of different groups.
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareUserFriends implements Serializable {

    private static final long serialVersionUID = -6264070010780654226L;

    private int count;
    private List<FoursquareUserFriendGroup> groups = new ArrayList<FoursquareUserFriendGroup>();

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
     * <p>Getter for the field <code>groups</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<FoursquareUserFriendGroup> getGroups() {
        return groups;
    }

    /**
     * <p>Setter for the field <code>groups</code>.</p>
     *
     * @param groups a {@link java.util.List} object
     */
    public void setGroups(List<FoursquareUserFriendGroup> groups) {
        this.groups = groups;
    }
}
