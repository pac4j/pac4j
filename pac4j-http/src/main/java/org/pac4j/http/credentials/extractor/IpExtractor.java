package org.pac4j.http.credentials.extractor;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;

/**
 * To extract a remote IP address.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class IpExtractor implements CredentialsExtractor<TokenCredentials> {

    private final String clientName;

    private String alternateIpHeader;

    public IpExtractor(final String clientName) {
        this.clientName = clientName;
    }

    public TokenCredentials extract(WebContext context) throws HttpAction {
        final String ip;
        if (alternateIpHeader == null) {
            ip = context.getRemoteAddr();
        } else {
            ip = context.getRequestHeader(alternateIpHeader);
        }

        if (ip == null) {
            return null;
        }

        return new TokenCredentials(ip, clientName);
    }

    public String getAlternateIpHeader() {
        return alternateIpHeader;
    }

    public void setAlternateIpHeader(final String alternateIpHeader) {
        this.alternateIpHeader = alternateIpHeader;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "clientName", this.clientName, "alternateIpHeader", this.alternateIpHeader);
    }
}
