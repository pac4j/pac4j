package org.pac4j.cas.profile;

import lombok.ToString;
import org.pac4j.core.profile.CommonProfile;

/**
 * This is {@link org.pac4j.cas.profile.CasRestProfile} that represents
 * the CAS TGT when it's obtained via the rest api.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
@ToString(callSuper = true)
public final class CasRestProfile extends CommonProfile {

    private static final long serialVersionUID = -1688563185891330018L;
    private static final String TGT_KEY = "$tgt_key";

    /**
     * <p>Constructor for CasRestProfile.</p>
     */
    public CasRestProfile() {
    }

    /**
     * <p>Constructor for CasRestProfile.</p>
     *
     * @param ticketGrantingTicketId a {@link java.lang.String} object
     * @param userName a {@link java.lang.String} object
     */
    public CasRestProfile(final String ticketGrantingTicketId, final String userName) {
        super();
        addAttribute(TGT_KEY, ticketGrantingTicketId);
        setId(userName);
    }

    /**
     * <p>getTicketGrantingTicketId.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getTicketGrantingTicketId() {
        return (String) getAttribute(TGT_KEY);
    }

    /** {@inheritDoc} */
    @Override
    public void removeLoginData() {
        removeAttribute(TGT_KEY);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return getTicketGrantingTicketId().hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        return obj instanceof CasRestProfile casRestProfile
            && (obj == this || getTicketGrantingTicketId().equals(casRestProfile.getTicketGrantingTicketId()));
    }
}
