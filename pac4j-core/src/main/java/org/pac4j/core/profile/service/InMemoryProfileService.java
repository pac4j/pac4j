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

/**
 * In-memory profile service.
 *
 * @author Elie Roux
 * @since 2.0.0
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
		profiles.put(id, attributes);
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
		final List<Map<String, Object>> listAttributes = new ArrayList<>();
		if (key.equals(getIdAttribute())) {
			final Map<String,Object> profile = profiles.get(value);
			if (profile != null) {
				listAttributes.add(populateAttributes(profile, names));
			}
		} else {
			for (Map<String,Object> profile : profiles.values()) {
				if (profile.get(key).equals(value)) {
					listAttributes.add(populateAttributes(profile, names));
				}
			}
		}
		logger.debug("Found: {}", listAttributes);
		return listAttributes;
	}

}
