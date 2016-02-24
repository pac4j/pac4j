package org.pac4j.scribe.extractors;

import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.extractors.AccessTokenExtractor;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.utils.Preconditions;
import org.pac4j.scribe.model.OrcidToken;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a specific JSON extractor for ORCiD using OAuth protocol version 2. It could be part of the Scribe library.
 *
 * @author Jens Tinglev
 * @since 1.6.0
 */
public class OrcidJsonExtractor implements AccessTokenExtractor {

    private final Pattern accessTokenPattern = Pattern.compile("\"access_token\"\\s*:\\s*\"(\\S*?)\"");
    private final Pattern orcidTokenPattern = Pattern.compile("\"orcid\"\\s*:\\s*\"(\\S*?)\"");

    public Token extract(String response) {
        Preconditions.checkEmptyString(response, "Cannot extract a token from a null or empty String");
        Matcher matcher = this.accessTokenPattern.matcher(response);
        if (matcher.find() && matcher.groupCount() > 0) {
            final String accessToken = matcher.group(1);
            matcher = this.orcidTokenPattern.matcher(response);
            if (matcher.find() && matcher.groupCount() > 0) {
                return new OrcidToken(accessToken, "", matcher.group(1), response);
            } else {
                throw new OAuthException("Cannot extract orcid. Response was: " + response);
            }
        } else {
            throw new OAuthException("Cannot extract an access token. Response was: " + response);
        }
    }
}
