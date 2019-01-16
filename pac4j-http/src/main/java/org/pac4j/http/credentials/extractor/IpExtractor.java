package org.pac4j.http.credentials.extractor;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.util.CommonHelper;

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
public class IpExtractor implements CredentialsExtractor<TokenCredentials> {

    private List<String> alternateIpHeaders = Collections.emptyList();

    private String proxyIp = "";

    public IpExtractor() {}

    public IpExtractor(String... alternateIpHeaders) {
        this.alternateIpHeaders = Arrays.asList(alternateIpHeaders);
    }

    @Override
    public Optional<TokenCredentials> extract(WebContext context) {
        final Optional<String> ip;
        if (alternateIpHeaders.isEmpty()) {
            ip = Optional.ofNullable(context.getRemoteAddr());
        } else {
            String requestSourceIp = context.getRemoteAddr();
            if (this.proxyIp.isEmpty()) {
                ip = ipFromHeaders(context);
            }
            // if using proxy, check if the ip proxy is correct
            else if (this.proxyIp.equals(requestSourceIp)) {
                ip = ipFromHeaders(context);
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
        String ip;
        for (String header : alternateIpHeaders) {
            ip = context.getRequestHeader(header);
            if (ip != null && !ip.isEmpty()) {
                return Optional.of(ip);
            }
        }
        return Optional.empty();
    }

    /**
     * @return The verified proxy IP
     * @since 2.1.0
     */
    public String getProxyIp() {
        return proxyIp;
    }

    /**
     * @param proxyIp Set the IP to verify the proxy request source.
     *               Setting {@code null} or {@code ""} (empty string) disabled the proxy IP check.
     * @since 2.1.0
     */
    public void setProxyIp(String proxyIp) {
        this.proxyIp = proxyIp == null ? "" : proxyIp;
    }

    /**
     * @return Defined headers to search for IP as {@link Collections#unmodifiableList(List)}
     * @since 2.1.0
     */
    public List getAlternateIpHeaders() {
        return Collections.unmodifiableList(this.alternateIpHeaders);
    }

    /**
     * @param alternateIpHeaders Sets alternate headers to search for IP.
     *                           The first match will be returned as specified for {@code enhanced for} iteration over arrays.
     * @since 2.1.0
     */
    public void setAlternateIpHeaders(final String... alternateIpHeaders) {
        CommonHelper.assertNotNull("alternateIpHeaders", alternateIpHeaders);
        this.alternateIpHeaders = Arrays.asList(alternateIpHeaders);
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "alternateIpHeaders", Arrays.asList(this.alternateIpHeaders));
    }
}
