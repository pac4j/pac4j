package org.pac4j.core.profile.converter;

import org.pac4j.core.profile.Gender;

/**
 * This class converts an Integer to a Gender.
 * 
 * @author indvdum (gotoindvdum[at]gmail[dot]com)
 * @since 1.5
 * 
 */
public final class GenderIntegerConverter implements AttributeConverter<Gender> {

	private final Integer maleValue;

	private final Integer femaleValue;

	public GenderIntegerConverter(final Integer maleText, final Integer femaleText) {
		this.maleValue = maleText;
		this.femaleValue = femaleText;
	}

	@Override
	public Gender convert(final Object attribute) {
		if (attribute != null) {
			if (attribute instanceof Integer) {
				Integer value = (Integer) attribute;
				if (value.equals(this.maleValue)) {
					return Gender.MALE;
				} else if (value.equals(this.femaleValue)) {
					return Gender.FEMALE;
				} else {
					return Gender.UNSPECIFIED;
				}
			} else if (attribute instanceof Gender) {
				return (Gender) attribute;
			}
		}
		return null;
	}
}
