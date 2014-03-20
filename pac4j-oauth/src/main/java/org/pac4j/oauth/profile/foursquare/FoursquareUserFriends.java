package org.pac4j.oauth.profile.foursquare;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.pac4j.oauth.profile.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class FoursquareUserFriends extends JsonObject {

    public static class FoursquareUserFriendGroup extends JsonObject{

        private int count;
        private String name;
        private String type;

        private List<FoursquareUserFriend> friends = new ArrayList<FoursquareUserFriend>();

        @Override
        protected void buildFromJson(JsonNode json) {
            count = json.get("count").asInt();
            name = json.get("name").asText();
            type = json.get("type").asText();

            ArrayNode groupsArray = (ArrayNode) json.get("items");

            for (int i=0;i<groupsArray.size();i++) {
                FoursquareUserFriend friend = new FoursquareUserFriend();
                friend.buildFromJson(groupsArray.get(i));
                friends.add(friend);
            }
        }

        public List<FoursquareUserFriend> getFriends() {
            return friends;
        }

        public int getCount() {
            return count;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }

    private int count;
    private List<FoursquareUserFriendGroup> groups = new ArrayList<FoursquareUserFriendGroup>();

    @Override
    protected void buildFromJson(JsonNode json) {
        count = json.get("count").asInt();
        ArrayNode groupsArray = (ArrayNode) json.get("groups");

        for (int i=0;i<groupsArray.size();i++) {
            FoursquareUserFriendGroup group = new FoursquareUserFriendGroup();
            group.buildFromJson(groupsArray.get(i));
            groups.add(group);
        }
    }

    public int getCount() {
        return count;
    }

    public List<FoursquareUserFriendGroup> getGroups() {
        return groups;
    }


}
