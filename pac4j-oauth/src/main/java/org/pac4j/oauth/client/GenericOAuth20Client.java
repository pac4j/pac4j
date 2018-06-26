package org.pac4j.oauth.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.github.scribejava.core.model.Verb;
import org.pac4j.core.profile.converter.AbstractAttributeConverter;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.generic.GenericOAuth20ProfileDefinition;
import org.pac4j.scribe.builder.api.GenericApi20;

/**
 * <p>This class is a generic OAuth2 client to authenticate users in a standard OAuth2 server.</p>
 * <p>All configuration parameters can be specified setting the corresponding attribute.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.OAuth20Profile}.</p>
 *
 * @author Julio Arrebola
 */
public class GenericOAuth20Client extends OAuth20Client<OAuth20Profile> {

    private static final Logger LOG = Logger.getLogger(GenericOAuth20Client.class.getName());

    private String authUrl;
    private String tokenUrl;
    private String profileUrl;
    private String profilePath;
    private Verb profileVerb;
    private Map<String, String> profileAttrs;
    private Map<String, String> customParams;
    private Class[] converterClasses;

    public GenericOAuth20Client() {
    }

    @Override
    protected void clientInit() {

        LOG.info("InternalInit");

        GenericApi20 genApi = new GenericApi20(authUrl, tokenUrl);
        configuration.setApi(genApi);

        configuration.setCustomParams(customParams);

        GenericOAuth20ProfileDefinition profileDefinition = new GenericOAuth20ProfileDefinition();
        profileDefinition.setFirstNodePath(profilePath);
        profileDefinition.setProfileVerb(profileVerb);
        profileDefinition.setProfileUrl(profileUrl);

        if (profileAttrs != null) {
            for (Map.Entry<String, String> entry : profileAttrs.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                String[] tokens = value.split("\\|");
                if (tokens.length == 2) {
                    profileDefinition.profileAttribute(key, tokens[1], getConverter(tokens[0]));
                } else if (tokens.length == 1) {
                    profileDefinition.profileAttribute(key, value, null);
                } else {
                    LOG.warning("Ignored incorrect attribute value expressions:" + value);
                }
            }
        }

        configuration.setProfileDefinition(profileDefinition);

        super.clientInit();
    }

    private Class[] getConverters() {
        if (converterClasses == null) {
            try {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                Class cla = classLoader.getClass();
                while (cla != ClassLoader.class) {
                    cla = cla.getSuperclass();
                }
                Field field = cla.getDeclaredField("classes");
                field.setAccessible(true);
                Vector<Class> classes = (Vector<Class>) field.get(classLoader);
                converterClasses = classes.stream()
                    .filter(x -> AbstractAttributeConverter.class.isAssignableFrom(x) && !Modifier.isAbstract(x.getModifiers()))
                    .toArray(Class[]::new);
                return converterClasses;
            } catch (Exception e) {
                LOG.warning(e.toString());
            }
        }
        return converterClasses;
    }

    private AbstractAttributeConverter getConverter(final String typeName) {
        try {
            Stream<Class> acceptableConverters = Arrays.stream(getConverters())
                .filter(x -> {
                    try {
                        AbstractAttributeConverter<?> converter = (AbstractAttributeConverter<?>) x.getDeclaredConstructor().newInstance();
                        Method accept = AbstractAttributeConverter.class.getDeclaredMethod("accept", String.class);
                        return (Boolean) accept.invoke(converter, typeName);
                    } catch (Exception e) {
                        LOG.warning("Ignore type which no parameterless constructor:" + x.getName());
                    }
                    return false;
                });
            Class converterClazz = acceptableConverters.findFirst().get();
            return (AbstractAttributeConverter<?>) converterClazz.newInstance();
        } catch (Exception e) {
            LOG.warning(e.toString());
        }
        return null;
    }

    public void setAuthUrl(final String authUrl) {
        this.authUrl = authUrl;
    }

    public void setTokenUrl(final String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public void setProfileUrl(final String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void setProfileNodePath(final String profilePath) {
        this.profilePath = profilePath;
    }

    public void setProfileVerb(final Verb profileVerb) {
        this.profileVerb = profileVerb;
    }

    public void setProfileAttrs(final Map<String, String> profileAttrsMap) {
        this.profileAttrs = profileAttrsMap;
    }

    public void setCustomParams(final Map<String, String> customParamsMap) {
        this.customParams = customParamsMap;
    }
}
