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
			} else if (attribute instanceof String) {
				return "1".equals(attribute);
			} else if (attribute instanceof Number) {
				return new Integer(1).equals(attribute);
			}
		}
		return null;
	}
}
