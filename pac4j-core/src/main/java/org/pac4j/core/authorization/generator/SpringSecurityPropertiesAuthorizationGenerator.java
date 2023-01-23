package org.pac4j.core.authorization.generator;

import lombok.val;
import org.pac4j.core.context.CallContext;
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
        val keys = properties.stringPropertyNames();
        for (val key : keys) {
            val value = properties.getProperty(key);
            if (CommonHelper.isNotBlank(value)) {
                val parts = value.split(",");
                val nb = parts.length;
                if (nb > 1) {
                    val latest = parts[nb - 1];
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
    public Optional<UserProfile> generate(final CallContext ctx, final UserProfile profile) {
        val id = profile.getId();
        val roles = rolesByUsers.get(id);
        if (roles != null && !roles.isEmpty()) {
            profile.addRoles(roles);
        }
        return Optional.of(profile);
    }
}
