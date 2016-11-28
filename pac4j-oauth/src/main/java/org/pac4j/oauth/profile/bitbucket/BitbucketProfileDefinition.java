package org.pac4j.oauth.profile.bitbucket;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.definition.CommonProfileDefinition;

import java.util.Arrays;

/**
 * This class is the Bitbucket profile definition.
 * 
 * @author Sebastian Sdorra
 * @since 1.5.1
 */
public class BitbucketProfileDefinition extends CommonProfileDefinition<BitbucketProfile> {

    public static final String LAST_NAME = "last_name";
    public static final String IS_TEAM = "is_team";
    public static final String AVATAR = "avatar";
    public static final String RESOURCE_URI = "resource_uri";

    public BitbucketProfileDefinition() {
        super(x -> new BitbucketProfile());
        Arrays.stream(new String[] {Pac4jConstants.USERNAME, LAST_NAME, IS_TEAM})
                .forEach(a -> primary(a, Converters.STRING));
        primary(IS_TEAM, Converters.BOOLEAN);
        primary(AVATAR, Converters.URL);
        primary(RESOURCE_URI, Converters.URL);
    }
}
