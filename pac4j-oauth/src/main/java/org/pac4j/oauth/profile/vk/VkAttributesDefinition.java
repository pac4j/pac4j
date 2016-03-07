package org.pac4j.oauth.profile.vk;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.converter.FormattedDateConverter;
import org.pac4j.core.profile.converter.GenderIntegerConverter;
import org.pac4j.oauth.profile.vk.converter.VkBooleanConverter;

import java.util.Arrays;

/**
 * This class defines the attributes of the Vk profile.
 * 
 * @author indvdum (gotoindvdum[at]gmail[dot]com)
 * @since 1.5
 */
public class VkAttributesDefinition extends AttributesDefinition {

	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String SEX = "sex";
	public static final String BIRTH_DATE = "bdate";
	public static final String PHOTO_50 = "photo_50";
	public static final String PHOTO_100 = "photo_100";
	public static final String PHOTO_200_ORIG = "photo_200_orig";
	public static final String PHOTO_200 = "photo_200";
	public static final String PHOTO_400_ORIG = "photo_400_orig";
	public static final String PHOTO_MAX = "photo_max";
	public static final String PHOTO_MAX_ORIG = "photo_max_orig";
	public static final String ONLINE = "online";
	public static final String ONLINE_MOBILE = "online_mobile";
	public static final String DOMAIN = "domain";
	public static final String HAS_MOBILE = "has_mobile";
	public static final String MOBILE_PHONE = "mobile_phone";
	public static final String HOME_PHONE = "home_phone";
	public static final String SKYPE = "skype";
	public static final String SITE = "site";
	public static final String CAN_POST = "can_post";
	public static final String CAN_SEE_ALL_POST = "can_see_all_posts";
	public static final String CAN_SEE_AUDIO = "can_see_audio";
	public static final String CAN_WRITE_PRIVATE_MESSAGE = "can_write_private_message";
	public static final String STATUS = "status";
	public static final String COMMON_COUNT = "common_count";
	public static final String RELATION = "relation";

	public VkAttributesDefinition() {
		Arrays.stream(new String[] {FIRST_NAME, LAST_NAME, PHOTO_50, PHOTO_100, PHOTO_200_ORIG, PHOTO_200, PHOTO_400_ORIG,
				PHOTO_MAX, PHOTO_MAX_ORIG, DOMAIN, MOBILE_PHONE, HOME_PHONE, SKYPE, SITE, STATUS}).forEach(a -> primary(a, Converters.STRING));
		primary(COMMON_COUNT, Converters.INTEGER);
		primary(RELATION, Converters.INTEGER);
		final VkBooleanConverter booleanConverter = new VkBooleanConverter();
		Arrays.stream(new String[] {ONLINE, ONLINE_MOBILE, HAS_MOBILE, CAN_POST, CAN_SEE_ALL_POST, CAN_SEE_AUDIO, CAN_WRITE_PRIVATE_MESSAGE})
				.forEach(a -> primary(a, booleanConverter));
		primary(BIRTH_DATE, new FormattedDateConverter("dd.MM.yyyy"));
		primary(SEX, new GenderIntegerConverter(2, 1));
	}
}
