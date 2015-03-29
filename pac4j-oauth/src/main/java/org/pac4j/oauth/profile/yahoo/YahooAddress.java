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

import java.util.Locale;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.JsonObject;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class represents a Yahoo address.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooAddress extends JsonObject {
    
    private static final long serialVersionUID = 5415315569181241541L;
    
    private Integer id;
    
    private Boolean current;
    
    private Locale country;
    
    private String state;
    
    private String city;
    
    private String postalCode;
    
    private String street;
    
    private String type;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.id = (Integer) JsonHelper.convert(Converters.integerConverter, json, "id");
        this.current = (Boolean) JsonHelper.convert(Converters.booleanConverter, json, "current");
        this.country = (Locale) JsonHelper.convert(Converters.localeConverter, json, "country");
        this.state = (String) JsonHelper.convert(Converters.stringConverter, json, "state");
        this.city = (String) JsonHelper.convert(Converters.stringConverter, json, "city");
        this.postalCode = (String) JsonHelper.convert(Converters.stringConverter, json, "postalCode");
        this.street = (String) JsonHelper.convert(Converters.stringConverter, json, "street");
        this.type = (String) JsonHelper.convert(Converters.stringConverter, json, "type");
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public Boolean getCurrent() {
        return this.current;
    }
    
    public Locale getCountry() {
        return this.country;
    }
    
    public String getState() {
        return this.state;
    }
    
    public String getCity() {
        return this.city;
    }
    
    public String getPostalCode() {
        return this.postalCode;
    }
    
    public String getStreet() {
        return this.street;
    }
    
    public String getType() {
        return this.type;
    }
}
