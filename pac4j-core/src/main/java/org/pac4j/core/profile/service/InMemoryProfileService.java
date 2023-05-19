package org.pac4j.core.profile.service;

import lombok.val;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.factory.ProfileFactory;
import org.pac4j.core.util.serializer.JsonSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * In-memory profile service.
 *
 * @author Elie Roux
 * @since 2.1.0
 */
public class InMemoryProfileService<U extends CommonProfile> extends AbstractProfileService<U>  {

    public Map<String,Map<String,Object>> profiles;
    public ProfileFactory profileFactory;

    /**
     * <p>Constructor for InMemoryProfileService.</p>
     *
     * @param profileFactory a {@link ProfileFactory} object
     */
    public InMemoryProfileService(final ProfileFactory profileFactory) {
        this(new HashMap<>(), profileFactory);
    }

    /**
     * <p>Constructor for InMemoryProfileService.</p>
     *
     * @param profiles a {@link Map} object
     * @param profileFactory a {@link ProfileFactory} object
     */
    public InMemoryProfileService(final Map<String,Map<String,Object>> profiles, final ProfileFactory profileFactory) {
        this.profiles = profiles;
        this.profileFactory = profileFactory;
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotNull("passwordEncoder", getPasswordEncoder());

        setProfileDefinitionIfUndefined(new CommonProfileDefinition(profileFactory));
        setSerializer(new JsonSerializer(CommonProfile.class));

        super.internalInit(forceReinit);
    }

    /** {@inheritDoc} */
    @Override
    protected void insert(final Map<String, Object> attributes) {
        val id = (String) attributes.get(getIdAttribute());
        logger.debug("Inserting doc id: {} with attributes: {}", id, attributes);
        profiles.put(id, attributes);
    }

    /** {@inheritDoc} */
    @Override
    protected void update(final Map<String, Object> attributes) {
        val id = (String) attributes.get(getIdAttribute());
        logger.debug("Updating id: {} with attributes: {}", id, attributes);
        val profile = profiles.get(id);
        if (profile != null) {
            profile.putAll(attributes);
        } else {
            profiles.put(id, attributes);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void deleteById(final String id) {
        logger.debug("Delete id: {}", id);
        profiles.remove(id);
    }

    private Map<String, Object> populateAttributes(final Map<String, Object> rowAttributes, final Collection<String> names) {
        return rowAttributes.entrySet().stream()
                .filter(p -> names == null || names.contains(p.getKey()))
                // not using Collators.toMap because of
                // https://stackoverflow.com/questions/24630963/java-8-nullpointerexception-in-collectors-tomap
                .collect(HashMap::new, (m,v)->m.put(v.getKey(), v.getValue()), HashMap::putAll);
    }

    /** {@inheritDoc} */
    @Override
    protected List<Map<String, Object>> read(final List<String> names, final String key, final String value) {
        logger.debug("Reading key / value: {} / {}", key, value);
        final List<Map<String, Object>> listAttributes;
        if (key.equals(getIdAttribute())) {
            listAttributes = new ArrayList<>();
            val profile = profiles.get(value);
            if (profile != null) {
                listAttributes.add(populateAttributes(profile, names));
            }
        } else {
            listAttributes = profiles.values().stream()
                    .filter(stringObjectMap -> stringObjectMap.get(key) != null && stringObjectMap.get(key).equals(value))
                    .map(stringObjectMap -> populateAttributes(stringObjectMap, names))
                    .collect(Collectors.toList());
        }
        logger.debug("Found: {}", listAttributes);
        return listAttributes;
    }
}
