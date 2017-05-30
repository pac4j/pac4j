package org.pac4j.http.credentials.extractor;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * To extract a remote IP address.
 * Search for headers as defined in an array.
 * The first match will be returned as specified for {@code enhanced for} iteration over arrays.
 *
 * @author Guilherme I F L Weizenmann
 * @since 2.1.0
 */
public class IpHeaderChainExtractor implements CredentialsExtractor<TokenCredentials> {

    private final String clientName;

    private List<String> alternateIpHeaders = Collections.emptyList();

    public IpHeaderChainExtractor(final String clientName) {
        this.clientName = clientName;
    }

    public TokenCredentials extract(WebContext context) throws HttpAction {
        final String ip;
        if (alternateIpHeaders.isEmpty()) {
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

    /**
     * @return Defined headers to search for IP as {@link Collections#unmodifiableList(List)}
     */
    public List getAlternateIpHeaders() {
        return Collections.unmodifiableList(this.alternateIpHeaders);
    }

    /**
     * @param alternateIpHeaders Sets alternate headers to search for IP.
     *                           The first match will be returned as specified for {@code enhanced for} iteration over arrays.
     */
    public void setAlternateIpHeader(final String... alternateIpHeaders) {
        Objects.requireNonNull(alternateIpHeaders, "Ip headers must be not null");
        this.alternateIpHeaders = Arrays.asList(alternateIpHeaders);
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "clientName", this.clientName, "alternateIpHeaders", Arrays.asList(this.alternateIpHeaders));
    }
}
