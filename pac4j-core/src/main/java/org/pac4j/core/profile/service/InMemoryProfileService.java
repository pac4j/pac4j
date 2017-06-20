package org.pac4j.core.profile.service;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.util.CommonHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory profile service.
 *
 * @author Elie Roux
 * @since 2.0.0
 */

public class InMemoryProfileService<U extends CommonProfile> extends AbstractProfileService<U>  {

	public Map<String,Map<String,Object>> profiles;
	//public AuthenticatorProfileCreator<UsernamePasswordCredentials, U> creator;
	public Class<U> typeArgumentClass;

	public InMemoryProfileService(Class<U> typeArgumentClass) {
		this(new HashMap<String,Map<String,Object>>(), typeArgumentClass);
	}

	public InMemoryProfileService(Map<String,Map<String,Object>> profiles, Class<U> typeArgumentClass) {
		this.profiles = profiles;
		this.typeArgumentClass = typeArgumentClass;
	}

	@Override
	protected void internalInit(final WebContext context) {
		CommonHelper.assertNotNull("passwordEncoder", getPasswordEncoder());
		defaultProfileDefinition(new CommonProfileDefinition<U>(x -> {
			try {
				return typeArgumentClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new TechnicalException("Unable to instanciate "+typeArgumentClass+", replace with 'CommonProfile' in simple cases");
			}
		}));
		super.internalInit(context);
	}

	@Override
	protected void insert(Map<String, Object> attributes) {
		String id = (String) attributes.get(getIdAttribute());
		logger.debug("Inserting doc id: {} with attributes: {}", id, attributes);
		profiles.put(id, attributes);
	}

	@Override
	protected void update(Map<String, Object> attributes) {
		String id = (String) attributes.get(getIdAttribute());
		logger.debug("Updating id: {} with attributes: {}", id, attributes);
		profiles.put(id, attributes);
	}

	@Override
	protected void deleteById(String id) {
		logger.debug("Delete id: {}", id);
		profiles.remove(id);
	}

	private Map<String, Object> populateAttributes(final Map<String, Object> rowAttributes, final List<String> names) {
		final Map<String, Object> newAttributes = new HashMap<>();
		for (final Map.Entry<String, Object> entry : rowAttributes.entrySet()) {
			final String name = entry.getKey();
			if (names == null || names.contains(name)) {
				newAttributes.put(name, entry.getValue());
			}
		}
		return newAttributes;
	}

	@Override
	protected List<Map<String, Object>> read(List<String> names, String key, String value) {
		logger.debug("Reading key / value: {} / {}", key, value);
		final List<Map<String, Object>> listAttributes = new ArrayList<>();

		if (key.equals(getIdAttribute())) {
			Map<String,Object> profile = profiles.get(value);
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
