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
package org.scribe.up.profile.facebook;

import org.scribe.up.profile.AttributeConverter;

/**
 * This class is the converter of a String to a FacebookRelationshipStatus.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class FacebookRelationshipStatusConverter implements AttributeConverter<FacebookRelationshipStatus> {
    
    public FacebookRelationshipStatus convert(Object attribute) {
        if (attribute != null && attribute instanceof String) {
            String v = ((String) attribute).toLowerCase();
            if ("single".equals(v)) {
                return FacebookRelationshipStatus.SINGLE;
            } else if ("in a relationship".equals(v)) {
                return FacebookRelationshipStatus.IN_A_RELATIONSHIP;
            } else if ("engaged".equals(v)) {
                return FacebookRelationshipStatus.ENGAGED;
            } else if ("married".equals(v)) {
                return FacebookRelationshipStatus.MARRIED;
            } else if ("it's complicated".equals(v)) {
                return FacebookRelationshipStatus.ITS_COMPLICATED;
            } else if ("in an open relationship".equals(v)) {
                return FacebookRelationshipStatus.IN_AN_OPEN_RELATIONSHIP;
            } else if ("widowed".equals(v)) {
                return FacebookRelationshipStatus.WIDOWED;
            } else if ("separated".equals(v)) {
                return FacebookRelationshipStatus.SEPARATED;
            } else if ("divorced".equals(v)) {
                return FacebookRelationshipStatus.DIVORCED;
            } else if ("in a civil union".equals(v)) {
                return FacebookRelationshipStatus.IN_A_CIVIL_UNION;
            } else if ("in a domestic partnership".equals(v)) {
                return FacebookRelationshipStatus.IN_A_DOMESTIC_PARTNERSHIP;
            }
        }
        return null;
    }
}
