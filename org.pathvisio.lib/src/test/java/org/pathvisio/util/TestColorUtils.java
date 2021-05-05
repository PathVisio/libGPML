package org.pathvisio.util;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

public class TestColorUtils extends TestCase {

	public static Color randomMethod(String hex) {
		System.out.println(hex);
		if (hex.contains("#"))
			hex = hex.replace("#", "");
		int r = Integer.valueOf(hex.substring(0, 2), 16);
		int g = Integer.valueOf(hex.substring(2, 4), 16);
		int b = Integer.valueOf(hex.substring(4, 6), 16);
		switch (hex.length()) {
		case 6:
			System.out.println(r);
			System.out.println(g);
			System.out.println(b);
			return new Color(r, g, b);
		case 8:
			int a = Integer.valueOf(hex.substring(6, 8), 16);
			System.out.println(r);
			System.out.println(g);
			System.out.println(b);
			System.out.println(a);
			return new Color(r, g, b, a);
		}
		return null;
	}
	
	
	public static void testColorConverter() {
		
		randomMethod("#ffffff");
		List<String> hexTests = new ArrayList<String>(Arrays.asList("#b4b46419", "#00000000", "#ffffff"));
		List<Color> colorTests = new ArrayList<Color>(
				Arrays.asList(new Color(180, 180, 100, 25), new Color(0, 0, 0, 0),new Color(255, 255, 255, 255)));
		for (int i = 0; i < hexTests.size(); i++) {
			Color color = ColorUtils.hexToColor(hexTests.get(i));
			assertEquals(color.getRed(), colorTests.get(i).getRed());
			assertEquals(color.getGreen(), colorTests.get(i).getGreen());
			assertEquals(color.getBlue(), colorTests.get(i).getBlue());
			assertEquals(color.getAlpha(), colorTests.get(i).getAlpha());
			String hex = ColorUtils.colorToHex(color, true);
			assertEquals(hex, hexTests.get(i));

		}
	}
}
