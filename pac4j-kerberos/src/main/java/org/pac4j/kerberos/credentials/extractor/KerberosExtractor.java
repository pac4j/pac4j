package org.pac4j.kerberos.credentials.extractor;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.pac4j.kerberos.credentials.KerberosCredentials;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.DeferredHttpAction;
import org.pac4j.core.exception.DeferredHttpActionCallback;

/**
 * To extract Kerberos headers.
 *
 * @author Garry Boyce
 * @since 1.9.1
 */
public class KerberosExtractor implements CredentialsExtractor<KerberosCredentials> {
    private final String clientName;

    public KerberosExtractor(final String clientName) {
        this.clientName = clientName;
    }

    @Override
    public KerberosCredentials extract(WebContext context) {
        final String header = context.getRequestHeader(HttpConstants.AUTHORIZATION_HEADER);
        if (header == null) {
            throw DeferredHttpAction.deferredHttpAction("Kerberos Header not found", new DeferredHttpActionCallback() {
				
				@Override
				public void execute(WebContext context) {
					// request additional information from browser
			        context.setResponseHeader("WWW-Authenticate", "Negotiate");
			        context.setResponseStatus(HttpConstants.UNAUTHORIZED);
				}
			});
        }

        if (!(header.startsWith("Negotiate ") || header.startsWith("Kerberos "))) {
            throw new CredentialsException("Wrong prefix for header: " + HttpConstants.AUTHORIZATION_HEADER);
        }
        try {
            byte[] base64Token = header.substring(header.indexOf(" ") + 1).getBytes("UTF-8");
            byte[] kerberosTicket =  Base64.getDecoder().decode(base64Token);

            return new KerberosCredentials(kerberosTicket, clientName);
        } catch (UnsupportedEncodingException e) {
            throw new TechnicalException(e);
        }
    }

}
