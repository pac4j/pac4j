package org.pac4j.oauth.client;

import com.github.scribejava.core.model.Verb;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.profile.converter.AbstractAttributeConverter;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.oauth.profile.generic.GenericOAuth20ProfileDefinition;
import org.pac4j.scribe.builder.api.GenericApi20;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>This class is a generic OAuth2 client to authenticate users in a standard OAuth2 server.</p>
 * <p>All configuration parameters can be specified setting the corresponding attribute.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.OAuth20Profile}.</p>
 *
 * @author Julio Arrebola
 */
@Slf4j
@Getter
@Setter
public class GenericOAuth20Client extends OAuth20Client {

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

    private List<Class<? extends AbstractAttributeConverter>> converterClasses;

    /**
     * <p>Constructor for GenericOAuth20Client.</p>
     */
    public GenericOAuth20Client() {
    }

    private static List<Class<? extends AbstractAttributeConverter>> findAttributeConverterClasses() {
        try {
            var reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(AttributeConverter.class.getPackageName())));

            var subTypes = reflections.getSubTypesOf(AbstractAttributeConverter.class);
            return subTypes.stream()
                .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.warn(e.toString());
        }
        return new ArrayList<>(0);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        this.converterClasses = findAttributeConverterClasses();

        val genApi = new GenericApi20(authUrl, tokenUrl);
        configuration.setApi(genApi);

        if (clientAuthenticationMethod != null) {
            genApi.setClientAuthenticationMethod(clientAuthenticationMethod);
        }

        configuration.setCustomParams(customParams);

        var profileDefinition = new GenericOAuth20ProfileDefinition();
        profileDefinition.setFirstNodePath(profilePath);
        profileDefinition.setProfileVerb(profileVerb);
        profileDefinition.setProfileUrl(profileUrl);

        profileDefinition.setProfileId(Objects.requireNonNullElse(profileId, "id"));
        if (profileAttrs != null) {
            for (var entry : profileAttrs.entrySet()) {
                var key = entry.getKey();
                var value = entry.getValue();
                var tokens = value.split("\\|");
                if (tokens.length == 2) {
                    profileDefinition.profileAttribute(key, tokens[1], getConverter(tokens[0]));
                } else if (tokens.length == 1) {
                    profileDefinition.profileAttribute(key, value, null);
                } else {
                    LOGGER.warn("Ignored incorrect attribute value expressions: {}", value);
                }
            }
        }

        configuration.setProfileDefinition(profileDefinition);
        configuration.setScope(scope);
        configuration.setWithState(withState);

        super.internalInit(forceReinit);
    }

    AbstractAttributeConverter getConverter(final String typeName) {
        try {
            var acceptableConverters = this.converterClasses.stream()
                .filter(x -> {
                    try {
                        var converter = (AbstractAttributeConverter) x.getDeclaredConstructor().newInstance();
                        var accept = AbstractAttributeConverter.class.getDeclaredMethod("accept", String.class);
                        return (Boolean) accept.invoke(converter, typeName);
                    } catch (ReflectiveOperationException e) {
                        LOGGER.warn("Ignore type which no parameterless constructor:" + x.getName());
                    }
                    return false;
                });
            var converterClazz = acceptableConverters.findFirst().orElse(null);
            if (converterClazz != null) {
                return converterClazz.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            LOGGER.warn(e.toString());
        }
        return null;
    }
    /**
     * <p>getConverters.</p>
     *
     * @return a {@link List} object
     */
    public List<Class<? extends AbstractAttributeConverter>> getConverters() {
        return List.copyOf(converterClasses);
    }

    /**
     * Add attribute converter.
     *
     * @param converter the converter
     */
    public void addAttributeConverter(final Class<AbstractAttributeConverter> converter) {
        this.converterClasses.add(converter);
    }
}
