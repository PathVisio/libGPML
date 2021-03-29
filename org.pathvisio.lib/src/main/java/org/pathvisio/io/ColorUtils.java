package org.pathvisio.io;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.pathvisio.debug.Logger;

public class ColorUtils {

	/**
	 * Converts a {@link Color} object to a hexBinary string.
	 * 
	 * @param color
	 */
	public static String colorToHex(Color color) {
		String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
		return hex;
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
		cMap.put("Gray", Color.decode("808080"));
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
//	 * Converts a string containing either a named color, e.g. "White", or a
//	 * hexBinary number to a {@link Color} object
//	 * 
//	 * @param stringColor
//	 */
//	public static Color stringToColor(String stringColor) {
//		if (COLOR_MAPPINGS.contains(stringColor)) {
//			double[] color = (double[]) RGB_MAPPINGS.get(COLOR_MAPPINGS.indexOf(stringColor));
//			return new Color((int) (255 * color[0]), (int) (255 * color[1]), (int) (255 * color[2]));
//		} else {
//			try {
//				return Color.decode("#"+stringColor);
//			} catch (Exception e) {
//				Logger.log.error("while converting color: " + "Color " + stringColor
//						+ " is not valid, element color is set to black", e);
//			}
//		}
//		return Color.decode("#000000");
//	}
//	public static final List<String> COLOR_MAPPINGS = Arrays
//			.asList(new String[] { "Aqua", "Black", "Blue", "Fuchsia", "Gray", "Green", "Lime", "Maroon", "Navy",
//					"Olive", "Purple", "Red", "Silver", "Teal", "White", "Yellow", "Transparent" });
//
//	public static final List<double[]> RGB_MAPPINGS = Arrays.asList(new double[][] {
//
//			{ 0, 1, 1 }, // Aqua
//			{ 0, 0, 0 }, // Black
//			{ 0, 0, 1 }, // Blue
//			{ 1, 0, 1 }, // Fuchsia
//			{ .5, .5, .5, }, // Gray
//			{ 0, .5, 0 }, // Green
//			{ 0, 1, 0 }, // Lime
//			{ .5, 0, 0 }, // Maroon
//			{ 0, 0, .5 }, // Navy
//			{ .5, .5, 0 }, // Olive
//			{ .5, 0, .5 }, // Purple
//			{ 1, 0, 0 }, // Red
//			{ .75, .75, .75 }, // Silver
//			{ 0, .5, .5 }, // Teal
//			{ 1, 1, 1 }, // White
//			{ 1, 1, 0 }, // Yellow
//			{ 0, 0, 0 } // Transparent (actually irrelevant)
//	});

//	/**
//	 * Prepends character c x-times to the input string to make it length n
//	 * 
//	 * @param s String to pad
//	 * @param n Number of characters of the resulting string
//	 * @param c character to append
//	 * @return string of length n or larger (if given string s > n)
//	 */
//	public static String padding(String s, int n, char c) {
//		while (s.length() < n) {
//			s = c + s;
//		}
//		return s;
//	}
//	
//	/**
//	 * Converts a {@link Color} object to a hexBinary string.
//	 * 
//	 * @param color
//	 */
//	public static String colorToHex(Color color) {
//		
//		String red = padding(Integer.toBinaryString(color.getRed()), 8, '0');
//		String green = padding(Integer.toBinaryString(color.getGreen()), 8, '0');
//		String blue = padding(Integer.toBinaryString(color.getBlue()), 8, '0');
//		String hexBinary = Integer.toHexString(Integer.valueOf(red + green + blue, 2));
//		return padding(hexBinary, 6, '0');
//	}
//	
//	/**
//	 * Converts a string containing either a named color, e.g. "White", or a
//	 * hexBinary number to a {@link Color} object
//	 * 
//	 * @param stringColor
//	 */
//	public static Color stringToColor(String stringColor) {
//		if (COLOR_MAPPINGS.contains(stringColor)) {
//			double[] color = (double[]) RGB_MAPPINGS.get(COLOR_MAPPINGS.indexOf(stringColor));
//			return new Color((int) (255 * color[0]), (int) (255 * color[1]), (int) (255 * color[2]));
//		} else {
//			try {
//				return new Color.decode(stringColor);
//				stringColor = padding(stringColor, 6, '0');
//				int red = Integer.valueOf(stringColor.substring(0, 2), 16);
//				int green = Integer.valueOf(stringColor.substring(2, 4), 16);
//				int blue = Integer.valueOf(stringColor.substring(4, 6), 16);
//				return new Color(red, green, blue);
//			} catch (Exception e) {
//				Logger.log.error("while converting color: " + "Color " + stringColor
//						+ " is not valid, element color is set to black", e);
//			}
//		}
//		return new Color(0, 0, 0);
//	}

}
