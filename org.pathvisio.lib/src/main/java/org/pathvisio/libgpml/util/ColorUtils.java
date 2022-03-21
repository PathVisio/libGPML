/*******************************************************************************
 * PathVisio, a tool for data visualization and analysis using biological pathways
 * Copyright 2006-2022 BiGCaT Bioinformatics, WikiPathways
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.pathvisio.libgpml.util;

import java.awt.Color;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.pathvisio.libgpml.debug.Logger;

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
	 * @param color      the color object.
	 * @param appendHash the boolean, if true appends "#" to beginning of hex
	 *                   string.
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
	 * hexBinary String to {@link Color} object. Color name string matching is case
	 * insensitive.
	 */
	private static final Map<String, String> colorMap;
	static {
		Map<String, String> cMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
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

	/**
	 * Returns true if fill color is equal to null or the alpha value is equal to 0.
	 * 
	 * @return true if fill color is transparent.
	 */
	public static boolean isTransparent(Color color) {
		return color == null || color.getAlpha() == 0;
	}

	/**
	 * Returns a new transparent color for given color with given alpha.
	 * 
	 * @param color the source color.
	 * @param alpha the integer alpha value.
	 * @return new color created from given color and alpha.
	 */
	public static Color makeTransparent(Color color, int alpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}

//	/**
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
