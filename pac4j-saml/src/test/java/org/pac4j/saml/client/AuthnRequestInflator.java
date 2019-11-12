package org.pac4j.saml.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class AuthnRequestInflator {

    /**
     * Extracts the AuthnRequest payload from the encoded URL location.
     * @param location the URL location containing the encoded AuthnRequest payload
     * @return the plain decoded AuthnRequest payload
     */
    public static String getInflatedAuthnRequest(final String location) {
        final List<NameValuePair> pairs = URLEncodedUtils.parse(java.net.URI.create(location), "UTF-8");
        final Inflater inflater = new Inflater(true);
        final byte[] decodedRequest = Base64.getDecoder().decode(pairs.get(0).getValue());
        final ByteArrayInputStream is = new ByteArrayInputStream(decodedRequest);
        final InflaterInputStream inputStream = new InflaterInputStream(is, inflater);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line;
        final StringBuilder bldr = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                bldr.append(line);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return bldr.toString();
    }

}
