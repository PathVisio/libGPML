package org.pathvisio.model;

import java.awt.Color;

import junit.framework.TestCase;

/**
 * Test for {@link Group}.
 * 
 * @author finterly
 */
public class TestTrial extends TestCase {

	/**
	 * Create a {@link Group} and add to pathway model. Create {@link DataNode} and
	 * add to pathway model. Add DataNode to Group using
	 * {@link Group#addPathwayElement()} and {@link Group#removePathwayElement()}.
	 */
	public static void testGroupAddRemove() {
			
		System.out.println("TEST 1");

		// create a pathway model
		PathwayModel p1 = new PathwayModel();

		// create group
		Group g1 = new Group(null);
		assertNull(g1.getElementId());
		assertNull(g1.getPathwayModel());
		g1.setBorderColor(Color.white);
		p1.addGroup(g1);
		g1.setElementId("tempID");
		// add to pathway model
	}

}
