package org.scribe.up.addon_to_scribe;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.scribe.exceptions.OAuthException;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.Token;
import org.scribe.utils.Preconditions;

/**
 * This class represents a specific JSON extractor for Google using OAuth protocol version 2. It should be implemented natively in Scribe in
 * further release.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class Google2JsonExtractor implements AccessTokenExtractor {
    
    private final Pattern accessTokenPattern = Pattern.compile("\"access_token\"\\s*:\\s*\"(\\S*?)\"");
    
    public Token extract(final String response) {
        Preconditions.checkEmptyString(response, "Cannot extract a token from a null or empty String");
        final Matcher matcher = accessTokenPattern.matcher(response);
        if (matcher.find()) {
            return new Token(matcher.group(1), "", response);
        } else {
            throw new OAuthException("Cannot extract an acces token. Response was: " + response);
        }
    }
}
