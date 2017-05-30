package org.pac4j.http.credentials.extractor;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;

import java.util.Arrays;
import java.util.Objects;

/**
 * To extract a remote IP address.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class IpHeaderChainExtractor implements CredentialsExtractor<TokenCredentials> {

    private static final String[] EMPTY_ARRAY = {};

    private final String clientName;

    private String[] alternateIpHeaders = EMPTY_ARRAY;

    public IpHeaderChainExtractor(final String clientName) {
        this.clientName = clientName;
    }

    public TokenCredentials extract(WebContext context) throws HttpAction {
        final String ip;
        if (alternateIpHeaders.length == 0) {
            ip = context.getRemoteAddr();
        } else {
            ip = ipFromHeaders(context);
        }

        if (ip == null) {
            return null;
        }

        return new TokenCredentials(ip, clientName);
    }

    private String ipFromHeaders(WebContext context) {
        String ip;
        for (String header : alternateIpHeaders) {
            ip = context.getRequestHeader(header);
            if (ip != null && !ip.isEmpty()) {
                return ip;
            }
        }
        return null;
    }

    public String[] getAlternateIpHeaders() {
        return alternateIpHeaders;
    }

    public void setAlternateIpHeader(final String... alternateIpHeaders) {
        Objects.requireNonNull(alternateIpHeaders, "Ip headers must be not null");
        this.alternateIpHeaders = alternateIpHeaders;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "clientName", this.clientName, "alternateIpHeaders", Arrays.asList(this.alternateIpHeaders));
    }
}
