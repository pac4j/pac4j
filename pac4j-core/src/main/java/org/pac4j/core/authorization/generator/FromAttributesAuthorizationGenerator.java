package org.pac4j.core.authorization.generator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;

import java.util.*;

/**
 * <p>Generate the authorization information by inspecting attributes.</p>
 * <p>The attributes containing the roles separated by the {@link #splitChar} property (can be set through {@link #setSplitChar(String)})
 * are defined in the constructor. It's the same for the attributes containing the permissions.</p>
 *
 * @author Jerome Leleu
 * @since 1.5.0
 */
public class FromAttributesAuthorizationGenerator implements AuthorizationGenerator {

    private Collection<String> roleAttributes;

    private Collection<String> permissionAttributes;

    private String splitChar = ",";

    public FromAttributesAuthorizationGenerator() {
        this.roleAttributes = new ArrayList<>();
        this.permissionAttributes = new ArrayList<>();
    }

    public FromAttributesAuthorizationGenerator(final Collection<String> roleAttributes, final Collection<String> permissionAttributes) {
        this.roleAttributes = roleAttributes;
        this.permissionAttributes = permissionAttributes;
    }

    public FromAttributesAuthorizationGenerator(final String[] roleAttributes, final String[] permissionAttributes) {
        if (roleAttributes != null) {
            this.roleAttributes = Arrays.asList(roleAttributes);
        } else {
            this.roleAttributes = null;
        }
        if (permissionAttributes != null) {
            this.permissionAttributes = Arrays.asList(permissionAttributes);
        } else {
            this.permissionAttributes = null;
        }
    }

    @Override
    public Optional<UserProfile> generate(final WebContext context, final SessionStore sessionStore, final UserProfile profile) {
        generateAuth(profile, this.roleAttributes, true);
        generateAuth(profile, this.permissionAttributes, false);
        return Optional.of(profile);
    }

    private void generateAuth(final UserProfile profile, final Iterable<String> attributes, final boolean isRole) {
        if (attributes == null) {
            return;
        }

        for (final var attribute : attributes) {
            final var value = profile.getAttribute(attribute);
            if (value != null) {
                if (value instanceof String) {
                    final var st = new StringTokenizer((String) value, this.splitChar);
                    while (st.hasMoreTokens()) {
                        addRoleOrPermissionToProfile(profile, st.nextToken(), isRole);
                    }
                } else if (value.getClass().isArray() && value.getClass().getComponentType().isAssignableFrom(String.class)) {
                    for (var item : (Object[]) value) {
                        addRoleOrPermissionToProfile(profile, item.toString(), isRole);
                    }
                } else if (Collection.class.isAssignableFrom(value.getClass())) {
                    for (Object item : (Collection<?>) value) {
                        if (item.getClass().isAssignableFrom(String.class)) {
                            addRoleOrPermissionToProfile(profile, item.toString(), isRole);
                        }
                    }
                }
            }
        }

    }

    private void addRoleOrPermissionToProfile(final UserProfile profile, final String value, final boolean isRole) {
        if (isRole) {
            profile.addRole(value);
        } else {
            profile.addPermission(value);
        }
    }

    public String getSplitChar() {
        return this.splitChar;
    }

    public void setSplitChar(final String splitChar) {
        this.splitChar = splitChar;
    }

    public void setRoleAttributes(final String roleAttributesStr) {
        this.roleAttributes = Arrays.asList(roleAttributesStr.split(splitChar));
    }

    public void setPermissionAttributes(final String permissionAttributesStr) {
        this.permissionAttributes = Arrays.asList(permissionAttributesStr.split(splitChar));
    }
}
