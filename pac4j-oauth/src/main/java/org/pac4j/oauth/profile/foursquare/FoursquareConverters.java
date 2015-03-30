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

import org.pac4j.oauth.profile.converter.JsonObjectConverter;

/**
 * This class defines all the converters specific to Foursquare.
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareConverters {
    public final static JsonObjectConverter friendsConverter = new JsonObjectConverter(FoursquareUserFriends.class);
    public final static JsonObjectConverter contactConverter = new JsonObjectConverter(FoursquareUserContact.class);
    public final static JsonObjectConverter photoConverter = new JsonObjectConverter(FoursquareUserPhoto.class);
}
