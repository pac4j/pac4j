package org.pac4j.http.credentials.extractor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * To extract a remote IP address.
 * Search for headers as defined in an array.
 * The first match will be returned as specified for {@code enhanced for} iteration over arrays.
 * By default, if no proxy ip is setted ({@link #setProxyIp(String)}), only request from proxy IP will be accepted.
 *
 * @author Jerome Leleu
 * @author Guilherme I F L Weizenmann
 * @since 1.8.0
 */
@Getter
@Setter
@ToString
public class IpExtractor implements CredentialsExtractor {

    private List<String> alternateIpHeaders = Collections.emptyList();

    @Getter
    private String proxyIp = Pac4jConstants.EMPTY_STRING;

    public IpExtractor() {}

    public IpExtractor(String... alternateIpHeaders) {
        this.alternateIpHeaders = Arrays.asList(alternateIpHeaders);
    }

    @Override
    public Optional<Credentials> extract(final CallContext ctx) {
        val webContext = ctx.webContext();
        final Optional<String> ip;
        if (alternateIpHeaders.isEmpty()) {
            ip = Optional.ofNullable(webContext.getRemoteAddr());
        } else {
            val requestSourceIp = webContext.getRemoteAddr();
            if (this.proxyIp.isEmpty()) {
                ip = ipFromHeaders(webContext);
            }
            // if using proxy, check if the ip proxy is correct
            else if (this.proxyIp.equals(requestSourceIp)) {
                ip = ipFromHeaders(webContext);
            } else {
                ip = Optional.empty();
            }
        }

        if (!ip.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(new TokenCredentials(ip.get()));
    }

    private Optional<String> ipFromHeaders(WebContext context) {
        Optional<String> ip;
        for (var header : alternateIpHeaders) {
            ip = context.getRequestHeader(header);
            if (ip.isPresent() && !ip.get().isEmpty()) {
                return ip;
            }
        }
        return Optional.empty();
    }

    /**
     * @param proxyIp Set the IP to verify the proxy request source.
     *               Setting {@code null} or {@code ""} (empty string) disabled the proxy IP check.
     */
    public void setProxyIp(String proxyIp) {
        this.proxyIp = proxyIp == null ? Pac4jConstants.EMPTY_STRING : proxyIp;
    }

    /**
     * @param alternateIpHeaders Sets alternate headers to search for IP.
     *                           The first match will be returned as specified for {@code enhanced for} iteration over arrays.
     */
    public void setAlternateIpHeaders(final String... alternateIpHeaders) {
        CommonHelper.assertNotNull("alternateIpHeaders", alternateIpHeaders);
        this.alternateIpHeaders = Arrays.asList(alternateIpHeaders);
    }
}
