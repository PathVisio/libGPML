package org.pathvisio.model.ref;


import org.pathvisio.model.Shape;
import org.pathvisio.model.graphics.Coordinate;

import junit.framework.TestCase;

/**
 * For testing Citation methods 
 * 
 * @author finterly
 */
public class TestEvidence extends TestCase {

	/**
	 * Test for adding Citation and CitationRef to pathway model
	 */
	public static void testCitation() {
		
		Shape shape = new Shape();
		
		double centerX = shape.getCenterXY().getX();
		
		System.out.println(centerX);
		
		shape.getCenterXY().setX(2);
		
		System.out.println(centerX);
		System.out.println(shape.getCenterXY().getX());


	}
	
	
}
