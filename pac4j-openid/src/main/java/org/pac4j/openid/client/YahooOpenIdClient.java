package org.pac4j.openid.client;

import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.openid.profile.yahoo.YahooOpenIdProfileDefinition;
import org.pac4j.openid.profile.yahoo.YahooOpenIdProfile;

/**
 * <p>This class is the OpenID client to authenticate users with their yahoo account.</p>
 * <p>It returns a {@link org.pac4j.openid.profile.yahoo.YahooOpenIdProfile}.</p>
 * 
 * @see org.pac4j.openid.profile.yahoo.YahooOpenIdProfile
 * @author Patrice de Saint Steban
 * @since 1.6.0
 */
public class YahooOpenIdClient extends BaseOpenIdClient<YahooOpenIdProfile> {

    public static final String YAHOO_GENERIC_USER_IDENTIFIER = "https://me.yahoo.com";

    @Override
    protected String getUser(final WebContext context) {
        return YAHOO_GENERIC_USER_IDENTIFIER;
    }

    @Override
    protected void internalInit(final WebContext context) {
        super.internalInit(context);
        setProfileDefinition(new YahooOpenIdProfileDefinition());
    }

    @Override
    protected FetchRequest getFetchRequest() throws MessageException {
        final FetchRequest fetchRequest = FetchRequest.createFetchRequest();
		fetchRequest.addAttribute(CommonProfileDefinition.EMAIL,
				"http://axschema.org/contact/email", true);
		fetchRequest.addAttribute(YahooOpenIdProfileDefinition.FULLNAME,
				"http://axschema.org/namePerson", true);
		fetchRequest.addAttribute(YahooOpenIdProfileDefinition.LANGUAGE,
				"http://axschema.org/pref/language", true);
		fetchRequest.addAttribute(YahooOpenIdProfileDefinition.IMAGE,
				"http://axschema.org/media/image/default", true);
		
		logger.debug("fetchRequest: {}", fetchRequest);
        return fetchRequest;
    }

    @Override
    protected YahooOpenIdProfile createProfile(final AuthSuccess authSuccess) throws MessageException, HttpAction {
        final YahooOpenIdProfile profile = getProfileDefinition().newProfile();

        if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
            final FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);
            for (final String name : getProfileDefinition().getPrimaryAttributes()) {
                getProfileDefinition().convertAndAdd(profile, name, fetchResp.getAttributeValue(name));
            }
        }
        return profile;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "name", getName());
    }
}
