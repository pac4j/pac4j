package org.pac4j.core.profile.service;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.util.CommonHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * In-memory profile service.
 *
 * @author Elie Roux
 * @since 2.1.0
 */

public class InMemoryProfileService<U extends CommonProfile> extends AbstractProfileService<U>  {

    public Map<String,Map<String,Object>> profiles;
    public Function<Object[], U> profileFactory;

    public InMemoryProfileService(final Function<Object[], U> profileFactory) {
        this(new HashMap<>(), profileFactory);
    }

    public InMemoryProfileService(final Map<String,Map<String,Object>> profiles, final Function<Object[], U> profileFactory) {
        this.profiles = profiles;
        this.profileFactory = profileFactory;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("passwordEncoder", getPasswordEncoder());
        defaultProfileDefinition(new CommonProfileDefinition<U>(profileFactory));
        super.internalInit(context);
    }

    @Override
    protected void insert(final Map<String, Object> attributes) {
        final String id = (String) attributes.get(getIdAttribute());
        logger.debug("Inserting doc id: {} with attributes: {}", id, attributes);
        profiles.put(id, attributes);
    }

    @Override
    protected void update(final Map<String, Object> attributes) {
        final String id = (String) attributes.get(getIdAttribute());
        logger.debug("Updating id: {} with attributes: {}", id, attributes);
        final Map<String,Object> profile = profiles.get(id);
        if (profile != null) {
            profile.putAll(attributes);
        } else {
            profiles.put(id, attributes);
        }
    }

    @Override
    protected void deleteById(final String id) {
        logger.debug("Delete id: {}", id);
        profiles.remove(id);
    }

    private Map<String, Object> populateAttributes(final Map<String, Object> rowAttributes, final List<String> names) {
        return rowAttributes.entrySet().stream()
                .filter(p -> names == null || names.contains(p.getKey()))
                // not using Collators.toMap because of
                // https://stackoverflow.com/questions/24630963/java-8-nullpointerexception-in-collectors-tomap
                .collect(HashMap::new, (m,v)->m.put(v.getKey(), v.getValue()), HashMap::putAll);
    }

    @Override
    protected List<Map<String, Object>> read(final List<String> names, final String key, final String value) {
        logger.debug("Reading key / value: {} / {}", key, value);
        final List<Map<String, Object>> listAttributes;
        if (key.equals(getIdAttribute())) {
            listAttributes = new ArrayList<>();
            final Map<String,Object> profile = profiles.get(value);
            if (profile != null) {
                listAttributes.add(populateAttributes(profile, names));
            }
        } else {
            listAttributes = profiles.entrySet().stream()
                    .filter(p -> p.getValue().get(key) != null && p.getValue().get(key).equals(value))
                    .map(p -> populateAttributes(p.getValue(), names))
                    .collect(Collectors.toList());
        }
        logger.debug("Found: {}", listAttributes);
        return listAttributes;
    }

}
