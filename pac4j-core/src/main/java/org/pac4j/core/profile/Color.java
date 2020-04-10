package org.pac4j.core.profile;

import java.io.Serializable;

import org.pac4j.core.exception.TechnicalException;

/**
 * <p>This class is a simple RGB color values holder.</p>
 * <p>It was introduced in 1.2.0 to replace usage of {@link java.awt.Color} which is a restricted class on Google AppEngine.</p>
 *
 * @author Peter Knego
 * @since 1.2.0
 */
public class Color implements Serializable {

    private static final long serialVersionUID = -28080878626869621L;

    private int red;
    private int green;
    private int blue;

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

    public void setRed(final int red) {
        this.red = red;
    }

    public void setGreen(final int green) {
        this.green = green;
    }

    public void setBlue(final int blue) {
        this.blue = blue;
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
