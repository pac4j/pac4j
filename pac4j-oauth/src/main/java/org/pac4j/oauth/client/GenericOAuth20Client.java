package org.pac4j.oauth.client;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.github.scribejava.core.model.Verb;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.converter.AbstractAttributeConverter;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;
import org.pac4j.oauth.profile.generic.GenericOAuth20ProfileDefinition;
import org.pac4j.scribe.builder.api.GenericApi20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This class is a generic OAuth2 client to authenticate users in a standard OAuth2 server.</p>
 * <p>All configuration parameters can be specified setting the corresponding attribute.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.OAuth20Profile}.</p>
 * <p> GenericOAuth20Client also supports typed profiles and
 * hooks to override Api, ProfileDefinition, ProfileCreator and LogoutUrl</p>
 *
 * @author Julio Arrebola
 * @author Vassilis Virvilis
 */
public class GenericOAuth20Client<P extends OAuth20Profile> extends OAuth20Client<P> {

    private static final Logger LOG = LoggerFactory.getLogger(GenericOAuth20Client.class);

    private String authUrl;
    private String tokenUrl;
    private String profileUrl;
    private String profilePath;
    private String profileId;
    private String scope;
    private boolean withState;
    private String clientAuthenticationMethod;
    private Verb profileVerb;
    private Map<String, String> profileAttrs;
    private Map<String, String> customParams;
    private Class[] converterClasses;

    private final BiFunction<String, String, ? extends GenericApi20> apiProvider;
    private final Supplier<? extends GenericOAuth20ProfileDefinition> profileDefinitionProvider;
    private final BiFunction<OAuth20Configuration, IndirectClient, ? extends OAuth20ProfileCreator<P>> profileCreatorProvider;
    private final Supplier<String> logoutUrlProvider;

    public GenericOAuth20Client(BiFunction<String, String, ? extends GenericApi20> apiProvider,
            Supplier<? extends GenericOAuth20ProfileDefinition> profileDefinitionProvider,
            BiFunction<OAuth20Configuration, IndirectClient, ? extends OAuth20ProfileCreator<P>> profileCreatorProvider,
            Supplier<String> logoutUrlProvider) {
        this.apiProvider = apiProvider;
        this.profileDefinitionProvider = profileDefinitionProvider;
        this.profileCreatorProvider = profileCreatorProvider;
        this.logoutUrlProvider = logoutUrlProvider;
    }

    public GenericOAuth20Client() {
        this(null, null, null, null);
    }

    @Override
    protected void clientInit() {

        LOG.info("InternalInit");

        GenericApi20 genApi = apiProvider != null ? apiProvider.apply(authUrl, tokenUrl) : new GenericApi20(authUrl, tokenUrl);
        configuration.setApi(genApi);

        if (clientAuthenticationMethod != null) {
            genApi.setClientAuthenticationMethod(clientAuthenticationMethod);
        }

        configuration.setCustomParams(customParams);

        GenericOAuth20ProfileDefinition profileDefinition = profileDefinitionProvider != null ? profileDefinitionProvider.get()
                : new GenericOAuth20ProfileDefinition();
        profileDefinition.setFirstNodePath(profilePath);
        profileDefinition.setProfileVerb(profileVerb);
        profileDefinition.setProfileUrl(profileUrl);

        if (profileId != null) {
            profileDefinition.setProfileId(profileId);
        }
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
                    LOG.warn("Ignored incorrect attribute value expressions:" + value);
                }
            }
        }

        configuration.setProfileDefinition(profileDefinition);

        configuration.setScope(scope);
        configuration.setWithState(withState);

        if (profileCreatorProvider != null)
            defaultProfileCreator(profileCreatorProvider.apply(configuration, this));
        if (logoutUrlProvider != null)
            defaultLogoutActionBuilder((ctx, profile, targetUrl) -> RedirectAction.redirect(logoutUrlProvider.get()));

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
            } catch (IllegalAccessException | NoSuchFieldException e) {
                LOG.warn(e.toString());
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
                    } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                        LOG.warn("Ignore type which no parameterless constructor:" + x.getName());
                    }
                    return false;
                });
            Class converterClazz = acceptableConverters.findFirst().get();
            return (AbstractAttributeConverter<?>) converterClazz.newInstance();
        } catch (Exception e) {
            LOG.warn(e.toString());
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

    public void setProfileId(final String profileId) {
        this.profileId = profileId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(final String scope) {
        this.scope = scope;
    }

    public boolean isWithState() {
        return withState;
    }

    public void setWithState(final boolean withState) {
        this.withState = withState;
    }

    public String getClientAuthenticationMethod() {
        return clientAuthenticationMethod;
    }

    public void setClientAuthenticationMethod(final String clientAuthenticationMethod) {
        this.clientAuthenticationMethod = clientAuthenticationMethod;
    }
}
