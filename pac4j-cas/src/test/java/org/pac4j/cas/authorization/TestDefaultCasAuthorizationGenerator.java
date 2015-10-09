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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.pac4j.core.authorization.AuthorizationGenerator;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;

/**
 * This class tests the {@link DefaultCasAuthorizationGenerator}.
 * 
 * @author Michael Remond
 * @since 1.5.1
 */
public final class TestDefaultCasAuthorizationGenerator extends TestCase {

    public void testNoAttribute() {
        AuthorizationGenerator<CommonProfile> generator = new DefaultCasAuthorizationGenerator<CommonProfile>();
        Map<String, Object> attributes = new HashMap<String, Object>();
        CommonProfile profile = (CommonProfile) ProfileHelper.buildProfile("CasProfile#id", attributes);
        generator.generate(profile);
        assertEquals(false, profile.isRemembered());
    }

    public void testBadAttributeValue() {
        AuthorizationGenerator<CommonProfile> generator = new DefaultCasAuthorizationGenerator<CommonProfile>();
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(DefaultCasAuthorizationGenerator.DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME, "yes");
        CommonProfile profile = (CommonProfile) ProfileHelper.buildProfile("CasProfile#id", attributes);
        generator.generate(profile);
        assertEquals(false, profile.isRemembered());
    }

    public void testIsNotRemembered() {
        AuthorizationGenerator<CommonProfile> generator = new DefaultCasAuthorizationGenerator<CommonProfile>();
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(DefaultCasAuthorizationGenerator.DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME, "false");
        CommonProfile profile = (CommonProfile) ProfileHelper.buildProfile("CasProfile#id", attributes);
        generator.generate(profile);
        assertEquals(false, profile.isRemembered());
    }

    public void testIsRemembered() {
        AuthorizationGenerator<CommonProfile> generator = new DefaultCasAuthorizationGenerator<CommonProfile>();
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(DefaultCasAuthorizationGenerator.DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME, "true");
        CommonProfile profile = (CommonProfile) ProfileHelper.buildProfile("CasProfile#id", attributes);
        generator.generate(profile);
        assertEquals(true, profile.isRemembered());
    }
}
