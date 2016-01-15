/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.oauth.profile.vk;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.OAuthAttributesDefinition;

/**
 * This class defines the attributes of the Vk profile.
 * 
 * @author indvdum (gotoindvdum[at]gmail[dot]com)
 * @since 1.5
 * 
 */
public class VkAttributesDefinition extends OAuthAttributesDefinition {

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
		primary(FIRST_NAME, Converters.stringConverter);
		primary(LAST_NAME, Converters.stringConverter);
		primary(SEX, VkConverters.genderConverter);
		primary(BIRTH_DATE, VkConverters.dateConverter);
		primary(PHOTO_50, Converters.stringConverter);
		primary(PHOTO_100, Converters.stringConverter);
		primary(PHOTO_200_ORIG, Converters.stringConverter);
		primary(PHOTO_200, Converters.stringConverter);
		primary(PHOTO_400_ORIG, Converters.stringConverter);
		primary(PHOTO_MAX, Converters.stringConverter);
		primary(PHOTO_MAX_ORIG, Converters.stringConverter);
		primary(ONLINE, VkConverters.booleanConverter);
		primary(ONLINE_MOBILE, VkConverters.booleanConverter);
		primary(DOMAIN, Converters.stringConverter);
		primary(HAS_MOBILE, VkConverters.booleanConverter);
		primary(MOBILE_PHONE, Converters.stringConverter);
		primary(HOME_PHONE, Converters.stringConverter);
		primary(SKYPE, Converters.stringConverter);
		primary(SITE, Converters.stringConverter);
		primary(CAN_POST, VkConverters.booleanConverter);
		primary(CAN_SEE_ALL_POST, VkConverters.booleanConverter);
		primary(CAN_SEE_AUDIO, VkConverters.booleanConverter);
		primary(CAN_WRITE_PRIVATE_MESSAGE, VkConverters.booleanConverter);
		primary(STATUS, Converters.stringConverter);
		primary(COMMON_COUNT, Converters.integerConverter);
		primary(RELATION, Converters.integerConverter);
	}
}
