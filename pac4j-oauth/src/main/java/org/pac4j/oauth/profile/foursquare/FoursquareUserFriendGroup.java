/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
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

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<FoursquareUserFriend> getFriends() {
        return friends;
    }

    public void setFriends(List<FoursquareUserFriend> friends) {
        this.friends = friends;
    }
}
