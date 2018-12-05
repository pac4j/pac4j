package org.pac4j.oauth.profile.hiorgserver;

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

    @Override
    public String getUsername() {
        return getAttribute(HiOrgServerProfileDefinition.USERNAME, String.class);
    }

    @Override
    public String getFirstName() {
        return getAttribute(HiOrgServerProfileDefinition.FIRST_NAME, String.class);
    }

    @Override
    public String getFamilyName() {
        return getAttribute(HiOrgServerProfileDefinition.NAME, String.class);
    }

    @Override
    public String getDisplayName() {
        return getAttribute(HiOrgServerProfileDefinition.FULL_NAME, String.class);
    }

    public int getRolesAsInteger() {
        Integer rolesAsInt = getAttribute(HiOrgServerProfileDefinition.GROUP, Integer.class);
        if (rolesAsInt != null) {
            return rolesAsInt;
        } else {
            return 0;
        }
    }

    public boolean hasRole(int groupId) {
        return (getRolesAsInteger() & groupId) == groupId;
    }

    public String getOrganisationId() {
        return getAttribute(HiOrgServerProfileDefinition.ORGANISATION_ID, String.class);
    }

    public String getOrganisationName() {
        return getAttribute(HiOrgServerProfileDefinition.ORGANISATION_NAME, String.class);
    }

    public String getPosition() {
        return getAttribute(HiOrgServerProfileDefinition.POSITION, String.class);
    }

    public boolean isLeader() {
        return getAttribute(HiOrgServerProfileDefinition.LEADER, Boolean.class);
    }

    /** An alternative identifier to uniquely identify a HiOrg-Server user.
     * 
     * @return username and the organisation id, joined by an at-sign
     */
    public String getAlternativeId() {
        return getUsername() + "@" + getOrganisationId();
    }
    
    /**
     * A alternative, typed identifier based on {@link #getAlternativeId()}.
     * 
     * @return the class name and {@link #getAlternativeId()}, joined by the seperator constant
     */
    public String getTypedAlternativeId() {
        return this.getClass().getName() + SEPARATOR + getAlternativeId();
    }

}
