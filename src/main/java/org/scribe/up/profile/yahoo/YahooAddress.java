/*
  Copyright 2012 Jerome Leleu

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
package org.scribe.up.profile.yahoo;

import java.util.Locale;

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.converter.LocaleConverter;
import org.scribe.up.util.ObjectHelper;

/**
 * This class represents an address object for Yahoo.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooAddress {
    
    private static final LocaleConverter localeConverter = new LocaleConverter();
    
    private int id;
    
    private boolean current;
    
    private Locale country;
    
    private String state;
    
    private String city;
    
    private String postalCode;
    
    private String street;
    
    private String type;
    
    public YahooAddress(JsonNode json) {
        if (json != null) {
            this.id = (Integer) ObjectHelper.getDefaultIfNull(JsonHelper.getNumberValue(json, "id"), new Integer(0));
            this.current = (Boolean) ObjectHelper.getDefaultIfNull(JsonHelper.getBooleanValue(json, "current"),
                                                                   Boolean.FALSE);
            this.country = localeConverter.convert(JsonHelper.getTextValue(json, "country"));
            this.state = JsonHelper.getTextValue(json, "state");
            this.city = JsonHelper.getTextValue(json, "city");
            this.postalCode = JsonHelper.getTextValue(json, "postalCode");
            this.street = JsonHelper.getTextValue(json, "street");
            this.type = JsonHelper.getTextValue(json, "type");
        }
    }
    
    public int getId() {
        return id;
    }
    
    public boolean isCurrent() {
        return current;
    }
    
    public Locale getCountry() {
        return country;
    }
    
    public String getState() {
        return state;
    }
    
    public String getCity() {
        return city;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public String getStreet() {
        return street;
    }
    
    public String getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return "YahooAddress(id:" + id + ",current:" + current + ",country:" + country + ",state:" + state + ",city:"
               + city + ",postalCode:" + postalCode + ",street:" + street + ",type:" + type + ")";
    }
}
