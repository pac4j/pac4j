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
package org.pac4j.oauth.profile.facebook.converter;

import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.oauth.profile.facebook.FacebookRelationshipStatus;

/**
 * This class converts a String into a FacebookRelationshipStatus.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class FacebookRelationshipStatusConverter implements AttributeConverter<FacebookRelationshipStatus> {
    
    public FacebookRelationshipStatus convert(final Object attribute) {
        if (attribute != null && attribute instanceof String) {
            String s = ((String) attribute).toLowerCase();
            s = s.replaceAll("_", " ");
            s = s.replaceAll("'", "");
            if ("single".equals(s)) {
                return FacebookRelationshipStatus.SINGLE;
            } else if ("in a relationship".equals(s)) {
                return FacebookRelationshipStatus.IN_A_RELATIONSHIP;
            } else if ("engaged".equals(s)) {
                return FacebookRelationshipStatus.ENGAGED;
            } else if ("married".equals(s)) {
                return FacebookRelationshipStatus.MARRIED;
            } else if ("its complicated".equals(s)) {
                return FacebookRelationshipStatus.ITS_COMPLICATED;
            } else if ("in an open relationship".equals(s)) {
                return FacebookRelationshipStatus.IN_AN_OPEN_RELATIONSHIP;
            } else if ("widowed".equals(s)) {
                return FacebookRelationshipStatus.WIDOWED;
            } else if ("separated".equals(s)) {
                return FacebookRelationshipStatus.SEPARATED;
            } else if ("divorced".equals(s)) {
                return FacebookRelationshipStatus.DIVORCED;
            } else if ("in a civil union".equals(s)) {
                return FacebookRelationshipStatus.IN_A_CIVIL_UNION;
            } else if ("in a domestic partnership".equals(s)) {
                return FacebookRelationshipStatus.IN_A_DOMESTIC_PARTNERSHIP;
            }
        }
        return null;
    }
}
