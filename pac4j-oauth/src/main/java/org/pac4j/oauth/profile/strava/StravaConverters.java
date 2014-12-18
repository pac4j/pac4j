package org.pac4j.oauth.profile.strava;

import org.pac4j.core.profile.converter.DateConverter;
import org.pac4j.core.profile.converter.FormattedDateConverter;
import org.pac4j.oauth.profile.converter.JsonListConverter;

/**
 * Strava profile fields specific converters.
 *
 * @author Adrian Papusoi
 */
public final class StravaConverters {

    public final static JsonListConverter clubListConverter = new JsonListConverter(StravaClub.class);
    public final static JsonListConverter gearListConverter = new JsonListConverter(StravaGear.class);
    /**
     * Looks like the time zone is missused by Strava. To be verified!
     */
    public final static DateConverter dateConverter =  new FormattedDateConverter("yyyy-MM-dd'T'HH:mm:ss'Z'");
}
