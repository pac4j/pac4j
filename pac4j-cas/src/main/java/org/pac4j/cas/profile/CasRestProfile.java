package org.pac4j.cas.profile;

import org.pac4j.core.profile.CommonProfile;

/**
 * This is {@link CasRestProfile} that represents
 * the CAS TGT when it's obtained via the rest api.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public final class CasRestProfile extends CommonProfile {

    private static final long serialVersionUID = -1688563185891330018L;
    private static final String TGT_KEY = "$tgt_key";

    public CasRestProfile() {
    }

    public CasRestProfile(final String ticketGrantingTicketId, final String userName) {
        super();
        addAttribute(TGT_KEY, ticketGrantingTicketId);
        setId(userName);
    }

    public String getTicketGrantingTicketId() {
        return (String) getAttribute(TGT_KEY);
    }

    @Override
    public void removeLoginData() {
        removeAttribute(TGT_KEY);
    }

    @Override
    public int hashCode() {
        return getTicketGrantingTicketId().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        return obj instanceof CasRestProfile casRestProfile
            && (obj == this || getTicketGrantingTicketId().equals(casRestProfile.getTicketGrantingTicketId()));
    }
}
