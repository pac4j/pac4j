/*
  Copyright 2012 - 2014 Jerome Leleu

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
package org.pac4j.core.profile;

import java.io.Serializable;

import org.pac4j.core.exception.TechnicalException;

/**
 * This class is a simple RGB color values holder.
 * <p/>
 * It was introduced in 1.2.0 to replace usage of {@link java.awt.Color} which is a restricted class on Google AppEngine.
 * 
 * @author Peter Knego
 * @since 1.2.0
 */
public class Color implements Serializable {
    
    private static final long serialVersionUID = -28080878626869621L;
    
    private final int red;
    private final int green;
    private final int blue;
    
    public Color(final int red, final int green, final int blue) {
        if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255)
            throw new TechnicalException("Color's red, green or blue values must be between 0 and 255.");
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    
    public int getRed() {
        return this.red;
    }
    
    public int getGreen() {
        return this.green;
    }
    
    public int getBlue() {
        return this.blue;
    }
    
    @Override
    public String toString() {
        return toPaddedHexString(this.red) + toPaddedHexString(this.green) + toPaddedHexString(this.blue);
    }
    
    private String toPaddedHexString(final int i) {
        // add "0" padding to single-digit hex values
        return i < 16 ? "0" + Integer.toHexString(i).toUpperCase() : Integer.toHexString(i).toUpperCase();
    }
}
