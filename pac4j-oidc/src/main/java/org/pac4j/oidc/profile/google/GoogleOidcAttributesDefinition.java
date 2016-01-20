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
package org.pac4j.oidc.profile.google;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;

import java.util.Arrays;

/**
 * This class defines the attributes of the Google OpenID Connect profile.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class GoogleOidcAttributesDefinition extends AttributesDefinition {

    public static final String NAME = "name";
    public static final String GIVEN_NAME = "given_name";
    public static final String FAMILY_NAME = "family_name";
    public static final String PROFILE = "profile";
    public static final String PICTURE = "picture";
    public static final String EMAIL = "email";
    public static final String EMAIL_VERIFIED = "email_verified";
    public static final String GENDER = "gender";
    public static final String LOCALE = "locale";

    public GoogleOidcAttributesDefinition() {
        Arrays.stream(new String[] {NAME, GIVEN_NAME, FAMILY_NAME, PROFILE, PICTURE, EMAIL}).forEach(a -> primary(a, Converters.STRING));
        primary(EMAIL_VERIFIED, Converters.BOOLEAN);
        primary(GENDER, Converters.GENDER);
        primary(LOCALE, Converters.LOCALE);
    }
}
