package org.pac4j.oauth.profile.vk;

import org.pac4j.core.profile.converter.FormattedDateConverter;
import org.pac4j.core.profile.converter.GenderIntegerConverter;
import org.pac4j.oauth.profile.vk.converter.VkBooleanConverter;

/**
 * This class defines all the converters specific to Vk.
 * 
 * @author indvdum (gotoindvdum[at]gmail[dot]com)
 * @since 1.5
 * 
 */
public final class VkConverters {

	public final static FormattedDateConverter dateConverter = new FormattedDateConverter("dd.MM.yyyy");
	public final static GenderIntegerConverter genderConverter = new GenderIntegerConverter(2, 1);
	public final static VkBooleanConverter booleanConverter = new VkBooleanConverter();
}
