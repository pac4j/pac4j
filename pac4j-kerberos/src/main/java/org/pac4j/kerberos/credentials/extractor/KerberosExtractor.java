package org.pac4j.kerberos.credentials.extractor;

import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.kerberos.credentials.KerberosCredentials;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

/**
 * To extract Kerberos headers.
 *
 * @author Garry Boyce
 * @since 2.1.0
 */
public class KerberosExtractor implements CredentialsExtractor {

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> extract(final CallContext ctx) {
        val optHeader = ctx.webContext().getRequestHeader(HttpConstants.AUTHORIZATION_HEADER);
        if (optHeader.isEmpty()) {
            return Optional.empty();
        }

        val header = optHeader.get();
        if (!(header.startsWith("Negotiate ") || header.startsWith("Kerberos "))) {
            // "Authorization" header do not indicate Kerberos mechanism yet,
            // so the extractor shouldn't throw an exception
            return Optional.empty();
        }

        var base64Token = header.substring(header.indexOf(" ") + 1).getBytes(StandardCharsets.UTF_8);
        var kerberosTicket = Base64.getDecoder().decode(base64Token);

        return Optional.of(new KerberosCredentials(kerberosTicket));
    }
}
