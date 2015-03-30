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
package org.pac4j.oauth.profile.converter;

import java.lang.reflect.Constructor;

import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.oauth.profile.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class converts a XML text into a XML object.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public final class XmlObjectConverter implements AttributeConverter<XmlObject> {
    
    private static final Logger logger = LoggerFactory.getLogger(XmlObjectConverter.class);
    
    private final Class<? extends XmlObject> clazz;
    
    public XmlObjectConverter(final Class<? extends XmlObject> clazz) {
        this.clazz = clazz;
    }
    
    public XmlObject convert(final Object attribute) {
        if (attribute != null && attribute instanceof String) {
            try {
                final Constructor<? extends XmlObject> constructor = this.clazz.getDeclaredConstructor();
                final XmlObject xmlObject = constructor.newInstance();
                xmlObject.buildFrom((String) attribute);
                return xmlObject;
            } catch (final Exception e) {
                logger.error("Cannot build XmlObject : {}", e, this.clazz);
            }
        }
        return null;
    }
}
