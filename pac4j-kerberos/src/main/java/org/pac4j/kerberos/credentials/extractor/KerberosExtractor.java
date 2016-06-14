package org.pac4j.kerberos.credentials.extractor;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.http.credentials.extractor.Extractor;
import org.pac4j.kerberos.credentials.KerberosCredentials;


/**
 * To extract Kerberos headers.
 *
 * @author Garry Boyce
 * @since 1.8.10
 */
public class KerberosExtractor implements Extractor<KerberosCredentials> {
    private final String clientName;

    public KerberosExtractor(final String clientName) {
        this.clientName = clientName;
    }

    @Override
    public KerberosCredentials extract(WebContext context) {
        final String header = context.getRequestHeader(HttpConstants.AUTHORIZATION_HEADER);
        if (header == null) {
            return null;
        }

        if (!(header.startsWith("Negotiate ") || header.startsWith("Kerberos "))) {
            throw new CredentialsException("Wrong prefix for header: " + HttpConstants.AUTHORIZATION_HEADER);
        }
        try {
            byte[] base64Token = header.substring(header.indexOf(" ") + 1).getBytes("UTF-8");
            byte[] kerberosTicket = Base64.decodeBase64(base64Token);

            return new KerberosCredentials(kerberosTicket, clientName);
        } catch (UnsupportedEncodingException e) {
            throw new TechnicalException(e);
        }
    }

}
