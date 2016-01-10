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
package org.pac4j.core.authorization.generator;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

import java.util.*;

/**
 * Authorization generator based on a properties file in Spring security format:
 * username=password,grantedAuthority[,grantedAuthority][,enabled|disabled]
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class SpringSecurityPropertiesAuthorizationGenerator  implements AuthorizationGenerator<CommonProfile> {

    public final static String DISABLED = "disabled";
    public final static String ENABLED = "enabled";

    private Map<String, List<String>> rolesByUsers = new HashMap<>();

    public SpringSecurityPropertiesAuthorizationGenerator(final Properties properties) {
        final Set<String> keys = properties.stringPropertyNames();
        for (final String key : keys) {
            final String value = properties.getProperty(key);
            if (CommonHelper.isNotBlank(value)) {
                final String[] parts = value.split(",");
                final int nb = parts.length;
                if (nb > 1) {
                    final String latest = parts[nb - 1];
                    if (!DISABLED.equals(latest)) {
                        final List<String> roles = new ArrayList<>(Arrays.asList(parts));
                        if (ENABLED.equals(latest)) {
                            roles.remove(nb - 1);
                        }
                        roles.remove(0);
                        rolesByUsers.put(key, roles);
                    }
                }
            }
        }
    }

    @Override
    public void generate(final CommonProfile profile) {
        final String id = profile.getId();
        final List<String> roles = rolesByUsers.get(id);
        if (roles != null && !roles.isEmpty()) {
            profile.addRoles(roles);
        }
    }
}
