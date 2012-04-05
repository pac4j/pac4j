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

import java.io.Serializable;
import java.util.Locale;

import org.codehaus.jackson.JsonNode;
import org.scribe.up.profile.JsonObject;
import org.scribe.up.profile.converter.Converters;

/**
 * This class represents an address object for Yahoo.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooAddress extends JsonObject implements Serializable {
    
    private static final long serialVersionUID = 5153219758684696414L;
    
    private Integer id;
    
    private Boolean current;
    
    private Locale country;
    
    private String state;
    
    private String city;
    
    private String postalCode;
    
    private String street;
    
    private String type;
    
    public YahooAddress(Object json) {
        super(json);
    }
    
    @Override
    protected void buildFromJson(JsonNode json) {
        this.id = Converters.integerConverter.convertFromJson(json, "id");
        this.current = Converters.booleanConverter.convertFromJson(json, "current");
        this.country = Converters.localeConverter.convertFromJson(json, "country");
        this.state = Converters.stringConverter.convertFromJson(json, "state");
        this.city = Converters.stringConverter.convertFromJson(json, "city");
        this.postalCode = Converters.stringConverter.convertFromJson(json, "postalCode");
        this.street = Converters.stringConverter.convertFromJson(json, "street");
        this.type = Converters.stringConverter.convertFromJson(json, "type");
    }
    
    public int getId() {
        return id != null ? id : 0;
    }
    
    public boolean isIdDefined() {
        return id != null;
    }
    
    public boolean isCurrent() {
        return current != null ? current : false;
    }
    
    public boolean isCurrentDefined() {
        return current != null;
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
}
