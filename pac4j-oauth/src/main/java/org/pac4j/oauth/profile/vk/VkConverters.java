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
