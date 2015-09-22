/*
 * Copyright 2012 - 2015 pac4j organization
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.pac4j.cas.authorization;

import org.pac4j.cas.client.CasClient;
import org.pac4j.core.authorization.AuthorizationGenerator;
import org.pac4j.core.profile.CommonProfile;

/**
 * Default {@link AuthorizationGenerator} implementation for a {@link CasClient} which is able
 * to retrieve the isRemembered status from the CAS response and put it in the profile. 
 * 
 * @author Michael Remond
 * @since 1.5.1
 *
 * @param <U> the profile
 */
public class DefaultCasAuthorizationGenerator<U extends CommonProfile> implements AuthorizationGenerator<U> {

    public static final String DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME = "longTermAuthenticationRequestTokenUsed";

    // default name of the CAS attribute for remember me authentication (CAS 3.4.10+)
    private String rememberMeAttributeName = DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME;

    public DefaultCasAuthorizationGenerator() {
    }

    public DefaultCasAuthorizationGenerator(String rememberMeAttributeName) {
        this.rememberMeAttributeName = rememberMeAttributeName;
    }

    public void generate(U profile) {
        String rememberMeValue = (String) profile.getAttribute(rememberMeAttributeName);
        boolean isRemembered = rememberMeValue != null && Boolean.parseBoolean(rememberMeValue);
        profile.setRemembered(isRemembered);
    }

}
