package org.pathvisio.util;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.pathvisio.debug.Logger;

/**
 * This utils class for converting color.
 * 
 * @author finterly
 */
public class ColorUtils {

	/**
	 * Converts a {@link Color} object to a hexBinary string.
	 * 
	 * @param color
	 */
	public static String colorToHex(Color color) {
		if (color.getAlpha() == 255) {
			return String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
		} else {
			return String.format("#%02x%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue(),
					color.getAlpha());
		}
	}

	/**
	 * Converts a string containing either a named color, e.g. "White", or a
	 * hexBinary number to a {@link Color} object.
	 * 
	 * @param stringColor
	 */
	public static Color stringToColor(String stringColor) {
		if (colorMap.containsKey(stringColor)) {
			return colorMap.get(stringColor);
		} else {
			try {
				return Color.decode("#" + stringColor);
			} catch (Exception e) {
				Logger.log.error("while converting color: " + "Color " + stringColor
						+ " is not valid, element color is set to black", e);
			}
		}
		return Color.decode("#000000");
	}

	/**
	 * Mapping of string gpml:ColorType to a {@link Color} object. gpml:ColorType
	 * uses string color in older versions of gpml.
	 */
	private static final Map<String, Color> colorMap;
	static {
		Map<String, Color> cMap = new HashMap<String, Color>();
		cMap.put("Aqua", Color.decode("#00ffff"));
		cMap.put("Black", Color.decode("#000000"));
		cMap.put("Blue", Color.decode("#0000ff"));
		cMap.put("Fuchsia", Color.decode("#ff00ff"));
		cMap.put("Gray", Color.decode("#808080"));
		cMap.put("Green", Color.decode("#008000"));
		cMap.put("Lime", Color.decode("#00ff00"));
		cMap.put("Maroon", Color.decode("#800000"));
		cMap.put("Navy", Color.decode("#000080"));
		cMap.put("Olive", Color.decode("#808000"));
		cMap.put("Purple", Color.decode("#800080"));
		cMap.put("Red", Color.decode("#ff0000"));
		cMap.put("Silver", Color.decode("#c0c0c0"));
		cMap.put("Teal", Color.decode("#008080"));
		cMap.put("White", Color.decode("#ffffff"));
		cMap.put("Yellow", Color.decode("#ffff00"));
		cMap.put("Transparent", Color.decode("#00000000")); // TODO
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
