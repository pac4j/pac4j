package org.pac4j.kerberos.credentials.extractor;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
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

    @Override
    public KerberosCredentials extract(final WebContext context) {
        final String header = context.getRequestHeader(HttpConstants.AUTHORIZATION_HEADER);
        if (header == null) {
            return null;
        }

        if (!(header.startsWith("Negotiate ") || header.startsWith("Kerberos "))) {
            // "Authorization" header do not indicate Kerberos mechanism yet,
            // so the extractor shouldn't throw an exception
            return null;
        }

        byte[] base64Token = header.substring(header.indexOf(" ") + 1).getBytes(StandardCharsets.UTF_8);
        byte[] kerberosTicket = Base64.getDecoder().decode(base64Token);

        return new KerberosCredentials(kerberosTicket);
    }
}
