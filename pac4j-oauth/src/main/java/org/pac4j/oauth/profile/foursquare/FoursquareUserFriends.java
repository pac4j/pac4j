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

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    public List<FoursquareUserFriendGroup> getGroups() {
        return groups;
    }

    public void setGroups(final List<FoursquareUserFriendGroup> groups) {
        this.groups = groups;
    }
}
