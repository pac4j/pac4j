package org.pac4j.core.authorization.generator;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.profile.UserProfile;

import java.util.*;

/**
 * <p>Generate the authorization information by inspecting attributes.</p>
 * <p>The attributes containing the roles separated by the {@link #splitChar} property are defined in the constructor.</p>
 *
 * @author Jerome Leleu
 * @since 1.5.0
 */
@Getter
@Setter
public class FromAttributesAuthorizationGenerator implements AuthorizationGenerator {

    private Collection<String> roleAttributes;

    private String splitChar = ",";

    public FromAttributesAuthorizationGenerator() {
        this.roleAttributes = new ArrayList<>();
    }

    public FromAttributesAuthorizationGenerator(final Collection<String> roleAttributes) {
        this.roleAttributes = roleAttributes;
    }

    public FromAttributesAuthorizationGenerator(final String[] roleAttributes) {
        if (roleAttributes != null) {
            this.roleAttributes = Arrays.asList(roleAttributes);
        } else {
            this.roleAttributes = null;
        }
    }

    @Override
    public Optional<UserProfile> generate(final CallContext ctx, final UserProfile profile) {
        generateAuth(profile, this.roleAttributes);
        return Optional.of(profile);
    }

    private void generateAuth(final UserProfile profile, final Iterable<String> attributes) {
        if (attributes == null) {
            return;
        }

        for (val attribute : attributes) {
            val value = profile.getAttribute(attribute);
            if (value != null) {
                if (value instanceof String) {
                    val st = new StringTokenizer((String) value, this.splitChar);
                    while (st.hasMoreTokens()) {
                        addRoleToProfile(profile, st.nextToken());
                    }
                } else if (value.getClass().isArray() && value.getClass().getComponentType().isAssignableFrom(String.class)) {
                    for (var item : (Object[]) value) {
                        addRoleToProfile(profile, item.toString());
                    }
                } else if (Collection.class.isAssignableFrom(value.getClass())) {
                    for (Object item : (Collection<?>) value) {
                        if (item.getClass().isAssignableFrom(String.class)) {
                            addRoleToProfile(profile, item.toString());
                        }
                    }
                }
            }
        }

    }

    private void addRoleToProfile(final UserProfile profile, final String value) {
        profile.addRole(value);
    }
}
