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
package org.pac4j.cas.profile;

import org.pac4j.cas.client.rest.CasRestFormClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.TestCaseProfileHelper;

/**
 * The {@link TestHttpTGTProfileHelper} is responsible for
 * executing tests against {@link CasRestFormClient}.
 *
 * @author Misagh Moayyed
 */
public class TestHttpTGTProfileHelper extends TestCaseProfileHelper {

    @Override
    protected Class<? extends CommonProfile> getProfileClass() {
        return HttpTGTProfile.class;
    }

    @Override
    protected String getProfileType() {
        return HttpTGTProfile.class.getSimpleName();
    }

    @Override
    protected String getAttributeName() {
        return "nothing";
    }
}
