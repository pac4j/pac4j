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
package org.pac4j.http.authorization.authorizer;

import org.pac4j.core.authorization.Authorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Authorizes users based on their IP and a regexp pattern.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class IpRegexpAuthorizer implements Authorizer<UserProfile> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String regexPattern;

    private Pattern pattern;

    public IpRegexpAuthorizer() { }

    public IpRegexpAuthorizer(final String regexpPattern) {
        setRegexpPattern(regexpPattern);
    }

    @Override
    public boolean isAuthorized(final WebContext context, final UserProfile profile) {
        CommonHelper.assertNotNull("pattern", pattern);

        final String ip = context.getRemoteAddr();
        return this.pattern.matcher(ip).matches();
    }

    public void setRegexpPattern(final String regexpPattern) {
        this.regexPattern = regexpPattern;
        this.pattern = Pattern.compile(regexpPattern);
    }

    @Override
    public String toString() {
        return "IpRegexpAuthorizer[" + this.regexPattern + "]";
    }
}
