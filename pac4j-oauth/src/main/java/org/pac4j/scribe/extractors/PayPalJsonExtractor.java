package org.pac4j.scribe.extractors;

import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.extractors.AccessTokenExtractor;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.utils.Preconditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a specific JSON extractor for PayPal. It could be part of the Scribe library.
 * 
 * @author Jerome Leleu
 * @since 1.4.2
 */
public class PayPalJsonExtractor implements AccessTokenExtractor {
    
    private final Pattern accessTokenPattern = Pattern.compile("\"access_token\":\\s*\"(\\S*?)\"");
    
    public Token extract(final String response) {
        Preconditions.checkEmptyString(response, "Cannot extract a token from a null or empty String");
        final Matcher matcher = this.accessTokenPattern.matcher(response);
        if (matcher.find()) {
            return new Token(matcher.group(1), "", response);
        } else {
            throw new OAuthException("Cannot extract an acces token. Response was: " + response);
        }
    }
}
