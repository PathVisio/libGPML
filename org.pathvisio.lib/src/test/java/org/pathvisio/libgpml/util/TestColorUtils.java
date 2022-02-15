package org.pathvisio.libgpml.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.pathvisio.libgpml.util.ColorUtils;

import junit.framework.TestCase;

/**
 * Tests for ColorUtils class. 
 * 
 * @author finterly
 */
public class TestColorUtils extends TestCase {
	
	public void testHexToColorToHex() {
		// hex strings tests
		List<String> hexTests = new ArrayList<String>(Arrays.asList("#b4b46419", "#00000000", "#ffffff"));
		// color object equivalent of hex string tests
		List<Color> colorTests = new ArrayList<Color>(
				Arrays.asList(new Color(180, 180, 100, 25), new Color(0, 0, 0, 0),new Color(255, 255, 255, 255)));
		for (int i = 0; i < hexTests.size(); i++) {
			// convert hex string to color object
			Color color = ColorUtils.hexToColor(hexTests.get(i));
			// assert converted color object is equivalent to control color 
			assertEquals(color.getRed(), colorTests.get(i).getRed());
			assertEquals(color.getGreen(), colorTests.get(i).getGreen());
			assertEquals(color.getBlue(), colorTests.get(i).getBlue());
			assertEquals(color.getAlpha(), colorTests.get(i).getAlpha());
			//convert color back to hex string
			String hex = ColorUtils.colorToHex(color, true);
			// assert hex string returned is the same
			assertEquals(hex, hexTests.get(i));

		}
	}
	
	public void testStringToColor() {
		// strings tests
		List<String> strTests = new ArrayList<String>(Arrays.asList("white", "Transparent", "blue"));
		// color object equivalent of hex string tests
		List<Color> colorTests = new ArrayList<Color>(
				Arrays.asList(new Color(255, 255, 255, 255), new Color(0, 0, 0, 0),new Color(0, 0, 255, 255)));
		for (int i = 0; i < strTests.size(); i++) {
			// convert hex string to color object
			Color color = ColorUtils.stringToColor(strTests.get(i));
			// assert converted color object is equivalent to control color 
			assertEquals(color.getRed(), colorTests.get(i).getRed());
			assertEquals(color.getGreen(), colorTests.get(i).getGreen());
			assertEquals(color.getBlue(), colorTests.get(i).getBlue());
			assertEquals(color.getAlpha(), colorTests.get(i).getAlpha());
		}
	}
	
	public void testTransparency() {	
		Color color = ColorUtils.hexToColor("00000000");
		Color color2 = ColorUtils.hexToColor("ffffff00");
		assertTrue(ColorUtils.isTransparent(color));
		assertTrue(ColorUtils.isTransparent(color2));
	}
		
	
}
