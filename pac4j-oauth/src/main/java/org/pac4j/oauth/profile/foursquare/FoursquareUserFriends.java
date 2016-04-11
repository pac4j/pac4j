package org.pac4j.oauth.profile.foursquare;

import org.pac4j.oauth.profile.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a Foursquare user friend group container, with count and list of different groups.
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareUserFriends extends JsonObject {

    private static final long serialVersionUID = -6264070010780654226L;

    private int count;
    private List<FoursquareUserFriendGroup> groups = new ArrayList<FoursquareUserFriendGroup>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<FoursquareUserFriendGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<FoursquareUserFriendGroup> groups) {
        this.groups = groups;
    }
}
