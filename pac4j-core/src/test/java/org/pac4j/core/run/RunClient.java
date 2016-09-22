package org.pac4j.core.run;

import com.esotericsoftware.kryo.Kryo;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.kryo.ColorSerializer;
import org.pac4j.core.kryo.FormattedDateSerializer;
import org.pac4j.core.kryo.LocaleSerializer;
import org.pac4j.core.profile.*;
import org.pac4j.core.util.JavaSerializationHelper;
import org.pac4j.core.util.KryoSerializationHelper;
import org.pac4j.core.util.TestsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Run manually a test for a client.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public abstract class RunClient implements TestsConstants {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public void run() throws Exception {
        final IndirectClient client = getClient();
        final MockWebContext context = MockWebContext.create();
        final String url = client.getRedirectAction(context).getLocation();
        logger.warn("Redirect to: \n{}", url);
        logger.warn("Use credentials: {} / {}", getLogin(), getPassword());
        if (canCancel()) {
            logger.warn("You can CANCEL the authentication.");
        }
        logger.warn("Returned url (copy/paste the fragment starting before the question mark of the query string):");
        final Scanner scanner = new Scanner(System.in, HttpConstants.UTF8_ENCODING);
        final String returnedUrl = scanner.nextLine().trim();
        populateContextWithUrl(context, returnedUrl);
        final Credentials credentials = client.getCredentials(context);
        final CommonProfile profile = client.getUserProfile(credentials, context);
        logger.debug("userProfile: {}", profile);
        if (profile != null || !canCancel()) {
            verifyProfile(profile);

            logger.warn("## Java serialization");
            // Java serialization
            final JavaSerializationHelper javaSerializationHelper = new JavaSerializationHelper();
            byte[] bytes = javaSerializationHelper.serializeToBytes(profile);
            final CommonProfile profile2 = (CommonProfile) javaSerializationHelper.unserializeFromBytes(bytes);
            verifyProfile(profile2);

            logger.warn("## Kryo serialization");
            // Kryo serialization
            final Kryo kryo = new Kryo();
            kryo.register(HashMap.class);
            kryo.register(Locale.class, new LocaleSerializer());
            kryo.register(Date.class);
            kryo.register(FormattedDate.class, new FormattedDateSerializer());
            kryo.register(Gender.class);
            kryo.register(Color.class, new ColorSerializer());
            kryo.register(ArrayList.class);
            registerForKryo(kryo);
            final KryoSerializationHelper kryoSerializationHelper = new KryoSerializationHelper(kryo);
            bytes = kryoSerializationHelper.serializeToBytes(profile);
            final CommonProfile profile3 = (CommonProfile) kryoSerializationHelper.unserializeFromBytes(bytes);
            verifyProfile(profile3);

            logger.warn("## CAS serialization");
            // CAS serialization
            final Map<String, Object> attributes = profile3.getAttributes();
            final Map<String, Object> newAttributes = new HashMap<>();
            for (final Map.Entry<String, Object> entry: attributes.entrySet()) {
                final String key = entry.getKey();
                final Object value = entry.getValue();
                if (value instanceof List) {
                    final List<String> newList = new ArrayList<>();
                    for (Object o : (List) value) {
                        newList.add(o.toString());
                    }
                    newAttributes.put(key, newList);
                } else {
                    newAttributes.put(key, value.toString());
                }
            }
            final CommonProfile profile4 = ProfileHelper.buildProfile(profile3.getTypedId(), newAttributes);
            verifyProfile(profile4);

            logger.warn("## Auto-rebuild");
            // auto-rebuild
            final CommonProfile profile5 = ProfileHelper.buildProfile(profile3.getTypedId(), attributes);
            verifyProfile(profile5);
        }
        logger.warn("################");
        logger.warn("Test successful!");
    }

    protected abstract String getLogin();

    protected abstract String getPassword();

    protected boolean canCancel() {
        return false;
    }

    protected abstract IndirectClient getClient();

    protected abstract void registerForKryo(final Kryo kryo);

    protected abstract void verifyProfile(final CommonProfile userProfile);

    protected void assertCommonProfile(final CommonProfile profile, final String email, final String firstName,
                                       final String familyName, final String displayName, final String username, final Gender gender,
                                       final Locale locale, final String pictureUrl, final String profileUrl, final String location) {
        assertEquals(email, profile.getEmail());
        assertEquals(firstName, profile.getFirstName());
        assertEquals(familyName, profile.getFamilyName());
        assertEquals(displayName, profile.getDisplayName());
        assertEquals(username, profile.getUsername());
        assertEquals(gender, profile.getGender());
        assertEquals(locale, profile.getLocale());
        if (pictureUrl == null) {
            assertNull(profile.getPictureUrl());
        } else {
            assertTrue(profile.getPictureUrl().contains(pictureUrl));
        }
        if (profileUrl == null) {
            assertNull(profile.getProfileUrl());
        } else {
            final String profUrl = profile.getProfileUrl();
            assertTrue(profUrl.startsWith(profileUrl));
        }
        assertEquals(location, profile.getLocation());
    }

    protected void populateContextWithUrl(final MockWebContext context, String url) {
        int pos = url.indexOf("?");
        if (pos >= 0) {
            url = url.substring(pos + 1);

            // removing stuffs after the hash for a regular query string
            pos = url.indexOf("#");
            if (pos >= 0) {
                url = url.substring(0, pos);
            }
        } else {
            // this is a hack to test client side stuffs, it would not work for server side
            pos = url.indexOf("#");
            if (pos >= 0) {
                url = url.substring(pos + 1);
            }
        }
        final Map<String, String> parameters = new HashMap<>();
        final StringTokenizer st = new StringTokenizer(url, "&");
        while (st.hasMoreTokens()) {
            final String keyValue = st.nextToken();
            final String[] parts = keyValue.split("=");
            if (parts != null && parts.length >= 2) {
                try {
                    parameters.put(parts[0], URLDecoder.decode(parts[1], HttpConstants.UTF8_ENCODING));
                } catch (final UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        context.addRequestParameters(parameters);
    }
}
