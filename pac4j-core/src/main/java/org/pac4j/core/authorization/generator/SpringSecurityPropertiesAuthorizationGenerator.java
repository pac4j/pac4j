package org.pac4j.core.authorization.generator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;

import java.util.*;

/**
 * Authorization generator based on a properties file in Spring security format:
 * username=password,grantedAuthority[,grantedAuthority][,enabled|disabled]
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class SpringSecurityPropertiesAuthorizationGenerator implements AuthorizationGenerator {

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
    public Optional<UserProfile> generate(final WebContext context, final UserProfile profile) {
        final String id = profile.getId();
        final List<String> roles = rolesByUsers.get(id);
        if (roles != null && !roles.isEmpty()) {
            profile.addRoles(roles);
        }
        return Optional.of(profile);
    }
}
