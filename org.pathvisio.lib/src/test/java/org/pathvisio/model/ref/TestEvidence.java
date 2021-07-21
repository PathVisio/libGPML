package org.pathvisio.model.ref;


import org.pathvisio.model.Shape;
import org.pathvisio.model.graphics.Coordinate;
import org.pathvisio.model.graphics.RectProperty;

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
		
		Shape shape = new Shape(new RectProperty(new Coordinate(0,0), 0, 0), null, null, 0);
		
		double centerX = shape.getRectProp().getCenterXY().getX();
		
		System.out.println(centerX);
		
		shape.getRectProp().getCenterXY().setX(2);
		
		System.out.println(centerX);
		System.out.println(shape.getRectProp().getCenterXY().getX());


	}
	
	
}
