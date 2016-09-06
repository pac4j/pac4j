package org.pac4j.oauth.profile.vk.converter;

import org.pac4j.core.profile.converter.AttributeConverter;

/**
 * @author indvdum (gotoindvdum[at]gmail[dot]com)
 * @since 1.5
 * 
 */
public final class VkBooleanConverter implements AttributeConverter<Boolean> {

	@Override
	public Boolean convert(final Object attribute) {
		if (attribute != null) {
			if (attribute instanceof Boolean) {
				return (Boolean) attribute;
			} 
			if (attribute instanceof String) {
				return "1".equals(attribute);
			} 
			if (attribute instanceof Number) {
				return Integer.valueOf(1).equals(attribute);
			}
		}
		return Boolean.FALSE;
	}
}
