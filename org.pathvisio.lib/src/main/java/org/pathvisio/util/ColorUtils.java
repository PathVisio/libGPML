package org.pathvisio.util;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.pathvisio.debug.Logger;

/**
 * This utils class contains methods for converting {@link Color} to hexBinary
 * or color name {@link String}, and vice versa.
 * 
 * @author finterly
 */
public class ColorUtils {

	/**
	 * Converts a {@link Color} object to a hexBinary string.
	 * 
	 * @param color the color object.
	 * @param hash  the boolean, if true appends "#" to beginning of hex string.
	 * @return the resulting hex string.
	 */
	public static String colorToHex(Color color, boolean appendHash) {
		int a = color.getAlpha();
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		if (a == 255) {
			if (appendHash) {
				return String.format("#%02x%02x%02x", r, g, b);
			} else {
				return String.format("%02x%02x%02x", r, g, b);
			}
		} else {
			if (appendHash) {
				return String.format("#%02x%02x%02x%02x", r, g, b, a);
			} else {
				return String.format("%02x%02x%02x%02x", r, g, b, a);
			}
		}
	}

	/**
	 * Converts a hexBinary string to {@link Color} (r,g,b) or (r,g,b,a). If it
	 * can't be converted null is returned.
	 * 
	 * @param hex
	 */
	public static Color hexToColor(String hex) {
		hex = hex.replace("#", "");
		long i = Long.parseLong(hex, 16);
		int r = (int) ((i >> 24) & 0xff);
		int g = (int) ((i >> 16) & 0xff);
		int b = (int) ((i >> 8) & 0xff);
		switch (hex.length()) {
		case 6:
			if (!hex.contains("#"))
				hex = "#" + hex;
			return Color.decode(hex);
		case 8:

			int a = (int) (i & 0xff);
			return new Color(r, g, b, a);
		}
		return null;
	}

	/**
	 * Converts a (gpml) string containing either a named color, e.g. "White", or a
	 * hexBinary number to a {@link Color} object.
	 * 
	 * @param stringColor
	 */
	public static Color stringToColor(String stringColor) {
		if (colorMap.containsKey(stringColor)) {
			return hexToColor(colorMap.get(stringColor));
		} else {
			try {
				return hexToColor(stringColor);
			} catch (Exception e) {
				Logger.log.error("while converting color: " + "Color " + stringColor
						+ " is not valid, element color is set to black", e);
			}
		}
		return Color.decode("#000000"); // default black (as implemented in GPML 2013a)
	}

	/**
	 * Mapping of string gpml:ColorType (older versions of gpml) to a hexBinary
	 * {@link String}. In {@link #stringToColor}, {@link #hexToColor}converts
	 * hexBinary String to {@link Color} object.
	 */
	private static final Map<String, String> colorMap;
	static {
		Map<String, String> cMap = new HashMap<String, String>();
		cMap.put("Aqua", "#00ffff");
		cMap.put("Black", "#000000");
		cMap.put("Blue", "#0000ff");
		cMap.put("Fuchsia", "#ff00ff");
		cMap.put("Gray", "#808080");
		cMap.put("Green", "#008000");
		cMap.put("Lime", "#00ff00");
		cMap.put("Maroon", "#800000");
		cMap.put("Navy", "#000080");
		cMap.put("Olive", "#808000");
		cMap.put("Purple", "#800080");
		cMap.put("Red", "#ff0000");
		cMap.put("Silver", "#c0c0c0");
		cMap.put("Teal", "#008080");
		cMap.put("White", "#ffffff");
		cMap.put("Yellow", "#ffff00");
		cMap.put("Transparent", "#00000000");
		colorMap = Collections.unmodifiableMap(cMap);
	}

//	/**
//	 * Checks if fill color is equal to null or the alpha value is equal to 0.
//	 * 
//	 * @return true if fill color equal to null or alpha value equal to 0, false
//	 *         otherwise.
//	 */
//	public boolean isTransparent() {
//		return fillColor == null || fillColor.getAlpha() == 0;
//	}
//	/**
//	 * TODO: Logic seems weird...
//	 * 
//	 * Sets the alpha component of fillColor to 0 if true, sets the alpha component
//	 * of fillColor to 255 if false.
//	 * 
//	 * @param value the boolean value.
//	 */
//	public void setTransparent(boolean value) {
//		if (isTransparent() != value) {
//			if (fillColor == null) {
//				fillColor = Color.WHITE;
//			}
//			int alpha = value ? 0 : 255;
//			fillColor = new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), alpha);
//		}
//	}

}
