package org.pac4j.oauth.client;

import junit.framework.TestCase;

import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.credentials.OAuthCredentials;

/**
 * This class tests the OAuth credential retrieval in the
 * {@link org.pac4j.oauth.client.VkClient} class.
 * 
 * @author indvdum (gotoindvdum[at]gmail[dot]com)
 * @since 1.5
 * 
 */
public class VkClientIT extends TestCase implements TestsConstants {

	@SuppressWarnings("rawtypes")
	private BaseOAuth20Client getClient() {
		final VkClient client = new VkClient();
		client.setKey(KEY);
		client.setSecret(SECRET);
		client.setCallbackUrl(CALLBACK_URL);
		return client;
	}

	public void testNoCode() throws RequiresHttpAction {
		try {
			getClient().getCredentials(MockWebContext.create());
			fail("should not get credentials");
		} catch (final TechnicalException e) {
			assertEquals("No credential found", e.getMessage());
		}
	}

	public void testOk() throws RequiresHttpAction {
		final OAuthCredentials oauthCredential = (OAuthCredentials) getClient().getCredentials(
				MockWebContext.create().addRequestParameter(BaseOAuth20Client.OAUTH_CODE, CODE));
		assertNotNull(oauthCredential);
		assertEquals(CODE, oauthCredential.getVerifier());
	}

}
