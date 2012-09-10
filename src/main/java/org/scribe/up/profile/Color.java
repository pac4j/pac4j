package org.scribe.up.profile;

import java.io.Serializable;

/**
 * This class is a simple RGB color values holder.
 * <p/>
 * It was introduced in 1.2.0 to replace usage of {@link java.awt.Color}
 * which is a restricted class on Google AppEngine.
 *
 * @author Peter Knego
 * @since 1.2.0
 */
public class Color implements Serializable {

	private int red;
	private int green;
	private int blue;

	public Color(int red, int green, int blue) {
		if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255)
			throw new IllegalArgumentException("Color's red, green or blue values must be between 0 and 255).");
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public int getRed() {
		return red;
	}

	public int getGreen() {
		return green;
	}

	public int getBlue() {
		return blue;
	}

	@Override
	public String toString() {
		return toPaddedHexString(red) + toPaddedHexString(green) + toPaddedHexString(blue);
	}

	private static String toPaddedHexString(int i) {
		// add "0" padding to single-digit hex values
		return i < 16 ? "0" + Integer.toHexString(i).toUpperCase() : Integer.toHexString(i).toUpperCase();
	}
}
