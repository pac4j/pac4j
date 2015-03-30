/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.scribe.extractors;

import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;
import org.scribe.tokens.OrcidToken;
import org.scribe.utils.Preconditions;
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
