package org.pac4j.kerberos.credentials.extractor;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.kerberos.credentials.KerberosCredentials;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * To extract Kerberos headers.
 *
 * @author Garry Boyce
 * @since 2.1.0
 */
public class KerberosExtractor implements CredentialsExtractor<KerberosCredentials> {
    private final String clientName;

    public KerberosExtractor(final String clientName) {
        this.clientName = clientName;
    }

    @Override
    public KerberosCredentials extract(WebContext context) throws CredentialsException, HttpAction {
        final String header = context.getRequestHeader(HttpConstants.AUTHORIZATION_HEADER);
        if (header == null) {
            throw HttpAction.unauthorizedNegotiate("Kerberos Header not found", context);
        }

        if (!(header.startsWith("Negotiate ") || header.startsWith("Kerberos "))) {
            throw new CredentialsException("Wrong prefix for header: " + HttpConstants.AUTHORIZATION_HEADER);
        }

        byte[] base64Token = header.substring(header.indexOf(" ") + 1).getBytes(StandardCharsets.UTF_8);
        byte[] kerberosTicket = Base64.getDecoder().decode(base64Token);

        return new KerberosCredentials(kerberosTicket, clientName);

    }

}
