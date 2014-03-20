package org.pac4j.oauth.profile.foursquare;

import org.pac4j.oauth.profile.converter.JsonObjectConverter;
import org.pac4j.oauth.profile.github.GitHubPlan;

public class FoursquareConverters {
    public final static JsonObjectConverter friendsConverter = new JsonObjectConverter(FoursquareUserFriends.class);
}
