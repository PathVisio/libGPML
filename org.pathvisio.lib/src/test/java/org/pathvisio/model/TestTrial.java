package org.pathvisio.model;

import junit.framework.TestCase;

/**
 * Test for random things.
 * 
 * @author finterly
 */
public class TestTrial extends TestCase {

	/**
	 * Temporary place for testing various methods.
	 */
	public static void testThis() {

		Interaction line = new Interaction();
		line.addLinePoint(null, 9, 18);
		line.addLinePoint(null, 4, 16);

		System.out.println(line.getRotatedBounds());
		System.out.println(line.getBounds());


	}

}
