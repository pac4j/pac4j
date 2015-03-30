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
package org.pac4j.oauth.profile.google2;

import org.pac4j.core.profile.converter.FormattedDateConverter;
import org.pac4j.oauth.profile.converter.JsonListConverter;

/**
 * This class defines all the converters specific to Google (using OAuth 2.0 protocol).
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class Google2Converters {

    public final static FormattedDateConverter dateConverter = new FormattedDateConverter("yyyy-MM-dd");

    public final static JsonListConverter listEmailConverter = new JsonListConverter(Google2Email.class);
}
