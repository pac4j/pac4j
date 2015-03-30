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
package org.pac4j.oauth.profile.yahoo;

import org.pac4j.core.profile.converter.FormattedDateConverter;
import org.pac4j.core.profile.converter.GenderConverter;
import org.pac4j.oauth.profile.converter.JsonListConverter;
import org.pac4j.oauth.profile.converter.JsonObjectConverter;

/**
 * This class defines all the converters specific to Yahoo.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooConverters {
    
    public final static JsonListConverter listAddressConverter = new JsonListConverter(YahooAddress.class);
    
    public final static FormattedDateConverter birthdateConverter = new FormattedDateConverter("MM/dd");
    
    public final static FormattedDateConverter dateConverter = new FormattedDateConverter("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    public final static JsonListConverter listDisclosureConverter = new JsonListConverter(YahooDisclosure.class);
    
    public final static JsonListConverter listEmailConverter = new JsonListConverter(YahooEmail.class);
    
    public final static GenderConverter genderConverter = new GenderConverter("m", "f");
    
    public final static JsonObjectConverter imageConverter = new JsonObjectConverter(YahooImage.class);
    
    public final static JsonListConverter listInterestConverter = new JsonListConverter(YahooInterest.class);
}
