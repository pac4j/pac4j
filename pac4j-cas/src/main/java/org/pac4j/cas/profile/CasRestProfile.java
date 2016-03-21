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

    private String ticketGrantingTicketId;

    public CasRestProfile() { }

    public CasRestProfile(final String ticketGrantingTicketId, final String userName) {
        super();
        this.ticketGrantingTicketId = ticketGrantingTicketId;
        setId(userName);
    }

    public String getTicketGrantingTicketId() {
        return ticketGrantingTicketId;
    }

    @Override
    public void clearSensitiveData() {
        this.ticketGrantingTicketId = null;
    }

    @Override
    public int hashCode() {
        return this.ticketGrantingTicketId.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof CasRestProfile)) {
            return false;
        }

        return obj == this ||
                this.ticketGrantingTicketId.equals(((CasRestProfile)obj).ticketGrantingTicketId);
    }
}
