package org.pac4j.oauth.profile.hiorgserver;

import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * This class is the user profile for HiOrg-Server with appropriate getters. It
 * is returned by the {@link org.pac4j.oauth.client.HiOrgServerClient}.
 *
 * @author Martin Boehmer
 * @since 3.2.0
 */
public class HiOrgServerProfile extends OAuth20Profile {

    private static final long serialVersionUID = 1889864079390590548L;

    /** {@inheritDoc} */
    @Override
    public String getUsername() {
        return getAttribute(HiOrgServerProfileDefinition.USERNAME, String.class);
    }

    /** {@inheritDoc} */
    @Override
    public String getFirstName() {
        return getAttribute(HiOrgServerProfileDefinition.FIRST_NAME, String.class);
    }

    /** {@inheritDoc} */
    @Override
    public String getFamilyName() {
        return getAttribute(HiOrgServerProfileDefinition.NAME, String.class);
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return getAttribute(HiOrgServerProfileDefinition.FULL_NAME, String.class);
    }

    /**
     * <p>getRolesAsInteger.</p>
     *
     * @return a int
     */
    public int getRolesAsInteger() {
        var rolesAsInt = getAttribute(HiOrgServerProfileDefinition.ROLES, Integer.class);
        if (rolesAsInt != null) {
            return rolesAsInt;
        } else {
            return 0;
        }
    }

    /**
     * <p>hasRole.</p>
     *
     * @param roleId a int
     * @return a boolean
     */
    public boolean hasRole(int roleId) {
        return (getRolesAsInteger() & roleId) == roleId;
    }

    /**
     * <p>getOrganisationId.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getOrganisationId() {
        return getAttribute(HiOrgServerProfileDefinition.ORGANISATION_ID, String.class);
    }

    /**
     * <p>getOrganisationName.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getOrganisationName() {
        return getAttribute(HiOrgServerProfileDefinition.ORGANISATION_NAME, String.class);
    }

    /**
     * <p>getPosition.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getPosition() {
        return getAttribute(HiOrgServerProfileDefinition.POSITION, String.class);
    }

    /**
     * <p>isLeader.</p>
     *
     * @return a boolean
     */
    public boolean isLeader() {
        return getAttribute(HiOrgServerProfileDefinition.LEADER, Boolean.class);
    }

    /**
     * An alternative identifier to uniquely identify a HiOrg-Server user.
     *
     * @return username and the organisation id, joined by an at-sign, lowercase
     */
    public String getAlternativeId() {
        var alternativeId = getUsername() + "@" + getOrganisationId();
        return alternativeId.toLowerCase();
    }

    /**
     * A alternative, typed identifier based on {@link #getAlternativeId()}.
     *
     * @return the class name and {@link #getAlternativeId()}, joined by the seperator constant
     */
    public String getTypedAlternativeId() {
        return this.getClass().getName() + Pac4jConstants.TYPED_ID_SEPARATOR + getAlternativeId();
    }
}
