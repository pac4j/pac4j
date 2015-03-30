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
