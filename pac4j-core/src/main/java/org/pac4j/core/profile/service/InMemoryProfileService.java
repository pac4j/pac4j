package org.pac4j.core.profile.service;

import org.pac4j.core.profile.CommonProfile;
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
	
	public InMemoryProfileService() {
		 this(new HashMap<String,Map<String,Object>>());
	}
	
	public InMemoryProfileService(Map<String,Map<String,Object>> profiles) {
		 this.profiles = profiles;
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
			Map<String,Object> profile = profiles.get(key);
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
