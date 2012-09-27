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
import org.scribe.up.profile.JsonObject;
import org.scribe.up.profile.converter.Converters;

/**
 * This class represents a Yahoo address.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooAddress extends JsonObject {
    
    private static final long serialVersionUID = -3178498630494820877L;
    
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
        return getSafeInt(this.id);
    }
    
    public boolean isIdDefined() {
        return this.id != null;
    }
    
    public boolean isCurrent() {
        return getSafeBoolean(this.current);
    }
    
    public boolean isCurrentDefined() {
        return this.current != null;
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
