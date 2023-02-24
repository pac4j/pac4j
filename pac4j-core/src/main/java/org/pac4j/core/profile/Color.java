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

    /**
     * <p>Constructor for Color.</p>
     *
     * @param red a int
     * @param green a int
     * @param blue a int
     */
    public Color(final int red, final int green, final int blue) {
        if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255)
            throw new TechnicalException("Color's red, green or blue values must be between 0 and 255.");
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * <p>Getter for the field <code>red</code>.</p>
     *
     * @return a int
     */
    public int getRed() {
        return this.red;
    }

    /**
     * <p>Getter for the field <code>green</code>.</p>
     *
     * @return a int
     */
    public int getGreen() {
        return this.green;
    }

    /**
     * <p>Getter for the field <code>blue</code>.</p>
     *
     * @return a int
     */
    public int getBlue() {
        return this.blue;
    }

    /**
     * <p>Setter for the field <code>red</code>.</p>
     *
     * @param red a int
     */
    public void setRed(final int red) {
        this.red = red;
    }

    /**
     * <p>Setter for the field <code>green</code>.</p>
     *
     * @param green a int
     */
    public void setGreen(final int green) {
        this.green = green;
    }

    /**
     * <p>Setter for the field <code>blue</code>.</p>
     *
     * @param blue a int
     */
    public void setBlue(final int blue) {
        this.blue = blue;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return toPaddedHexString(this.red) + toPaddedHexString(this.green) + toPaddedHexString(this.blue);
    }

    private String toPaddedHexString(final int i) {
        // add "0" padding to single-digit hex values
        return i < 16 ? "0" + Integer.toHexString(i).toUpperCase() : Integer.toHexString(i).toUpperCase();
    }
}
